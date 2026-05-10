FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw -B -q -DskipTests dependency:go-offline

COPY src/ src/

RUN ./mvnw -B -q -DskipTests package


FROM eclipse-temurin:21-jre-jammy

RUN apt-get update \
  && apt-get install -y --no-install-recommends wget \
  && groupadd --system app \
  && useradd --system --gid app --create-home --home-dir /app app \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ENV SERVER_PORT=8080

COPY --from=build --chown=app:app /workspace/target/*.jar app.jar

USER app

EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -q -O /dev/null "http://localhost:${SERVER_PORT}/actuator/health/readiness" || exit 1

# JVM tuned for 1GB container limit
ENTRYPOINT [ \
  "java", \
  "-Xms128m", \
  "-Xmx384m", \
  "-XX:MaxMetaspaceSize=128m", \
  "-XX:+UseSerialGC", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "/app/app.jar" \
]
