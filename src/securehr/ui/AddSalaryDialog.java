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

public class AddSalaryDialog extends JDialog {
    private final JTextField maNVField;
    private final JTextField luongCobanField;
    private final JTextField hesoField;
    private final JTextField phuCapField;
    private final JTextField thuongField;
    private boolean confirmed = false;

    public AddSalaryDialog(JFrame parent) {
        super(parent, "Thêm dữ liệu lương", true);

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

        // Mã NV
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        maNVField = new JTextField();
        UiTheme.styleTextField(maNVField);
        formPanel.add(maNVField, gbc);

        // Lương cơ bản
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Lương cơ bản:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        luongCobanField = new JTextField();
        UiTheme.styleTextField(luongCobanField);
        formPanel.add(luongCobanField, gbc);

        // Hệ số
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Hệ số:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        hesoField = new JTextField();
        UiTheme.styleTextField(hesoField);
        formPanel.add(hesoField, gbc);

        // Phụ cấp
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Phụ cấp:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phuCapField = new JTextField();
        UiTheme.styleTextField(phuCapField);
        formPanel.add(phuCapField, gbc);

        // Thưởng
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Thưởng:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        thuongField = new JTextField();
        UiTheme.styleTextField(thuongField);
        formPanel.add(thuongField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton addBtn = UiTheme.createPrimaryButton("Thêm");
        JButton cancelBtn = UiTheme.createSecondaryButton("Hủy");

        addBtn.addActionListener(e -> handleAdd());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void handleAdd() {
        try {
            String maNV = maNVField.getText().trim();
            String luongCobanStr = luongCobanField.getText().trim();
            String hesoStr = hesoField.getText().trim();
            String phuCapStr = phuCapField.getText().trim();
            String thuongStr = thuongField.getText().trim();

            if (maNV.isEmpty() || luongCobanStr.isEmpty() || hesoStr.isEmpty() || phuCapStr.isEmpty()
                    || thuongStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double luongCoban = Double.parseDouble(luongCobanStr);
            double heso = Double.parseDouble(hesoStr);
            double phuCap = Double.parseDouble(phuCapStr);
            double thuong = Double.parseDouble(thuongStr);

            // Thêm dữ liệu vào DB2
            String sqlDb2 = "INSERT INTO dbo.Salaries (MaNV, LuongCoBan, HeSo, PhuCap, Thuong) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getDb2Connection();
                    PreparedStatement ps = conn.prepareStatement(sqlDb2)) {
                ps.setString(1, maNV);
                ps.setDouble(2, luongCoban);
                ps.setDouble(3, heso);
                ps.setDouble(4, phuCap);
                ps.setDouble(5, thuong);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Thêm dữ liệu lương thành công!", "Thành công",
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
