
document.addEventListener("DOMContentLoaded", () => {
    const callApiButton = document.getElementById("callApiButton");
  
    callApiButton.addEventListener("click", () => {
      const fileInput = document.getElementById("fileInput");
  
      if (!fileInput.files.length) {
        alert("Please select a zip file.");
        return;
      }
  
      const file = fileInput.files[0];
      console.log("File received:", file);
  
      const selectedOption = document.getElementById("apiSelect").value;
      let apiUrl = "";
      switch (selectedOption) {
        case "afferent":
          apiUrl = "http://localhost:8083/api/code-analysis/afferent-coupling";
          break;
        case "efferent":
          apiUrl = "http://localhost:8083/api/code-analysis/efferent-coupling";
          break;
        case "defect":
          apiUrl = "http://localhost:8083/api/code-analysis/analyze";
          break;
        default:
          alert("Please select a valid API option.");
          return;
      }
  
      // Prepare FormData with the file.
      const formData = new FormData();
      formData.append("file", file);
  
      // Make the API call using fetch.
      fetch(apiUrl, {
        method: "POST",
        body: formData
      })
        .then(async (response) => {
          if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText}`);
          }
          return response.json();
        })
        .then((data) => {
          console.log("API response:", data);
          // Update the UI with the API response.
          const resultDiv = document.getElementById("result");
          resultDiv.innerHTML = `<h2>Analysis Result</h2>
            <p>Total Lines of Code: ${data.totalLinesOfCode}</p>
            <p>Total Defects: ${data.totalDefects}</p>
            <p>Defect Density: ${data.defectDensity}</p>`;
        })
        .catch((error) => {
          console.error("Error calling API:", error);
          const resultDiv = document.getElementById("result");
          resultDiv.innerHTML = `<p style="color: red;">Error: ${error.message}</p>`;
        });
    });
  });
