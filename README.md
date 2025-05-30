# SER516-Spr25-JavaB-3

# User Guide - Software Quality Metrics

# Project Overview

**Project Name:** **Software Quality Metrics**

This guide provides instructions on how to run the application both locally and using Docker.
This project uses maven as its build tool. Specifically, we use Java SDK 21.
## 1. Project Description:

The Software Quality Metrics project is a Java-based application designed to analyze software quality by computing essential metrics such as:
* **Afferent Coupling (Ca):** Measures how many other classes depend on a given class.
* **Efferent Coupling (Ce):** Measures how many external classes a given class depends on.
* **Defect Density:** Calculates the number of defects per lines of code to assess software reliability.
* **Instability Metric(I):** Measures how susceptible a software package is, to changes based on its outgoing dependencies. It is calculated using both Ce and Ca.

* This project enables developers to upload ZIP files containing Java code, analyze quality metrics, and track file history. It also supports benchmark values for quality evaluation.

## 2. System Requirements:

### 2.1 Prerequisites

Before setting up the project, ensure you have the following installed:
* **Java SDK 21**
* **Maven 3.8+**
* **spring-boot 3.2+**
* **Docker & Docker Compose**
* **Git**
* **Vue.js (https://v3.ru.vuejs.org/guide/installation.html)**
* **npm (https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)**
* **A modern web browser (Chrome, Firefox, Edge, etc.)**

## 3. Project Setup and Execution

### 3.1 Cloning the Repository
   ```bash
    git clone https://github.com/HiteshKolluru-asu/SER516-Spr25-JavaB-3.git
```

### 3.2 Building and Running the Application

Step-1. **Navigate to the Project root Directory:**
   ```bash
   cd SER516-Spr25-JavaB-3
   ```

Step-2. **Install Dependencies:**
   ```bash
   mvn clean install -P run-all
   ```
Step-3. **Building and Running the Application:**
   ```bash
   docker-compose up --build
   ```
Step-4. **Install npm:**
   ```
   cd frontendvue
   ```
   ```
   npm install
   ```
Step-5. **To Run the Application:**
   ```
   npm run serve
   ```
- Use the localhost URL appears after running above command

Step-6 **Uploading Input Value**

- The expected input is a ZIP file containing Java source code for Afferent and Efferent coupling analysis.
- You can use sample test files located in the TestData folder for testing.
- The expected input is github repository URL which has defects data for getting defect density analysis.
- Please use below sample github urls provided under TestData folder.

## 4. Application Features
### 4.1 Quality Metrics Computation

The system calculates:

* Afferent Coupling (Ca): Measures the number of incoming dependencies. Benchmark Value: 5
* Efferent Coupling (Ce): Measures the number of outgoing dependencies. Benchmark Value: 5
* Instability metric (I) : Measures how susceptible a software package is, to changes based on its outgoing dependencies. Benchmark Value: 0.5
* Defect Density: Number of defects per lines of code. Benchmark Value: 200
* Visualization of the metrics: Line graph for Afferent, Efferent and Instability metrics. Bar Chart for Defect Density.
* Added a Benchmark value for each metric and graph shows the metrics against this idealized baseline(benchmark) over time.

### 4.2 File Storage and Comparison

* **Max Upload Size:** 100MB
* **History Limit:** Only the last 5 files per project are stored for reference.
* **Comparison Mechanism:** File names are used to compare different versions.

## 5. API Documentation

Each API has a dedicated README inside its respective directory.
Refer to each README.md for full API details.

## 6. Development Workflow

### 6.1 Branching Strategy

- All development is done in **task branches** and **userstory branches**
- Completed stories are merged into **Period branches** (`Period-YY`).
- Before the period ends, `Period-YY` is merged into `main`.
- Releases are tagged as **`Period-YY-release-v"majorversionnumber"."minorversionnumber"`**.

### 6.2 Code Quality

- **Unit Testing:** JUnit is used for test coverage.
- **CI/CD:** GitHub Actions runs automated tests on each commit.
- **Code Style:** Follows standard Java coding conventions.

