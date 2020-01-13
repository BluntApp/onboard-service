FROM java:8
ADD target/onboard-service.jar onboard-service.jar
ENTRYPOINT ["java","-jar","onboard-service.jar"]
