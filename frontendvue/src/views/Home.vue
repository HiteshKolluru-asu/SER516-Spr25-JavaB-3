<!-- Home.vue -->
<template>
    <div class="home-body">
      <div class="container">
        <h1>Software Quality Metrics</h1>
        <div class="option-box">
          <!-- Replace these links with whatever microservice routes you have configured -->
          <div class="box">
            <router-link to="/microservice-a1">📊 Afferent &amp; Efferent Coupling</router-link>
          </div>
          <div class="box">
            <router-link to="/microservice-b1">🛠️ Defect Density Analysis</router-link>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    // Must be multi-word to satisfy vue/multi-word-component-names
    name: 'HomePage'
  }
  </script>
  
  <style scoped>
  .home-body {
    /* Replicate the body styling from your original index.html */
    font-family: 'Roboto', Arial, sans-serif;
    /* background: url('@/assets/BG.jpg') no-repeat center center fixed;  */
    /* or '@/assets/BG.jpg' if in src/assets */
    background-size: contain;
    background-color: #f4f7fa;
    margin: 0;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    text-align: center;
  }
  
  .container {
    background: rgba(255, 255, 255, 0.95);
    padding: 60px;
    border-radius: 14px;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.25);
    color: #333;
    max-width: 650px;
    width: 90%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  h1 {
    font-size: 42px;
    font-weight: 700;
    margin-bottom: 40px;
    color: #222;
  }
  
  .option-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 25px;
    width: 100%;
  }
  
  .box {
    width: 80%;
    background: #007bff;
    color: #fff;
    padding: 20px;
    border-radius: 12px;
    cursor: pointer;
    font-size: 22px;
    font-weight: bold;
    transition: transform 0.3s, box-shadow 0.3s;
    box-shadow: 0 5px 12px rgba(0, 0, 0, 0.15);
    text-align: center;
  }
  
  .box:hover {
    background: #0056b3;
    transform: scale(1.06);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  }
  
  .box a {
    text-decoration: none;
    color: #fff;
    display: block;
  }
  
  /* Responsive styles */
  @media (max-width: 768px) {
    .container {
      padding: 50px;
    }
    h1 {
      font-size: 36px;
    }
    .box {
      font-size: 20px;
      padding: 18px;
      width: 90%;
    }
  }
  </style>
  