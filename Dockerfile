FROM amazoncorretto:21
ADD target/demo-jenkins-k8s-0.0.1-SNAPSHOT.jar myporject.jar
ENTRYPOINT ["java", "-jar", "myporject.jar"]
EXPOSE 9090

