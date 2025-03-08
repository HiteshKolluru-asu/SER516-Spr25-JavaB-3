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
      resultMessage: '',
      chartInstance: null
    };
  },
  methods: {
    async analyzeRepo() {
      if (!this.repoUrl.trim()) {
        alert('Please enter a GitHub repository URL.');
        return;
      }

      const apiUrl = `http://localhost:8080/api/defects/repo?url=${encodeURIComponent(this.repoUrl)}`;
      try {
        const response = await fetch(apiUrl);
        const defectCount = await response.text();

        // If it's not a numeric value, show an error
        if (isNaN(defectCount)) {
          this.resultMessage = `<p style="color: red;">Error: ${defectCount}</p>`;
        } else {
          // Create result message including when count is zero
          const count = Number(defectCount);
          let statusMessage = '';

          if (count === 0) {
            statusMessage = '<p style="color: green;">✓ No defects found in this repository!</p>';
          } else if (count < 200) {
            statusMessage = `<p style="color: orange;">Found ${count} defects</p>`;
          } else {
            statusMessage = `<p style="color: red;">Warning: High defect count (${count})</p>`;
          }

          this.resultMessage = `

            <h3>Defect Density: ${count}</h3>
            ${statusMessage}
            <div id="chart-container" style="width: 100%; min-height: 200px;">
              <canvas id="defectDensityChart" width="400" height="200"></canvas>
            </div>

          `;

          this.saveMetric(this.repoUrl, count);

          // Allow DOM to update
          setTimeout(() => {
            this.renderChart();
          }, 100);
        }
      } catch (error) {
        this.resultMessage = `<p style="color: red;">Failed to fetch data: ${error}</p>`;
      }
    },

    // Saving the defect count in localStorage, keyed by repository URL
    saveMetric(url, count) {
      let defectMetrics = JSON.parse(localStorage.getItem('defectMetrics')) || {};
      let entries = defectMetrics[url] || [];

      entries.push({
        time: new Date().toLocaleString(),
        count
      });

      if (entries.length > 5) {
        entries.shift();
      }
      defectMetrics[url] = entries;
      localStorage.setItem('defectMetrics', JSON.stringify(defectMetrics));
    },

    renderChart() {
      // Make sure to clean up any existing chart instance
      if (this.chartInstance) {
        this.chartInstance.destroy();
        this.chartInstance = null;
      }

      const stored = JSON.parse(localStorage.getItem('defectMetrics')) || {};
      const entries = stored[this.repoUrl] || [];

      // If no entries, create a default entry with the current time and 0 count
      if (!entries.length) {
        entries.push({
          time: new Date().toLocaleString(),
          count: 0
        });
      }

      const rawLabels = entries.map(e => e.time);
      const rawData = entries.map(e => e.count);

      const thresholdValue = 200;

      const labels = ['', ...rawLabels, ''];
      const barData = [null, ...rawData, null];
      const lineData = labels.map(() => thresholdValue);

      const canvas = document.getElementById('defectDensityChart');
      if (!canvas) {
        console.error('Canvas element not found');
        return;
      }

      try {
        const ctx = canvas.getContext('2d');
        const maxValue = Math.max(250, ...rawData) * 1.2;

        this.chartInstance = new Chart(ctx, {
          type: 'bar',
          data: {
            labels,
            datasets: [
              {
                label: 'Defect Count',
                data: barData,
                backgroundColor: function(context) {
                  if (!context.raw && context.raw !== 0) return 'rgba(75, 192, 192, 0.7)';
                  const value = context.raw;
                  if (value === 0) return 'rgba(0, 200, 0, 0.7)';
                  return value < 200 ? 'rgba(75, 192, 192, 0.7)' : 'rgba(255, 99, 132, 0.7)';
                },
                borderColor: function(context) {
                  if (!context.raw && context.raw !== 0) return 'rgba(75, 192, 192, 1)';
                  const value = context.raw;
                  if (value === 0) return 'rgba(0, 200, 0, 1)';
                  return value < 200 ? 'rgba(75, 192, 192, 1)' : 'rgba(255, 99, 132, 1)';
                },
                borderWidth: 1,
                borderRadius: 8
              },
              {
                label: 'Critical Defect Threshold',
                data: lineData,
                type: 'line',
                borderColor: 'red',
                borderDash: [5, 5],
                borderWidth: 2,
                fill: false,
                pointRadius: 0,
                spanGaps: true
              }
            ]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              x: {
                offset: true
              },
              y: {
                beginAtZero: true,
                suggestedMax: maxValue
              }
            },
            plugins: {
              tooltip: {
                callbacks: {
                  title: function(tooltipItems) {
                    return tooltipItems[0].label || 'No date';
                  },
                  label: function(context) {
                    if (context.dataset.label === 'Defect Count') {
                      const value = context.raw;
                      if (value === null) return '';
                      return value === 0 ? 'No defects' : `${value} defects`;
                    }
                    return `Threshold: ${context.raw}`;
                  }
                }
              }
            }
          }
        });
      } catch (error) {
        console.error('Error creating chart:', error);
        this.resultMessage += `<p style="color: red;">Error creating chart: ${error.message}</p>`;
      }
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
  overflow: hidden;
}

.container {
  background: rgba(255, 255, 255, 0.98);
  padding: 50px;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.25);
  max-width: 700px;
  width: 85%;
  max-height: 80vh;
  overflow-y: auto;
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