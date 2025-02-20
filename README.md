# SER516-Spr25-JavaB-3

# Software Quality Metrics

This guide provides instructions on how to run the application both locally and using Docker.
This project uses maven as its build tool. Specifically, we use Java SDK 21.

**Key Details:**

* **File Size Limit:** Maximum file upload size is 100MB.
* **Comparison:** While fetching the history of metric values the records are compared using file name.
* **Number of files saved on browser:** Maximum each file history is 5, it will hold only previous 5 entries.
* **Benchmark value:** For Afferent coupling and Efferent coupling metric - 5
* **Benchmark value:** For Defect Density metric - 10
* **TestData:** Please use zip files inside TestData folder to test the application

**How to Use:**

## Building and Running the Application

1. **Navigate to the Project root Directory:**
   ```bash
   cd SER516-Spr25-JavaB-3
   ```

2. **Install Dependencies:**
   ```bash
   mvn clean install -P run-all
   ```
3. **Building and Running the Application:**
   ```bash
   docker-compose up --build
   ```

4. **To Run the Application:**
   ```
   open frontend/index.html
   ```
    - **Expected Input Value:** A zip file containing Java code.