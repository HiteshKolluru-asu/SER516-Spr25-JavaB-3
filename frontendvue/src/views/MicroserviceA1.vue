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
        <ul>
          <li v-for="(count, className) in afferentData" :key="className">
            {{ className }}: {{ count }}
          </li>
        </ul>

        <!-- Efferent Coupling Results -->
        <h4>Efferent Coupling</h4>
        <ul>
          <li v-for="(count, className) in efferentData" :key="className">
            {{ className }}: {{ count }}
          </li>
        </ul>

        <!-- Instability Result -->
        <h4>Instability Metric</h4>
        <p><strong>Value:</strong> {{ instabilityResult !== null ? instabilityResult.toFixed(4) : "N/A" }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "MicroserviceA1",

  setup() {
    const selectedFile = ref(null);
    const resultVisible = ref(false);
    const fileName = ref("");
    const afferentData = ref({});
    const efferentData = ref({});
    const instabilityResult = ref(null);

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

        

        // Calculate instability
        const instabilityValue = calculateInstability(fileName.value);
        if (instabilityValue !== null) {
          saveMetricsInstability(instabilityValue, fileName.value);
          instabilityResult.value = instabilityValue;
        }

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

    return {
      selectedFile,
      resultVisible,
      fileName,
      afferentData,
      efferentData,
      instabilityResult,
      handleFileUpload,
      analyzeFile,
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
  max-width: 700px;
  width: 85%;
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
</style>