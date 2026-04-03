package securehr.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import securehr.db.DatabaseManager;
import securehr.model.AppUser;

public class AuthService {
    public AppUser authenticate(String username, String password) throws SQLException {
        String sql = "SELECT Username, FullName, RoleName "
                + "FROM dbo.AppUsers "
                + "WHERE Username = ? AND PasswordHash = ? AND IsActive = 1";

        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AppUser(
                            rs.getString("Username"),
                            rs.getString("FullName"),
                            rs.getString("RoleName"));
                }
                return null;
            }
        }
    }
}
