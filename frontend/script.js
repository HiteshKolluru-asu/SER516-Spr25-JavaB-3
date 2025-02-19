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
            displayResults(data, selectedOption, file.name);
        } catch (error) {
            console.error("Error calling API:", error);
            document.getElementById("result").innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
        } finally {
            callApiButton.disabled = false;
        }
    });

    function saveMetrics(data, fileName) {
        // Parse as float so we can chart it numerically
        const numericDefectDensity = parseFloat(data.defectDensity) || 0;

        // Get existing metrics object from localStorage, or fallback to {}
        let allMetrics = JSON.parse(localStorage.getItem("metricsHistory")) || {};

        // If there's no array for this file, create it
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

    function displayResults(data, selectedOption, fileName) {
        const resultDiv = document.getElementById("result");

        // Convert defectDensity to a number before checking
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

        // Save metrics in localStorage (keyed by fileName)
        saveMetrics(data, fileName);

        // Render the chart from updated localStorage
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
    
        // Destroy old chart instance if it exists
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
                        // suggestedMax ensures the chart will expand if the data exceeds 20,
                        // but if the data is below 10, we still show up to 20 to see the benchmark line.
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
                    // The annotation plugin is configured here
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
    
});

// console.log(JSON.parse(localStorage.getItem("metricsHistory")));
// localStorage.removeItem("metricsHistory");