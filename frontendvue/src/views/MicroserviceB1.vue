<template>
  <div class="defect-density-page">
    <!-- Main Container -->
    <div class="container">
      <h1>Defect Density Analysis</h1>
      <p>Enter a GitHub Repository Link</p>

      <!-- Two-way binding for the repo URL -->
      <input
        type="text"
        v-model="repoUrl"
        placeholder="https://github.com/owner/repository"
      />

      <!-- Button triggers API call -->
      <button @click="analyzeRepo">Analyze</button>

      <!-- Results area (only shown if there's a message) -->
      <div id="result" v-html="resultMessage" v-show="resultMessage !== ''"></div>
    </div>
  </div>
</template>

<script>
// If you actually need Chart.js or chartjs-plugin-annotation, install/import them
// and integrate them into Vue as desired. Otherwise, you can remove those script references.
export default {
  name: 'DefectDensityAnalysis',
  data() {
    return {
      repoUrl: '',
      resultMessage: '', // Will hold API results or error message
    };
  },
  methods: {
    async analyzeRepo() {
      // Basic validation
      if (!this.repoUrl.trim()) {
        alert('Please enter a GitHub repository URL.');
        return;
      }

      const apiUrl = `http://localhost:8080/api/defects/repo?url=${encodeURIComponent(this.repoUrl)}`;
      try {
        const response = await fetch(apiUrl);
        const defectCount = await response.text();

        if (isNaN(defectCount)) {
          // API returned some error string
          this.resultMessage = `<p style="color: red;">Error: ${defectCount}</p>`;
        } else {
          // API returned a numeric result
          this.resultMessage = `<h3>Defect Count: ${defectCount}</h3>`;
        }
      } catch (error) {
        this.resultMessage = `<p style="color: red;">Failed to fetch data.</p>`;
      }
    },
  },
};
</script>

<style scoped>
/* Match your original styling, adapted for a Vue component */
.defect-density-page {
  font-family: 'Roboto', Arial, sans-serif;
  background: #fdfdfd;
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  text-align: center;
  position: relative;
  overflow: hidden; /* Prevent whole page scrolling */
}

/* Analysis Box with Scrolling */
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
  display: block; /* Controlled via v-show */
}
</style>
