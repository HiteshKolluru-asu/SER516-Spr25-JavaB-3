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
  
    });
  });