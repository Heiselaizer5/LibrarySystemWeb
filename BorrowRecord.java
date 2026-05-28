import java.time.LocalDate;

class BorrowRecord {
    static final double DAILY_FEE = 1.0;
    String isbn;
    String username;
    String dueDate;

    BorrowRecord(String isbn, String username) {
        this.isbn = isbn;
        this.username = username;
        this.dueDate = LocalDate.now().plusDays(14).toString();
    }

    boolean isOverdue() {
        return LocalDate.parse(dueDate).isBefore(LocalDate.now());
    }

    double getLateFee() {
        long daysLate = LocalDate.now().toEpochDay() - LocalDate.parse(dueDate).toEpochDay();
        return daysLate > 0 ? daysLate * DAILY_FEE : 0;
    }

    long getDaysOverdue() {
        long days = LocalDate.now().toEpochDay() - LocalDate.parse(dueDate).toEpochDay();
        return days > 0 ? days : 0;
    }
}
