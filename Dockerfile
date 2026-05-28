FROM eclipse-temurin:17-jre
WORKDIR /app
COPY *.class ./
COPY *.java ./
EXPOSE 7860
CMD ["java", "Main"]
