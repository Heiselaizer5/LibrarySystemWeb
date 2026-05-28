import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    private final String isbn;
    private final String username;
    private final String timestamp;
    private String status; // pending, approved, rejected

    public Request(String isbn, String username) {
        this.isbn = isbn;
        this.username = username;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.status = "pending";
    }

    public String getIsbn() { return isbn; }
    public String getUsername() { return username; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
