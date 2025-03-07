<template>
  <div class="defect-density-page">
    <!-- Main Container -->
    <div class="container">
      <h1>Defect Density Analysis</h1>
      <p>Enter a GitHub Repository Link</p>
      <input v-model="githubRepo" type="text" placeholder="https://github.com/owner/repository">
      <button @click="analyzeDefectDensity">Analyze</button>
      <div v-if="result" class="result-box">
        {{ result }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "DefectDensityAnalysis",
  setup() {
    const githubRepo = ref("");
    const result = ref("");

    const analyzeDefectDensity = () => {
      if (!githubRepo.value.trim()) {
        result.value = "Please enter a valid GitHub repository URL.";
        return;
      }

      // Simulating API Call
      setTimeout(() => {
        result.value = `Analysis for ${githubRepo.value} completed! (Mock Data)`;
      }, 1500);
    };

    return { githubRepo, result, analyzeDefectDensity };
  },
};
</script>

<style scoped>
/* Page Styling */
.defect-density-page {
  font-family: 'Roboto', Arial, sans-serif;
  background: #007bff;
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  text-align: center;
  position: relative;
  overflow: hidden;
}

/* Navigation Sidebar */
.nav-buttons {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 220px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nav-buttons .nav-link {
  text-decoration: none;
  background: white;
  color: #007bff;
  padding: 14px 20px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: bold;
  width: 100%;
  display: block;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, background 0.3s;
}

.nav-buttons .nav-link:hover {
  background: #f8f9fa;
  transform: scale(1.05);
}

.nav-buttons .active {
  background: #004aad;
  color: white;
  transform: scale(1.05);
}

/* Container */
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

.result-box {
  margin-top: 20px;
  padding: 15px;
  border-radius: 8px;
  background: #f9f9f9;
  font-size: 18px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  text-align: center;
}
</style>