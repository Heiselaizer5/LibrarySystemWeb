import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class BorrowRecord {
    String isbn;
    String username;
    String dueDate;

    BorrowRecord(String isbn, String username) {
        this.isbn = isbn;
        this.username = username;
        this.dueDate = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
    }

    private LocalDateTime parsed() {
        return LocalDateTime.parse(dueDate, DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
    }

    boolean isOverdue() {
        return parsed().isBefore(LocalDateTime.now());
    }

    long getDaysOverdue() {
        long days = ChronoUnit.DAYS.between(parsed(), LocalDateTime.now());
        return days > 0 ? days : 0;
    }
}
