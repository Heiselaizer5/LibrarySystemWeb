FROM openjdk:17-slim
WORKDIR /app
COPY *.java ./
RUN javac Main.java
EXPOSE 7860
CMD ["java", "Main"]
