FROM openjdk:17.0.2-oraclelinux8
EXPOSE 8085
ADD rain-forecast-service-0.1.0.jar /opt/lib/rain-forecast-service-0.1.0.jar
WORKDIR /opt/lib
ENTRYPOINT ["java", "-jar", "/opt/lib/rain-forecast-service-0.1.0.jar"]
