<template>
  <div class="defect-density-page">
    <div class="container">
      <h1>Defect Density Analysis</h1>
      <p>Enter a GitHub Repository Link</p>

      <!-- Two-way binding for the repo URL -->
      <input
        type="text"
        v-model="repoUrl"
        placeholder="https://github.com/owner/repository"
      />
      <button @click="analyzeRepo">Analyze</button>

      <!-- Results display (hidden until we have something to show) -->
      <div
        id="result"
        v-html="resultMessage"
        v-show="resultMessage !== ''"
      ></div>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';

export default {
  name: 'DefectDensityAnalysis',
  data() {
    return {
      repoUrl: '',
      resultMessage: '',      // Holds the HTML for the result container
      chartInstance: null     // We'll store our Chart.js instance here
    };
  },
  methods: {
    // Primary function: calls the API, parses the result, updates UI
    async analyzeRepo() {
      if (!this.repoUrl.trim()) {
        alert('Please enter a GitHub repository URL.');
        return;
      }

      const apiUrl = `http://localhost:8080/api/defects/repo?url=${encodeURIComponent(this.repoUrl)}`;
      try {
        const response = await fetch(apiUrl);
        const defectCount = await response.text(); // Plain text response

        // If it's not a numeric value, show an error
        if (isNaN(defectCount)) {
          this.resultMessage = `<p style="color: red;">Error: ${defectCount}</p>`;
        } else {
          this.resultMessage = `
            <h3>Defect Count: ${defectCount}</h3>
            <canvas id="defectDensityChart" width="400" height="200"></canvas>
          `;

          // Store the numeric count in localStorage (keyed by repoUrl)
          this.saveMetric(this.repoUrl, Number(defectCount));

          // Wait for the DOM to update, then render the chart
          this.$nextTick(() => {
            this.renderChart();
          });
        }
      } catch (error) {
        this.resultMessage = `<p style="color: red;">Failed to fetch data: ${error}</p>`;
      }
    },

    // Save the defect count in localStorage, keyed by repository URL
    saveMetric(url, count) {
      // "defectMetrics" in localStorage: { [repoUrl]: [{ time, count }, ...], ... }
      let defectMetrics = JSON.parse(localStorage.getItem('defectMetrics')) || {};

      // Get existing entries for this URL (or start fresh)
      let entries = defectMetrics[url] || [];

      // Add the new record
      entries.push({
        time: new Date().toLocaleString(),
        count
      });

      // Keep only the last 5
      if (entries.length > 5) {
        entries.shift();
      }

      // Save back
      defectMetrics[url] = entries;
      localStorage.setItem('defectMetrics', JSON.stringify(defectMetrics));
    },

    // Read the stored counts from localStorage and plot them in a bar chart
    renderChart() {
      // Destroy any existing chart to avoid duplicates
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      // Read from localStorage
      const stored = JSON.parse(localStorage.getItem('defectMetrics')) || {};
      const entries = stored[this.repoUrl] || [];

      // If no data for this URL yet, nothing to chart
      if (!entries.length) return;

      // Extract raw labels (timestamps) and data (defect counts)
      const rawLabels = entries.map(e => e.time);
      const rawData = entries.map(e => e.count);

      // Define your threshold
      const thresholdValue = 200; // Adjust as needed

      // --- KEY TRICK ---
      // Insert an empty label at the beginning and end so the line extends beyond the bars
      // The bar dataset uses 'null' for these two extra points so no extra bars appear,
      // while the threshold line uses the thresholdValue for the entire array length.
      const labels = ['', ...rawLabels, '']; // empty label on each side
      const barData = [null, ...rawData, null];
      const lineData = labels.map(() => thresholdValue); // same length as labels

      const canvas = document.getElementById('defectDensityChart');
      if (!canvas) return;

      const ctx = canvas.getContext('2d');

      // Create the chart
      this.chartInstance = new Chart(ctx, {
        type: 'bar', // base chart type
        data: {
          labels,
          datasets: [
            {
              // 1) Bar dataset for defect counts
              label: 'Defect Count',
              data: barData,
              backgroundColor: 'rgba(75, 192, 192, 0.7)',
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 1,
              borderRadius: 8
            },
            {
              // 2) Line dataset for the threshold
              label: 'Critical Defect Threshold',
              data: lineData,
              type: 'line',
              borderColor: 'red',
              borderDash: [5, 5],   // dotted line
              borderWidth: 2,
              fill: false,
              pointRadius: 0,       // no points
              spanGaps: true        // ensure the line spans any null values
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            x: {
              // offset: true adds some space on the edges of the x-axis
              // so the bars/line don't sit exactly at the chart boundary.
              offset: true
            },
            y: {
              beginAtZero: true
              // Optionally ensure the threshold is visible if it's high:
              // suggestedMax: thresholdValue + 50
            }
          }
        }
      });
    }


  }
};
</script>

<style scoped>
.defect-density-page {
  font-family: 'Roboto', Arial, sans-serif;
  background: #ffffff;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 700px;
  margin: 0 auto;
  border-radius: 18px;
  text-align: center;
  position: relative;
  overflow: hidden; /* Prevent whole page scrolling */
}

.container {
  background: rgba(255, 255, 255, 0.98);
  padding: 50px;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.25);
  max-width: 700px;
  width: 85%;
  max-height: 80vh; /* Limit height */
  overflow-y: auto; /* Scroll inside container if needed */
}

h1 {
  font-size: 38px;
  color: #222;
  margin-bottom: 30px;
  font-weight: bold;
}

p {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
}

input {
  width: 85%;
  padding: 14px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 18px;
}

button {
  width: 85%;
  padding: 16px;
  margin-top: 25px;
  border-radius: 10px;
  border: none;
  background-color: #004aad;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.2s;
  font-size: 20px;
}

button:hover {
  background-color: #003d80;
  transform: scale(1.07);
}

#result {
  margin-top: 20px;
  padding: 15px;
  border-radius: 8px;
  background: #f9f9f9;
  font-size: 18px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  text-align: center;
  display: block;
}
</style>
