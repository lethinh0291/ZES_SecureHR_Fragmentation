package securehr.model;

public class AppUser {
    private final String username;
    private final String fullName;
    private final String role;

    public AppUser(String username, String fullName, String role) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}
