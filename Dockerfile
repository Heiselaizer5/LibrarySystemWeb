FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY *.java ./
RUN javac Main.java
EXPOSE 7860
CMD ["java", "Main"]
