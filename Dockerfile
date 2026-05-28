FROM openjdk:17-slim
WORKDIR /app
COPY Main.java Book.java User.java Request.java BorrowRecord.java ./
RUN javac Main.java
CMD ["java", "Main"]
