package securehr.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

import securehr.db.DatabaseManager;
import securehr.util.TableModelUtil;

public class EmployeeService {
    public DefaultTableModel getAdminFullData() throws SQLException {
        String sql = "EXEC dbo.sp_AdminGetEmployees";
        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return TableModelUtil.fromResultSet(rs);
        }
    }

    public DefaultTableModel getPublicEmployeeData() throws SQLException {
        String sql = "SELECT MaNV, HoTen, PhongBan, ChucVu, Email, CreatedAt "
                + "FROM dbo.Employees ORDER BY MaNV";

        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return TableModelUtil.fromResultSet(rs);
        }
    }

    public DefaultTableModel getSalaryDataForAccountant() throws SQLException {
        String sql = "SELECT s.MaNV, e.HoTen, s.LuongCoBan, s.HeSo, s.PhuCap, s.Thuong, "
                + "(s.LuongCoBan * s.HeSo + s.PhuCap + s.Thuong) AS TongLuong "
                + "FROM SecureHR_DB2.dbo.Salaries s "
                + "LEFT JOIN SecureHR_DB1.dbo.Employees e ON e.MaNV = s.MaNV "
                + "ORDER BY s.MaNV";

        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return TableModelUtil.fromResultSet(rs);
        }
    }

    public void addEmployeeAndSalary(
            String maNV,
            String hoTen,
            String phongBan,
            String chucVu,
            String email,
            double luongCoBan,
            double heSo,
            double phuCap,
            double thuong) throws SQLException {
        String sql = "EXEC dbo.sp_AdminInsertEmployeeWithSalary ?,?,?,?,?,?,?,?,?";

        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, hoTen);
            ps.setString(3, phongBan);
            ps.setString(4, chucVu);
            ps.setString(5, email);
            ps.setDouble(6, luongCoBan);
            ps.setDouble(7, heSo);
            ps.setDouble(8, phuCap);
            ps.setDouble(9, thuong);
            ps.executeUpdate();
        }
    }

    public void deleteEmployeeAndSalary(String maNV) throws SQLException {
        String sql = "EXEC dbo.sp_AdminDeleteEmployee ?";

        try (Connection conn = DatabaseManager.getDb1Connection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.executeUpdate();
        }
    }
}
