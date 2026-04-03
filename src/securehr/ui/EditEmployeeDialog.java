package securehr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import securehr.db.DatabaseManager;

public class EditEmployeeDialog extends JDialog {
    private final JTextField maNVField;
    private final JTextField hoTenField;
    private final JTextField phongBanField;
    private final JTextField chucVuField;
    private final JTextField emailField;
    private boolean confirmed = false;

    public EditEmployeeDialog(JFrame parent, String maNV, String hoTen, String phongBan, String chucVu,
            String email) {
        super(parent, "Chỉnh sửa thông tin nhân viên", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(450, 420));
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Mã NV (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField maNVReadOnly = new JTextField(maNV);
        maNVReadOnly.setEditable(false);
        maNVReadOnly.setBackground(java.awt.Color.LIGHT_GRAY);
        formPanel.add(maNVReadOnly, gbc);
        this.maNVField = maNVReadOnly;

        // Họ tên
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        hoTenField = new JTextField(hoTen);
        UiTheme.styleTextField(hoTenField);
        formPanel.add(hoTenField, gbc);

        // Phòng ban
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Phòng ban:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phongBanField = new JTextField(phongBan);
        UiTheme.styleTextField(phongBanField);
        formPanel.add(phongBanField, gbc);

        // Chức vụ
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        chucVuField = new JTextField(chucVu);
        UiTheme.styleTextField(chucVuField);
        formPanel.add(chucVuField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = new JTextField(email);
        UiTheme.styleTextField(emailField);
        formPanel.add(emailField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton saveBtn = UiTheme.createPrimaryButton("Lưu");
        JButton cancelBtn = UiTheme.createSecondaryButton("Hủy");

        saveBtn.addActionListener(e -> handleSave(maNV));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void handleSave(String maNV) {
        try {
            String hoTen = hoTenField.getText().trim();
            String phongBan = phongBanField.getText().trim();
            String chucVu = chucVuField.getText().trim();
            String email = emailField.getText().trim();

            if (hoTen.isEmpty() || phongBan.isEmpty() || chucVu.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update dữ liệu ở DB1
            String sql = "UPDATE dbo.Employees SET HoTen=?, PhongBan=?, ChucVu=?, Email=? WHERE MaNV=?";
            try (Connection conn = DatabaseManager.getDb1Connection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hoTen);
                ps.setString(2, phongBan);
                ps.setString(3, chucVu);
                ps.setString(4, email);
                ps.setString(5, maNV);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            confirmed = true;
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
