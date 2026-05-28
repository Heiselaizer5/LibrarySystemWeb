import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String password;
    private final String role;
    private final String phoneNumber;

    public User(String username, String password, String role) {
        this(username, password, role, "");
    }

    public User(String username, String password, String role, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
}
