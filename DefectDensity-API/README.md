Running the application

if the .jar file is in target
java -jar target/DefectDensityAPI.jar

else use 
mvn spring-boot:run


To build the .jar file
navigate to my personal directory
cd /Users/twisted_fate/Desktop/SER516-Spr25-JavaB-3/DefectDensity-API

mvn clean package
This compiles the code, runs tests, and generates a .jar file inside the target/ directory.

ls target/*.jar
finds the .jar files

then use -> java -jar {followed by the name of the jar file}
in our case

java -jar target/DefectDensityAPI.jar


use mv target/DefectDensityAPI{diffName}jar target/DefectDensityAPI.jar 

to rename for simple usage.

Once running use the following URL
http://localhost:8085/api/code-analysis/analyze [POST]
variable name - 'file'
expected input value - a zip file containing java code.
