FROM amazoncorretto:21-al2023

WORKDIR /app

COPY build/libs/gym-auth-server-*.jar app.jar

EXPOSE 8083

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=5 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
