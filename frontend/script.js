document.addEventListener("DOMContentLoaded", () => {

  const callApiButton = document.getElementById("callApiButton");

  callApiButton.addEventListener("click", async () => {
      const fileInput = document.getElementById("fileInput");
      if (!fileInput.files.length) {
          alert("Please select a zip file.");
          return;
      }

      const file = fileInput.files[0];
      console.log("File received:", file);

      callApiButton.disabled = true;
      const selectedOption = document.getElementById("apiSelect").value;
      let apiUrl = "";

      switch (selectedOption) {
          case "afferent":
              apiUrl = "http://localhost:8080/api/afferent-coupling/upload";
              break;
          case "efferent":
              apiUrl = "http://localhost:8080/api/efferent-coupling/upload";
              break;
          case "defect":
              apiUrl = "http://localhost:8083/api/code-analysis/analyze";
              break;
          default:
              alert("Please select a valid API option.");
              callApiButton.disabled = false;
              return;
      }

      const formData = new FormData();
      formData.append("file", file);

      try {
          const response = await fetch(apiUrl, {
              method: "POST",
              body: formData
          });

          if (!response.ok) {
              const errorText = await response.text();
              throw new Error(`HTTP ${response.status}: ${errorText}`);
          }

          const data = await response.json();
          console.log("API response:", data);
          displayResults(data, selectedOption);
      } catch (error) {
          console.error("Error calling API:", error);
          const resultDiv = document.getElementById("result");
          resultDiv.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
      } finally {
        // Re-enable the button after the API call completes
        callApiButton.disabled = false;
    }
  });

  function saveMetrics(data) {
    let previousMetrics = JSON.parse(localStorage.getItem("metricsHistory")) || [];
    
    // Keep only last 5 records for benchmarking
    if (previousMetrics.length >= 5) {
        previousMetrics.shift();
    }

    previousMetrics.push({
        timestamp: new Date().toLocaleString(),
        totalLinesOfCode: data.totalLinesOfCode,
        totalDefects: data.totalDefects,
        defectDensity: data.defectDensity
    });

    localStorage.setItem("metricsHistory", JSON.stringify(previousMetrics));
}



function displayResults(data, selectedOption) {
    const resultDiv = document.getElementById("result");
    resultDiv.innerHTML = `<h2>Analysis Result</h2>`;

    if (selectedOption === "afferent" || selectedOption === "efferent") {
        const list = document.createElement("ul");
        Object.entries(data).forEach(([className, count]) => {
            const listItem = document.createElement("li");
            listItem.textContent = `${className}: ${count}`;
            list.appendChild(listItem);
        });
        resultDiv.appendChild(list);
    } else if (selectedOption === "defect") {
        let category = "";

        // Determine category based on industry standards
        if (data.defectDensity <= 100) {
            category = "Highly Reliable Software 🟢";
        } else if (data.defectDensity <= 500) {
            category = "Industry Standard Software 🟡";
        } else if (data.defectDensity <= 1000) {
            category = "Acceptable Threshold ⚠️";
        } else {
            category = "Poor Quality Software 🔴";
        }

        resultDiv.innerHTML += `
            <p><strong>Total Lines of Code:</strong> ${data.totalLinesOfCode}</p>
            <p><strong>Total Defects:</strong> ${data.totalDefects}</p>
            <p><strong>Defect Density:</strong> ${data.defectDensity} (per 1000 LOC)</p>
            <p><strong>Category:</strong> <span style="font-weight: bold; color: ${
                category.includes("Highly Reliable") ? "green" :
                category.includes("Industry Standard") ? "orange" :
                category.includes("Acceptable Threshold") ? "goldenrod" :
                "red"}">${category}</span></p>
            <div style="display: flex; flex-wrap: wrap; gap: 20px; justify-content: center;">
                <canvas id="linesOfCodeChart" width="300" height="300"></canvas>
                <canvas id="totalDefectsChart" width="300" height="300"></canvas>
                <canvas id="defectDensityChart" width="300" height="300"></canvas>
            </div>
            <h3>Benchmark Comparison</h3>
            <canvas id="benchmarkChart" width="500" height="300"></canvas>
        `;

        saveMetrics(data);
        renderCharts(data);
        renderBenchmarkComparison();
    }

    resultDiv.style.display = "block";
}



function renderCharts(data) {
    // Destroy previous charts if they exist
    if (window.linesOfCodeChart && typeof window.linesOfCodeChart.destroy === "function") {
        window.linesOfCodeChart.destroy();
    }
    if (window.totalDefectsChart && typeof window.totalDefectsChart.destroy === "function") {
        window.totalDefectsChart.destroy();
    }
    if (window.defectDensityChart && typeof window.defectDensityChart.destroy === "function") {
        window.defectDensityChart.destroy();
    }    

    // Get chart contexts
    const locCtx = document.getElementById("linesOfCodeChart").getContext("2d");
    const defectsCtx = document.getElementById("totalDefectsChart").getContext("2d");
    const densityCtx = document.getElementById("defectDensityChart").getContext("2d");

    // Lines of Code Chart
    window.linesOfCodeChart = new Chart(locCtx, {
        type: "bar",
        data: {
            labels: ["Project"],
            datasets: [{
                label: "Total Lines of Code",
                data: [data.totalLinesOfCode],
                backgroundColor: "rgba(54, 162, 235, 0.7)",
                borderColor: "rgba(54, 162, 235, 1)",
                borderWidth: 1,
                borderRadius: 8
            }]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });

    // Total Defects Chart
    window.totalDefectsChart = new Chart(defectsCtx, {
        type: "bar",
        data: {
            labels: ["Project"],
            datasets: [{
                label: "Total Defects",
                data: [data.totalDefects],
                backgroundColor: "rgba(255, 99, 132, 0.7)",
                borderColor: "rgba(255, 99, 132, 1)",
                borderWidth: 1,
                borderRadius: 8
            }]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });

    // Defect Density Chart
    window.defectDensityChart = new Chart(densityCtx, {
        type: "bar",
        data: {
            labels: ["Project"],
            datasets: [{
                label: "Defect Density",
                data: [data.defectDensity],
                backgroundColor: "rgba(75, 192, 192, 0.7)",
                borderColor: "rgba(75, 192, 192, 1)",
                borderWidth: 1,
                borderRadius: 8
            }]
        },
        options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });
}

function renderBenchmarkComparison() {
    let previousMetrics = JSON.parse(localStorage.getItem("metricsHistory")) || [];

    if (previousMetrics.length < 2) {
        document.getElementById("benchmarkChart").outerHTML = "<p>Not enough data for benchmarking yet.</p>";
        return;
    }

    const timestamps = previousMetrics.map(m => m.timestamp);
    const defectDensities = previousMetrics.map(m => m.defectDensity);

    const benchmarkCtx = document.getElementById("benchmarkChart").getContext("2d");

    // Destroy old chart if it exists
    if (window.benchmarkChart && typeof window.benchmarkChart.destroy === "function") {
        window.benchmarkChart.destroy();
    }

    window.benchmarkChart = new Chart(benchmarkCtx, {
        type: "line",
        data: {
            labels: timestamps,
            datasets: [
                {
                    label: "Defect Density Over Time",
                    data: defectDensities,
                    borderColor: "rgba(255, 159, 64, 1)",
                    backgroundColor: "rgba(255, 159, 64, 0.5)",
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            scales: { y: { beginAtZero: true } }
        }
    });
}


});
