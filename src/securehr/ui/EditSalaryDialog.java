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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import securehr.db.DatabaseManager;

public class EditSalaryDialog extends JDialog {
    private final JTextField maNVField;
    private final JTextField luongCobanField;
    private final JTextField hesoField;
    private final JTextField phuCapField;
    private final JTextField thuongField;
    private boolean confirmed = false;

    public EditSalaryDialog(JFrame parent, String maNV, double luongCoban, double heso, double phuCap,
            double thuong) {
        super(parent, "Chỉnh sửa dữ liệu lương", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(420, 380));
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

        // Lương cơ bản
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Lương cơ bản:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        luongCobanField = new JTextField(String.valueOf(luongCoban));
        UiTheme.styleTextField(luongCobanField);
        formPanel.add(luongCobanField, gbc);

        // Hệ số
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Hệ số:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        hesoField = new JTextField(String.valueOf(heso));
        UiTheme.styleTextField(hesoField);
        formPanel.add(hesoField, gbc);

        // Phụ cấp
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Phụ cấp:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phuCapField = new JTextField(String.valueOf(phuCap));
        UiTheme.styleTextField(phuCapField);
        formPanel.add(phuCapField, gbc);

        // Thưởng
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Thưởng:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        thuongField = new JTextField(String.valueOf(thuong));
        UiTheme.styleTextField(thuongField);
        formPanel.add(thuongField, gbc);

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
            String luongCobanStr = luongCobanField.getText().trim();
            String hesoStr = hesoField.getText().trim();
            String phuCapStr = phuCapField.getText().trim();
            String thuongStr = thuongField.getText().trim();

            if (luongCobanStr.isEmpty() || hesoStr.isEmpty() || phuCapStr.isEmpty() || thuongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double luongCoban = Double.parseDouble(luongCobanStr);
            double heso = Double.parseDouble(hesoStr);
            double phuCap = Double.parseDouble(phuCapStr);
            double thuong = Double.parseDouble(thuongStr);

            // Update dữ liệu ở DB2
            String sql = "UPDATE dbo.Salaries SET LuongCoBan=?, HeSo=?, PhuCap=?, Thuong=? WHERE MaNV=?";
            try (Connection conn = DatabaseManager.getDb2Connection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, luongCoban);
                ps.setDouble(2, heso);
                ps.setDouble(3, phuCap);
                ps.setDouble(4, thuong);
                ps.setString(5, maNV);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Cập nhật dữ liệu lương thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            confirmed = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập vào không hợp lệ (phải là số)", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
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
