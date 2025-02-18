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
              apiUrl = "http://localhost:8081/api/afferent-coupling/upload";
              break;
          case "efferent":
              apiUrl = "http://localhost:8082/api/efferent-coupling/upload";
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
          resultDiv.innerHTML += `
              <p><strong>Total Lines of Code:</strong> ${data.totalLinesOfCode}</p>
              <p><strong>Total Defects:</strong> ${data.totalDefects}</p>
              <p><strong>Defect Density:</strong> ${data.defectDensity}</p>
          `;
      }

      resultDiv.style.display = "block";
  }
});
