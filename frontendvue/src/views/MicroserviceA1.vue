<template>
  <div class="afferent-efferent-page">
    <div class="container">
      <h1>Afferent & Efferent Coupling Analysis</h1>
      <div class="file-input-container">
        <input type="file" @change="handleFileUpload" accept=".zip" />
      </div>
      <button @click="analyzeFile">Analyze</button>

      <!-- Analysis Results -->
      <div id="result" v-if="resultVisible">
        <h3>Analysis Results:</h3>
        <p><strong>File:</strong> {{ fileName }}</p>

        <h4>Afferent Coupling</h4>
        <ul>
          <li v-for="(count, className) in afferentData" :key="className">
            {{ className }}: {{ count }}
          </li>
        </ul>
        <canvas ref="afferentChart"></canvas>

        <h4>Efferent Coupling</h4>
        <ul>
          <li v-for="(count, className) in efferentData" :key="className">
            {{ className }}: {{ count }}
          </li>
        </ul>
        <canvas ref="efferentChart"></canvas>
      </div>
    </div>
  </div>
</template>
  
  <script>
  export default {
    name: 'MicroserviceA1'
  }
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
/* transition: background 0.3s ease, transform 0.2s; */
font-size: 20px;
}
button:hover {
background-color: #003D80;
transform: scale(1.07);
}
</style>
  