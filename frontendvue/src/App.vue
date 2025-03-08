<!-- src/App.vue -->
<template>
  <div id="app">
    <!-- Navigation bar at the top -->
    <nav class="navbar">
      <!-- Logo or Title (optional) -->
      <h2>Software Quality Metrics Dashboard</h2>

      <div class="nav-links">
        <router-link to="/" class="nav-link">🏠 Home</router-link>
        <router-link to="/afferent-efferent" class="nav-link">📊 Afferent & Efferent</router-link>
        <router-link to="/defect-density" class="nav-link">🔍 Defect Density Analysis</router-link>
      </div>
    </nav>

    <!-- Main content area -->
    <div class="content-container">
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 
  The #app styles the entire page, applying a background image that
  is white-based, plus flexible layout so the nav sits at top.
*/
#app {
  margin: 0;                  /* Remove default margins */
  min-height: 100vh;          /* Ensure full viewport height */
  display: flex;
  flex-direction: column;     /* Stack nav on top, content below */
  font-family: Avenir, Helvetica, Arial, sans-serif;
  color: #2c3e50;

  /* 
    White-based background image.
    Place BG.jpg in /public folder if you want to reference it with "/BG.jpg".
    If you prefer /src/assets, then use "url('@/assets/BG.jpg')".
  */
  background: url('@/assets/BG.jpg') no-repeat center center fixed; 
  background-size: cover;     /* or "contain" if you want the entire image visible */
}

/* 
  NAVIGATION BAR 
  - Slightly transparent white background so the underlying image shows through
  - Horizontal layout
*/
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #004aad;
  color: White;
  padding: 10px 20px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.15);
}

.navbar h2 {
  margin: 0;
  font-weight: 600;
  color: White;
}

/* Container for the navigation links on the right (or left) */
.nav-links {
  display: flex;
  gap: 20px; /* Space between links */
}

/* Style for router-link elements */
.nav-links a {
  color: White;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-links a:hover {
  color: #0056b3;
}

/* 
  MAIN CONTENT CONTAINER 
  - Takes remaining space under the nav
  - Centers content vertically/horizontally if desired
*/
.content-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
</style>
