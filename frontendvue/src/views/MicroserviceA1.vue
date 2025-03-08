<template>
  <div class="afferent-efferent-page">
    <div class="container">
      <h1>Afferent & Efferent Coupling Analysis</h1>

      <div class="file-input-container">
        <input type="file" @change="handleFileUpload" accept=".zip" />
      </div>
      <button @click="analyzeFile">Analyze</button>

      <!-- Display Analysis Results -->
      <div v-if="resultVisible" class="result-box">
        <h3>Analysis Results:</h3>
        <p><strong>File:</strong> {{ fileName }}</p>

        <!-- Afferent Coupling Results -->
        <h4>Afferent Coupling</h4>
        <div class="data-summary">
          <p v-if="Object.keys(afferentData).length > 10">
            <strong>{{ Object.keys(afferentData).length }}</strong> classes found
          </p>
          <details v-if="Object.keys(afferentData).length > 10">
            <summary>Show detailed data</summary>
            <ul>
              <li v-for="(count, className) in afferentData" :key="className">
                {{ className }}: {{ count }}
              </li>
            </ul>
          </details>
          <ul v-else>
            <li v-for="(count, className) in afferentData" :key="className">
              {{ className }}: {{ count }}
            </li>
          </ul>
        </div>
        <div :class="['chart-container', {'large-chart': isLargeDataset(afferentData)}]">
          <canvas id="afferentChart"></canvas>
        </div>

        <!-- Efferent Coupling Results -->
        <h4>Efferent Coupling</h4>
        <div class="data-summary">
          <p v-if="Object.keys(efferentData).length > 10">
            <strong>{{ Object.keys(efferentData).length }}</strong> classes found
          </p>
          <details v-if="Object.keys(efferentData).length > 10">
            <summary>Show detailed data</summary>
            <ul>
              <li v-for="(count, className) in efferentData" :key="className">
                {{ className }}: {{ count }}
              </li>
            </ul>
          </details>
          <ul v-else>
            <li v-for="(count, className) in efferentData" :key="className">
              {{ className }}: {{ count }}
            </li>
          </ul>
        </div>
        <div :class="['chart-container', {'large-chart': isLargeDataset(efferentData)}]">
          <canvas id="efferentChart"></canvas>
        </div>

        <!-- Instability Result -->
        <h4>Instability Metric</h4>
        <p><strong>Current Value:</strong> {{ instabilityResult !== null ? instabilityResult.toFixed(4) : "N/A" }}</p>
        <div class="chart-container">
          <canvas id="instabilityChart"></canvas>
        </div>

        <!-- Chart Legend/Filter -->
        <div v-if="showFilterControls" class="chart-controls">
          <h4>Chart Controls</h4>
          <div class="filter-controls">
            <button @click="toggleAllDatasets(true)" class="control-button">Show All</button>
            <button @click="toggleAllDatasets(false)" class="control-button">Hide All</button>
            <input
              v-model="searchFilter"
              placeholder="Filter classes..."
              class="filter-input"
              @input="applyFilter"
            />
          </div>
          <div class="legend-container" v-if="legendItems.length > 0">
            <div
              v-for="item in filteredLegendItems"
              :key="item.label"
              class="legend-item"
              @click="toggleDataset(item.label)"
            >
              <span
                class="legend-color"
                :style="{backgroundColor: item.color, opacity: item.visible ? 1 : 0.3}"
              ></span>
              <span :style="{opacity: item.visible ? 1 : 0.5}">{{ item.label }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import Chart from "chart.js/auto";

export default {
  name: "MicroserviceA1",

  setup() {
    const selectedFile = ref(null);
    const resultVisible = ref(false);
    const fileName = ref("");
    const afferentData = ref({});
    const efferentData = ref({});
    const instabilityResult = ref(null);
    const showFilterControls = ref(false);
    const legendItems = ref([]);
    const searchFilter = ref("");

    let afferentChartInstance = null;
    let efferentChartInstance = null;
    let instabilityChartInstance = null;

    const filteredLegendItems = computed(() => {
      if (!searchFilter.value) return legendItems.value;
      return legendItems.value.filter(item =>
        item.label.toLowerCase().includes(searchFilter.value.toLowerCase())
      );
    });

    const isLargeDataset = (data) => {
      return Object.keys(data).length > 10;
    };

    onMounted(() => {
      // Initialize chart instances as null
    });

    const toggleDataset = (label) => {
      // Find the legend item
      const item = legendItems.value.find(i => i.label === label);
      if (item) {
        item.visible = !item.visible;

        // Update both charts
        if (afferentChartInstance) {
          const datasetIndex = afferentChartInstance.data.datasets.findIndex(d => d.label === label);
          if (datasetIndex > -1) {
            afferentChartInstance.setDatasetVisibility(datasetIndex, item.visible);
            afferentChartInstance.update();
          }
        }

        if (efferentChartInstance) {
          const datasetIndex = efferentChartInstance.data.datasets.findIndex(d => d.label === label);
          if (datasetIndex > -1) {
            efferentChartInstance.setDatasetVisibility(datasetIndex, item.visible);
            efferentChartInstance.update();
          }
        }
      }
    };

    const toggleAllDatasets = (visible) => {
      // Update all legend items
      legendItems.value.forEach(item => {
        item.visible = visible;
      });

      // Update both charts
      if (afferentChartInstance) {
        afferentChartInstance.data.datasets.forEach((dataset, i) => {
          afferentChartInstance.setDatasetVisibility(i, visible);
        });
        afferentChartInstance.update();
      }

      if (efferentChartInstance) {
        efferentChartInstance.data.datasets.forEach((dataset, i) => {
          efferentChartInstance.setDatasetVisibility(i, visible);
        });
        efferentChartInstance.update();
      }
    };

    const applyFilter = () => {
      if (afferentChartInstance) {
        afferentChartInstance.data.datasets.forEach((dataset, i) => {
          const shouldShow = !searchFilter.value || dataset.label.toLowerCase().includes(searchFilter.value.toLowerCase());
          afferentChartInstance.setDatasetVisibility(i, shouldShow);
        });
        afferentChartInstance.update();
      }

      if (efferentChartInstance) {
        efferentChartInstance.data.datasets.forEach((dataset, i) => {
          const shouldShow = !searchFilter.value || dataset.label.toLowerCase().includes(searchFilter.value.toLowerCase());
          efferentChartInstance.setDatasetVisibility(i, shouldShow);
        });
        efferentChartInstance.update();
      }
    };

    const handleFileUpload = (event) => {
      selectedFile.value = event.target.files[0];
    };

    const analyzeFile = async () => {
      if (!selectedFile.value) {
        alert("Please select a zip file.");
        return;
      }

      fileName.value = selectedFile.value.name;
      resultVisible.value = true;

      const formData = new FormData();
      formData.append("file", selectedFile.value);

      try {
        const apiBaseUrl = "http://localhost:8080/api";
        const afferentApiUrl = `${apiBaseUrl}/afferent-coupling/upload`;
        const efferentApiUrl = `${apiBaseUrl}/efferent-coupling/upload`;

        const [afferentResponse, efferentResponse] = await Promise.all([
          fetch(afferentApiUrl, { method: "POST", body: formData }).then((res) => res.json()),
          fetch(efferentApiUrl, { method: "POST", body: formData }).then((res) => res.json()),
        ]);

        console.log("API Responses:", { afferentResponse, efferentResponse });

        // Save and update metrics
        saveMetricsAfferent(afferentResponse, fileName.value);
        saveMetricsEfferent(efferentResponse, fileName.value);

        afferentData.value = afferentResponse; // Display Afferent Data
        efferentData.value = efferentResponse; // Display Efferent Data

        // Show filter controls if there are many classes
        showFilterControls.value = isLargeDataset(afferentResponse) || isLargeDataset(efferentResponse);

        // Calculate instability
        const instabilityValue = calculateInstability(fileName.value);
        if (instabilityValue !== null) {
          saveMetricsInstability(instabilityValue, fileName.value);
          instabilityResult.value = instabilityValue;
        }

        // Render charts after data is ready
        setTimeout(() => {
          renderAfferentChart(fileName.value);
          renderEfferentChart(fileName.value);
          renderInstabilityChart(fileName.value);
        }, 100);
      } catch (error) {
        console.error("Error calling API:", error);
      }
    };

    // Function to calculate Instability Metric
    const calculateInstability = (fileName) => {
      const afferentMetrics = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
      const efferentMetrics = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};

      if (!afferentMetrics[fileName] || !efferentMetrics[fileName]) {
        alert("Both afferent and efferent coupling data are required to calculate instability.");
        return null;
      }

      const latestAfferent = afferentMetrics[fileName][afferentMetrics[fileName].length - 1].couplingData;
      const latestEfferent = efferentMetrics[fileName][efferentMetrics[fileName].length - 1].couplingData;

      const Ca = Object.values(latestAfferent).reduce((sum, val) => sum + val, 0);
      const Ce = Object.values(latestEfferent).reduce((sum, val) => sum + val, 0);

      if (Ca + Ce === 0) {
        alert("Cannot calculate instability metric when both afferent and efferent coupling are zero.");
        return null;
      }

      return Ce / (Ca + Ce);
    };

    // Save Afferent Metrics
    const saveMetricsAfferent = (data, fileName) => {
      let allAfferent = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
      if (!allAfferent[fileName]) {
        allAfferent[fileName] = [];
      }
      allAfferent[fileName].push({
        timestamp: new Date().toLocaleString(),
        fileName,
        couplingData: data,
      });
      if (allAfferent[fileName].length > 5) {
        allAfferent[fileName].shift();
      }
      localStorage.setItem("afferentMetricsHistory", JSON.stringify(allAfferent));
    };

    // Save Efferent Metrics
    const saveMetricsEfferent = (data, fileName) => {
      let allEfferent = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};
      if (!allEfferent[fileName]) {
        allEfferent[fileName] = [];
      }
      allEfferent[fileName].push({
        timestamp: new Date().toLocaleString(),
        fileName,
        couplingData: data,
      });
      if (allEfferent[fileName].length > 5) {
        allEfferent[fileName].shift();
      }
      localStorage.setItem("efferentMetricsHistory", JSON.stringify(allEfferent));
    };

    // Save Instability Metrics
    const saveMetricsInstability = (instabilityValue, fileName) => {
      let allInstability = JSON.parse(localStorage.getItem("instabilityMetricsHistory")) || {};

      if (!allInstability[fileName]) {
        allInstability[fileName] = [];
      }

      allInstability[fileName].push({
        timestamp: new Date().toLocaleString(),
        fileName,
        instability: instabilityValue,
      });

      if (allInstability[fileName].length > 5) {
        allInstability[fileName].shift();
      }

      localStorage.setItem("instabilityMetricsHistory", JSON.stringify(allInstability));
    };
    
    const renderAfferentChart = (fileName) => {
      const allAfferent = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
      const history = allAfferent[fileName];

      if (!history || history.length === 0) {
        console.warn("No afferent coupling data found for this file.");
        return;
      }

      const allClassNames = new Set();
      for (let snapshot of history) {
        for (let className of Object.keys(snapshot.couplingData)) {
          allClassNames.add(className);
        }
      }

      const classNamesArray = [...allClassNames];
      const timestamps = history.map(h => h.timestamp);

      // Update legend items
      const newLegendItems = classNamesArray.map((className, index) => ({
        label: className,
        color: getColorByIndex(index),
        visible: index < 10 // Only show first 10 by default if there are many
      }));

      legendItems.value = newLegendItems;

      const datasets = classNamesArray.map((className, index) => ({
        label: className,
        data: history.map(snapshot => snapshot.couplingData[className] || 0),
        borderColor: getColorByIndex(index),
        backgroundColor: getColorByIndex(index) + '20', // 20 is hex for 12% opacity
        pointBackgroundColor: getColorByIndex(index),
        pointRadius: classNamesArray.length > 20 ? 3 : 5, // Smaller points for large datasets
        pointHoverRadius: classNamesArray.length > 20 ? 5 : 7,
        tension: 0.1,
        borderWidth: classNamesArray.length > 20 ? 1 : 2,
        hidden: index >= 10 && classNamesArray.length > 15 // Hide datasets beyond first 10 if there are many
      }));

      const ctx = document.getElementById("afferentChart");
      if (!ctx) {
        console.error("Afferent chart canvas not found");
        return;
      }

      if (afferentChartInstance) {
        afferentChartInstance.destroy();
      }

      afferentChartInstance = new Chart(ctx, {
        type: "line",
        data: {
          labels: timestamps,
          datasets: datasets,
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            axis: 'x',
            intersect: true
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: { stepSize: 1 },
              title: { display: true, text: "Coupling Value" }
            },
            x: {
              title: { display: true, text: "Time" }
            }
          },
          plugins: {
            legend: {
              display: classNamesArray.length <= 10, // Only show legend for small datasets
              position: 'top'
            },
            tooltip: {
              enabled: true,
              mode: 'nearest',
              intersect: true,
              callbacks: {
                title: function(tooltipItems) {
                  if (tooltipItems.length > 0) {
                    return tooltipItems[0].label;
                  }
                  return '';
                },
                label: function(context) {
                  return `${context.dataset.label}: ${context.raw}`;
                }
              }
            }
          }
        }
      });
    };

    const renderEfferentChart = (fileName) => {
      const allEfferent = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};
      const history = allEfferent[fileName];

      if (!history || history.length === 0) {
        console.warn("No efferent coupling data found for this file.");
        return;
      }

      const allClassNames = new Set();
      for (let snapshot of history) {
        for (let className of Object.keys(snapshot.couplingData)) {
          allClassNames.add(className);
        }
      }

      const classNamesArray = [...allClassNames];
      const timestamps = history.map(h => h.timestamp);

      // Use same legend items as afferent chart if possible
      const existingLabels = legendItems.value.map(item => item.label);

      classNamesArray.forEach((className, index) => {
        if (!existingLabels.includes(className)) {
          legendItems.value.push({
            label: className,
            color: getColorByIndex(index),
            visible: index < 10 // Only show first 10 by default if there are many
          });
        }
      });

      const datasets = classNamesArray.map((className, index) => ({
        label: className,
        data: history.map(snapshot => snapshot.couplingData[className] || 0),
        borderColor: getColorByIndex(index),
        backgroundColor: getColorByIndex(index) + '20', // 20 is hex for 12% opacity
        pointBackgroundColor: getColorByIndex(index),
        pointRadius: classNamesArray.length > 20 ? 3 : 5, // Smaller points for large datasets
        pointHoverRadius: classNamesArray.length > 20 ? 5 : 7,
        tension: 0.1,
        borderWidth: classNamesArray.length > 20 ? 1 : 2,
        hidden: index >= 10 && classNamesArray.length > 15 // Hide datasets beyond first 10 if there are many
      }));

      const ctx = document.getElementById("efferentChart");
      if (!ctx) {
        console.error("Efferent chart canvas not found");
        return;
      }

      if (efferentChartInstance) {
        efferentChartInstance.destroy();
      }

      efferentChartInstance = new Chart(ctx, {
        type: "line",
        data: {
          labels: timestamps,
          datasets: datasets,
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            axis: 'x',
            intersect: true
          },
          scales: {
            y: {
              beginAtZero: true,
              ticks: { stepSize: 1 },
              title: { display: true, text: "Coupling Value" }
            },
            x: {
              title: { display: true, text: "Time" }
            }
          },
          plugins: {
            legend: {
              display: classNamesArray.length <= 10, // Only show legend for small datasets
              position: 'top'
            },
            tooltip: {
              enabled: true,
              mode: 'nearest',
              intersect: true,
              callbacks: {
                title: function(tooltipItems) {
                  if (tooltipItems.length > 0) {
                    return tooltipItems[0].label;
                  }
                  return '';
                },
                label: function(context) {
                  return `${context.dataset.label}: ${context.raw}`;
                }
              }
            }
          }
        }
      });
    };

    const getColorByIndex = (index) => {
      const palette = [
        "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0",
        "#9966FF", "#FF9F40", "#00A8A8", "#A8A800",
        "#A800A8", "#0080FF", "#FF8000", "#CC6677",
        "#88CCEE", "#DDCC77", "#44AA99", "#882255",
        "#332288", "#117733", "#999933", "#661100",
        "#DC3545", "#28A745", "#FFC107", "#17A2B8"
      ];
      return palette[index % palette.length];
    };

    return {
      selectedFile,
      resultVisible,
      fileName,
      afferentData,
      efferentData,
      instabilityResult,
      showFilterControls,
      legendItems,
      filteredLegendItems,
      searchFilter,
      isLargeDataset,
      handleFileUpload,
      analyzeFile,
      toggleDataset,
      toggleAllDatasets,
      applyFilter
    };
  },
};
</script>

