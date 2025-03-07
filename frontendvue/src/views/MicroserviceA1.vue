<template>
  <div class="afferent-efferent-page">
    <div class="container">
      <h1>Afferent & Efferent Coupling Analysis</h1>
      <div class="file-input-container">
        <input type="file" @change="handleFileUpload" accept=".zip" />
      </div>
      <button @click="analyzeFile">Analyze</button>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: 'MicroserviceA1',

  setup() {
    const selectedFile = ref(null);
    const resultVisible = ref(false);
    const fileName = ref("");
    const afferentData = ref({});
    const efferentData = ref({});
    const afferentChartRef = ref(null);
    const efferentChartRef = ref(null);
    const resultMessage = ref("");

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

      } catch (error) {
        console.error("Error calling API:", error);
      }
    };

    return {
      selectedFile,
      resultVisible,
      fileName,
      afferentData,
      efferentData,
      afferentChartRef,
      efferentChartRef,
      resultMessage,
      handleFileUpload,
      analyzeFile,
    };
  },
};
</script>

<style scoped>
.afferent-efferent-page {
  font-family: "Roboto", Arial, sans-serif;
  background-color: #007BFF;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
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
