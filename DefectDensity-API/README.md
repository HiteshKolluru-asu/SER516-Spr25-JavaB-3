Running the application

to Run move to DefectDensity-API directory and then run
mvn spring-boot:run

Once running use the following URL
http://localhost:8083/api/code-analysis/analyze [POST]
variable name - 'file'
expected input value - a zip file containing java code.

.jar file must exist so use below command before building docker container
mvn clean package 

to build docker container
docker build --no-cache -t defectdensity .

to run
docker run --name defectdensity_container -p 8083:8083 -d defectdensity

to start the container
docker start defectdensity_container

to stop the container
docker stop defectdensity_container

simply start container and ping local host

to build docker container
docker build -t [DockerImageName]

to run
docker run --name [DockerContainerName] -p 8083:8083 -d [DockerImageName]

to start the container
docker start [DockerContainerName]

to stop the container
docker stop [DockerContainerName]


