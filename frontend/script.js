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
        let apiBaseUrl = "http://localhost:8080/api"
        let afferentApiUrl = `${apiBaseUrl}/afferent-coupling/upload`;
        let efferentApiUrl = `${apiBaseUrl}/efferent-coupling/upload`;
        let defectApiUrl = `${apiBaseUrl}/code-analysis/upload`;

        const formData = new FormData();
        formData.append("file", file);

        try {
            // if (!response.ok) {
            //     const errorText = await response.text();
            //     throw new Error(`HTTP ${response.status}: ${errorText}`);
            // }

            let afferentResponse, efferentResponse, defectResponse;

            switch (selectedOption) {
                case "combined":

                    const afferentFormData = new FormData();
                    afferentFormData.append("file", file);

                    const efferentFormData = new FormData();
                    efferentFormData.append("file", file);

                    [afferentResponse, efferentResponse] = await Promise.all([
                      fetch(afferentApiUrl, {
                          method: "POST",
                          body: afferentFormData,
                          headers: { "Accept": "application/json" }
                      }).then(res => res.json()),

                      fetch(efferentApiUrl, {
                          method: "POST",
                          body: efferentFormData,
                          headers: { "Accept": "application/json" }
                      }).then(res => res.json())
                  ]);
                  console.log("Combined API responses:", { afferentResponse, efferentResponse });
                  displayCombinedResults(afferentResponse, efferentResponse, file.name);

                  const instabilityValue = calculateInstability(file.name);
                  saveMetricsInstability(instabilityValue, file.name);
                  displayInstabilityResults(instabilityValue, file.name);

                  break;

                case "afferent":
                      afferentResponse = await fetch(afferentApiUrl, { method: "POST", body: formData }).then(res => res.json());
                      displayResultsAfferent(afferentResponse, file.name);
                      break;

                case "efferent":
                    efferentResponse = await fetch(efferentApiUrl, { method: "POST", body: formData }).then(res => res.json());
                    displayResultsEfferent(efferentResponse, file.name);
                    break;

                case "defect":
                    defectResponse = await fetch(defectApiUrl, { method: "POST", body: formData }).then(res => res.json());
                    displayResults(defectResponse, file.name);
                    break;

                default:
                  alert("Please select a valid API option.");
                  callApiButton.disabled = false;
                  return;
            }

        } catch (error) {
            console.error("Error calling API:", error);
            document.getElementById("result").innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
        } finally {
            callApiButton.disabled = false;
        }
    });

      // Function to calculate Instability Metric
         function calculateInstability(fileName) {
          const afferentMetrics = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
            const efferentMetrics = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};

            if (!afferentMetrics[fileName] || !efferentMetrics[fileName]) {
               alert("Both afferent and efferent coupling data are required to calculate instability.");
               return;
            }

        // Fetching latest afferent and efferent values
        const latestAfferent = afferentMetrics[fileName][afferentMetrics[fileName].length - 1].couplingData;
        const latestEfferent = efferentMetrics[fileName][efferentMetrics[fileName].length - 1].couplingData;

         // Computing total Ca and Ce values
        const Ca = Object.values(latestAfferent).reduce((sum, val) => sum + val, 0);
        const Ce = Object.values(latestEfferent).reduce((sum, val) => sum + val, 0);

        if (Ca + Ce === 0) {
         alert("Cannot calculate instability metric when both afferent and efferent coupling are zero.");
         return;
         }

         const instability = Ce / (Ca + Ce);
         return instability;
    }

    function saveMetrics(data, fileName) {
        // Parse as float so we can chart it numerically
        const numericDefectDensity = parseFloat(data.defectDensity) || 0;

        // Get existing metrics object from localStorage, or fallback to {}
        let allMetrics = JSON.parse(localStorage.getItem("metricsHistory")) || {};

        if (!allMetrics[fileName]) {
            allMetrics[fileName] = [];
        }

        // Push the new record
        allMetrics[fileName].push({
            timestamp: new Date().toLocaleString(),
            fileName: fileName,
            totalLinesOfCode: data.totalLinesOfCode ?? null,
            totalDefects: data.totalDefects ?? null,
            defectDensity: numericDefectDensity
        });

        // Trim to last 5 entries
        if (allMetrics[fileName].length > 5) {
            allMetrics[fileName].shift(); // remove the oldest entry
        }

        // Save back to localStorage as an object
        localStorage.setItem("metricsHistory", JSON.stringify(allMetrics));
    }

    function saveMetricsEfferent(data, fileName) {
        let allEfferent = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};
        if (!allEfferent[fileName]) {
          allEfferent[fileName] = [];
        }
        allEfferent[fileName].push({
          timestamp: new Date().toLocaleString(),
          fileName,
          couplingData: data
        });
        if (allEfferent[fileName].length > 5) {
          allEfferent[fileName].shift();
        }
        localStorage.setItem("efferentMetricsHistory", JSON.stringify(allEfferent));
      }

      function saveMetricsAfferent(data, fileName) {
        let allAfferent = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
        if (!allAfferent[fileName]) {
          allAfferent[fileName] = [];
        }
        allAfferent[fileName].push({
          timestamp: new Date().toLocaleString(),
          fileName,
          couplingData: data
        });
        if (allAfferent[fileName].length > 5) {
          allAfferent[fileName].shift(); // keep last 5
        }
        localStorage.setItem("afferentMetricsHistory", JSON.stringify(allAfferent));
      }

      // Save Instability Metrics
      function saveMetricsInstability(instabilityValue, fileName) {
         let allInstability = JSON.parse(localStorage.getItem("instabilityMetricsHistory")) || {};

         if (!allInstability[fileName]) {
             allInstability[fileName] = [];
         }

         allInstability[fileName].push({
              timestamp: new Date().toLocaleString(),
              fileName,
              instability: instabilityValue
         });

         if (allInstability[fileName].length > 5) {
              allInstability[fileName].shift();
         }

            localStorage.setItem("instabilityMetricsHistory", JSON.stringify(allInstability));
         }

    function displayResults(data, selectedOption, fileName) {
        const resultDiv = document.getElementById("result");

        const numericDefectDensity = parseFloat(data.defectDensity) || 0;
        let category = "";

        if (numericDefectDensity <= 100) {
            category = "Highly Reliable Software 🟢";
        } else if (numericDefectDensity <= 500) {
            category = "Industry Standard Software 🟡";
        } else if (numericDefectDensity <= 1000) {
            category = "Acceptable Threshold ⚠️";
        } else {
            category = "Poor Quality Software 🔴";
        }

        resultDiv.innerHTML = `
          <h2>Analysis Result</h2>
          <p><strong>Total Lines of Code:</strong> ${data.totalLinesOfCode ?? "N/A"}</p>
          <p><strong>Total Defects:</strong> ${data.totalDefects ?? "N/A"}</p>
          <p><strong>Defect Density:</strong> ${data.defectDensity ?? "N/A"} (per 1000 LOC)</p>
          <p><strong>Category:</strong> <span style="font-weight: bold; color: ${
            category.includes("Highly Reliable") ? "green" :
            category.includes("Industry Standard") ? "orange" :
            category.includes("Acceptable Threshold") ? "goldenrod" :
            "red"
        }">${category}</span></p>
          <h3>Benchmark Comparison</h3>
          <canvas id="benchmarkChart" width="500" height="300"></canvas>
        `;

        saveMetrics(data, fileName);

        renderBenchmarkComparison(fileName);
    }

    function renderBenchmarkComparison(fileName) {
        const allMetrics = JSON.parse(localStorage.getItem("metricsHistory")) || {};

        if (!allMetrics[fileName] || allMetrics[fileName].length === 0) {
            document.getElementById("benchmarkChart").outerHTML =
                "<p>No previous data found for this file.</p>";
            return;
        }

        const history = allMetrics[fileName];
        const timestamps = history.map(entry => entry.timestamp);
        const defectDensities = history.map(entry => entry.defectDensity);

        const ctx = document.getElementById("benchmarkChart").getContext("2d");

        if (window.benchmarkChart instanceof Chart) {
            window.benchmarkChart.destroy();
        }

        window.benchmarkChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: timestamps,
                datasets: [
                    {
                        label: `Defect Density for ${fileName}`,
                        data: defectDensities,
                        backgroundColor: "rgba(54, 162, 235, 0.5)",
                        borderColor: "rgba(54, 162, 235, 1)",
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        suggestedMax: 20,
                        title: {
                            display: true,
                            text: "Defect Density"
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: "Timestamp"
                        }
                    }
                },
                plugins: {
                    annotation: {
                        annotations: {
                            benchmarkLine: {
                                type: "line",
                                yMin: 10,
                                yMax: 10,
                                borderColor: "red",
                                borderWidth: 2,
                                label: {
                                    enabled: true,
                                    content: "Benchmark (10 per 1000 LOC)",
                                    position: "end",  // positions label at the line end
                                    backgroundColor: "rgba(255, 255, 255, 0.7)",
                                    color: "red"
                                }
                            }
                        }
                    }
                }
            }
        });
    }

  function displayResultsAfferent(data, selectedOption, fileName) {
    const resultDiv = document.getElementById("result");
    resultDiv.innerHTML = `
      <h2>Afferent Coupling Analysis</h2>
      <p><strong>File:</strong> ${fileName}</p>
      <h3>Coupling Results:</h3>
      <ul id="afferentList"></ul>
      <canvas id="afferentChart" width="500" height="300"></canvas>
    `;

    // Fill the list
    const afferentList = document.getElementById("afferentList");
    for (const [className, count] of Object.entries(data)) {
      const li = document.createElement("li");
      li.textContent = `${className}: ${count}`;
      afferentList.appendChild(li);
    }

    // Save & visualize
    saveMetricsAfferent(data, fileName);
    renderBenchmarkComparisonAfferent(fileName);
  }


  function renderBenchmarkComparisonAfferent(fileName) {
    const allAfferent = JSON.parse(localStorage.getItem("afferentMetricsHistory")) || {};
    const history = allAfferent[fileName];

    if (!history || history.length === 0) {
      document.getElementById("afferentChart").outerHTML =
        "<p>No afferent coupling data found for this file.</p>";
      return;
    }

    const allClassNames = new Set();
    for (let snapshot of history) {
      for (let className of Object.keys(snapshot.couplingData)) {
        allClassNames.add(className);
      }
    }

    const timestamps = history.map(h => h.timestamp);

    const datasets = [];
    [...allClassNames].forEach((className, index) => {
      const dataPoints = history.map(snapshot => {
        return snapshot.couplingData[className] || 0;
      });
      datasets.push({
        label: className,
        data: dataPoints,
        borderColor: getColorByIndex(index),
        backgroundColor: "transparent",
        tension: 0.1,
        borderWidth: 2
      });
    });

    const ctx = document.getElementById("afferentChart").getContext("2d");

    if (window.afferentChart instanceof Chart) {
      window.afferentChart.destroy();
    }

    window.afferentChart = new Chart(ctx, {
      type: "line",
      data: {
        labels: timestamps,
        datasets: datasets
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            },
            title: {
              display: true,
              text: "Coupling Value"
            }
          },
          x: {
            title: {
              display: true,
              text: "Time"
            }
          }
        },
        plugins: {
            annotation: {
              annotations: {
                benchmarkLine: {
                  type: "line",
                  yMin: 5,  // benchmark value
                  yMax: 5,
                  borderColor: "red",
                  borderWidth: 2,
                  borderDash: [6, 6],
                  label: {
                    enabled: true,
                    content: "Benchmark (Idealized Value)",
                    position: "end",
                    backgroundColor: "rgba(255, 255, 255, 0.7)",
                    color: "red"
                  }
                }
              }
            },
            legend: {
              display: true
            }
          }
      }
    });
  }

  function getColorByIndex(index) {
    const palette = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0",
      "#9966FF", "#FF9F40", "#00A8A8", "#A8A800",
      "#A800A8", "#0080FF", "#FF8000"
    ];
    return palette[index % palette.length];
  }

  function displayResultsEfferent(data, selectedOption, fileName) {
    const resultDiv = document.getElementById("result");
    resultDiv.innerHTML = `
      <h2>Efferent Coupling Analysis</h2>
      <p><strong>File:</strong> ${fileName}</p>
      <h3>Coupling Results:</h3>
      <ul id="efferentList"></ul>
      <canvas id="efferentChart" width="500" height="300"></canvas>
    `;

    // Fill the list
    const efferentList = document.getElementById("efferentList");
    for (const [className, count] of Object.entries(data)) {
      const li = document.createElement("li");
      li.textContent = `${className}: ${count}`;
      efferentList.appendChild(li);
    }

    // Save & visualize
    saveMetricsEfferent(data, fileName);
    renderBenchmarkComparisonEfferent(fileName);
  }


  function renderBenchmarkComparisonEfferent(fileName) {
    const allEfferent = JSON.parse(localStorage.getItem("efferentMetricsHistory")) || {};
    const history = allEfferent[fileName];

    if (!history || history.length === 0) {
      const chartCanvas = document.getElementById("efferentChart");
      if (chartCanvas) {
        chartCanvas.outerHTML = "<p>No efferent coupling data found for this file.</p>";
      }
      return;
    }

    const allClassNames = new Set();
    history.forEach(snapshot => {
      Object.keys(snapshot.couplingData).forEach(className => allClassNames.add(className));
    });

    const timestamps = history.map(h => h.timestamp);

    const datasets = [...allClassNames].map((className, idx) => {
      // For each snapshot, get the coupling value or 0
      const dataPoints = history.map(snapshot => snapshot.couplingData[className] || 0);
      return {
        label: className,
        data: dataPoints,
        borderColor: getColorByIndex(idx),
        backgroundColor: "transparent",
        borderWidth: 2,
        tension: 0.1
      };
    });

    // Build the chart
    const ctx = document.getElementById("efferentChart").getContext("2d");

    if (window.efferentChart instanceof Chart) {
      window.efferentChart.destroy();
    }

    window.efferentChart = new Chart(ctx, {
      type: "line",
      data: {
        labels: timestamps,
        datasets: datasets
      },
      options: {
        responsive: true,
        scales: {
          x: {
            title: {
              display: true,
              text: "Time"
            }
          },
          y: {
            beginAtZero: true,
            ticks: {
              stepSize: 1
            },
            title: {
              display: true,
              text: "Efferent Coupling Value"
            }
          }
        },
        plugins: {
          annotation: {
            annotations: {
              benchmarkLine: {
                type: "line",
                yMin: 5,  //benchmark value
                yMax: 5,
                borderColor: "red",
                borderWidth: 2,
                borderDash: [6, 6],
                label: {
                  enabled: true,
                  content: "Benchmark (Idealized Value)",
                  position: "end",
                  backgroundColor: "rgba(255, 255, 255, 0.7)",
                  color: "red"
                }
              }
            }
          },
          legend: {
            display: true
          }
        }
      }
    });
  }


  function getColorByIndex(index) {
    const palette = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0",
      "#9966FF", "#FF9F40", "#00A8A8", "#A8A800",
      "#A800A8", "#0080FF", "#FF8000"
    ];
    return palette[index % palette.length];
  }

  function displayCombinedResults(afferentData, efferentData, fileName) {
    const resultDiv = document.getElementById("result");
    resultDiv.innerHTML = `
        <h2>Afferent & Efferent Coupling Analysis</h2>
        <p><strong>File:</strong> ${fileName}</p>
        <h3>Afferent Coupling Results:</h3>
        <ul id="afferentList"></ul>
        <canvas id="afferentChart" width="500" height="300"></canvas>
        <h3>Efferent Coupling Results:</h3>
        <ul id="efferentList"></ul>
        <canvas id="efferentChart" width="500" height="300"></canvas>
    `;

    // Fill the lists
    const afferentList = document.getElementById("afferentList");
    for (const [className, count] of Object.entries(afferentData)) {
        const li = document.createElement("li");
        li.textContent = `${className}: ${count}`;
        afferentList.appendChild(li);
    }

    const efferentList = document.getElementById("efferentList");
    for (const [className, count] of Object.entries(efferentData)) {
        const li = document.createElement("li");
        li.textContent = `${className}: ${count}`;
        efferentList.appendChild(li);
    }

    // Save metrics separately
    saveMetricsAfferent(afferentData, fileName);
    saveMetricsEfferent(efferentData, fileName);

    // Render separate graphs for Afferent and Efferent

        setTimeout(() => {
            renderBenchmarkComparisonAfferent(fileName);
            renderBenchmarkComparisonEfferent(fileName);
        }, 500);

}
// Display Instability Results
  function displayInstabilityResults(instabilityValue, fileName) {
  const resultDiv = document.getElementById("result");
  resultDiv.innerHTML += `
      <h3>Instability Metric Results:</h3>
      <p><strong>Instability:</strong> ${instabilityValue.toFixed(2)}</p>
      <canvas id="instabilityChart" width="500" height="300"></canvas>
      `;
      renderBenchmarkComparisonInstability(fileName);
  }

 // Render Instability Graph
 function renderBenchmarkComparisonInstability(fileName) {
  const allInstability = JSON.parse(localStorage.getItem("instabilityMetricsHistory")) || {};
  const history = allInstability[fileName];

  if (!history || history.length === 0) {
  document.getElementById("instabilityChart").outerHTML =
   "<p>No instability data found for this file.</p>";
    return;
   }

   const timestamps = history.map(h => h.timestamp);
   const instabilityValues = history.map(h => h.instability);

  const ctx = document.getElementById("instabilityChart").getContext("2d");

  if (window.instabilityChart instanceof Chart) {
       window.instabilityChart.destroy();
  }

  window.instabilityChart = new Chart(ctx, {
       type: "line",
       data: {
       labels: timestamps,
       datasets: [{
       label: "Instability Over Time",
       data: instabilityValues,
       borderColor: "#FF5733",
       backgroundColor: "transparent",
       borderWidth: 2,
       tension: 0.1
       }]
       },
        options: {
             responsive: true,
             scales: {
              x: {
               title: { display: true, text: "Time" }
               },
              y: {
                beginAtZero: true,
                title: { display: true, text: "Instability Metric (Ce / (Ca + Ce))" }
              }
              },
              plugins: {
                annotation: {
                annotations: {
                benchmarkLine: {
                    type: "line",
                    yMin: 0.5, // Example benchmark value
                    yMax: 0.5,
                    borderColor: "red",
                    borderWidth: 2,
                    borderDash: [6, 6],
                    label: {
                    enabled: true,
                    content: "Ideal Benchmark (0.5)",
                    position: "end",
                    backgroundColor: "rgba(255, 255, 255, 0.7)",
                    color: "red"
                    }
                   }
                  }
                },
                 legend: { display: true }
                    }
                }
            });
   }

});

// console.log(JSON.parse(localStorage.getItem("afferentMetricsHistory")));
// console.log(JSON.parse(localStorage.getItem("efferentMetricsHistory")));
// console.log(JSON.parse(localStorage.getItem("instabilityMetricsHistory")));
// console.log(JSON.parse(localStorage.getItem("metricsHistory")));
// localStorage.removeItem("afferentMetricsHistory");
// localStorage.removeItem("efferentMetricsHistory");
// localStorage.removeItem("instabilityMetricsHistory");
// localStorage.removeItem("metricsHistory");