import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 2L;
    private final String username;
    private final String password;
    private final String role;
    private final String phoneNumber;
    private final String securityQuestion;
    private final String securityAnswer;

    public User(String username, String password, String role) {
        this(username, password, role, "", "", "");
    }

    public User(String username, String password, String role, String phoneNumber) {
        this(username, password, role, phoneNumber, "", "");
    }

    public User(String username, String password, String role, String phoneNumber, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
}