<style scoped>
.afferent-efferent-page {
  font-family: "Roboto", Arial, sans-serif;
  background-color: #fdfdfd;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  text-align: center;
}

.container {
  background: rgba(255, 255, 255, 0.98);
  padding: 50px;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.25);
  max-width: 900px;
  width: 90%;
}

h1 {
  font-size: 38px;
  color: #222;
  margin-bottom: 30px;
  font-weight: bold;
}

button {
  width: 85%;
  padding: 16px;
  margin-top: 25px;
  border-radius: 10px;
  border: none;
  background-color: #004AAD;
  color: white;
  font-weight: bold;
  cursor: pointer;
  font-size: 20px;
}

button:hover {
  background-color: #003D80;
  transform: scale(1.07);
}

.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
  margin: 20px 0;
}

.large-chart {
  height: 500px; /* Taller chart for datasets with many classes */
}

.result-box {
  margin-top: 30px;
  text-align: left;
}

ul {
  margin-bottom: 20px;
  max-height: 200px;
  overflow-y: auto;
}

.chart-controls {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.filter-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  align-items: center;
}

.control-button {
  width: auto;
  padding: 8px 15px;
  margin: 0;
  font-size: 14px;
}

.filter-input {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.legend-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 5px 10px;
  background-color: #ffffff;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.legend-item:hover {
  background-color: #f0f0f0;
}

.legend-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
}

.data-summary {
  margin-bottom: 15px;
}

details {
  margin-bottom: 10px;
}

summary {
  cursor: pointer;
  color: #004AAD;
  font-weight: bold;
}
</style>