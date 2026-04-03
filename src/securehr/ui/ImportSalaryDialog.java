package securehr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import securehr.db.DatabaseManager;

public class ImportSalaryDialog extends JDialog {
    private final JTextArea csvArea;
    private boolean confirmed = false;

    public ImportSalaryDialog(JFrame parent) {
        super(parent, "Nhập dữ liệu lương từ CSV", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 420));
        setLocationRelativeTo(parent);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Hướng dẫn
        JLabel instructionLabel = new JLabel(
                "<html>Định dạng CSV (MaNV, LuongCoBan, HeSo, PhuCap, Thuong):<br>NV001,5000000,1.0,500000,1000000<br>NV002,4500000,0.9,400000,800000</html>");
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Text area
        csvArea = new JTextArea(10, 50);
        csvArea.setLineWrap(true);
        csvArea.setWrapStyleWord(true);
        csvArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(csvArea);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton importBtn = UiTheme.createPrimaryButton("Nhập");
        JButton cancelBtn = UiTheme.createSecondaryButton("Hủy");

        importBtn.addActionListener(e -> handleImport());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(importBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(instructionLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void handleImport() {
        String csv = csvArea.getText().trim();
        if (csv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập dữ liệu CSV", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] lines = csv.split("\n");
        int successCount = 0;
        int errorCount = 0;

        try (Connection conn = DatabaseManager.getDb2Connection()) {
            String sql = "INSERT INTO dbo.Salaries (MaNV, LuongCoBan, HeSo, PhuCap, Thuong) VALUES (?, ?, ?, ?, ?)";

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length != 5) {
                        errorCount++;
                        continue;
                    }

                    String maNV = parts[0].trim();
                    double luongCoban = Double.parseDouble(parts[1].trim());
                    double heso = Double.parseDouble(parts[2].trim());
                    double phuCap = Double.parseDouble(parts[3].trim());
                    double thuong = Double.parseDouble(parts[4].trim());

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maNV);
                        ps.setDouble(2, luongCoban);
                        ps.setDouble(3, heso);
                        ps.setDouble(4, phuCap);
                        ps.setDouble(5, thuong);
                        ps.executeUpdate();
                        successCount++;
                    }
                } catch (Exception ex) {
                    errorCount++;
                }
            }

            String message = String.format("Kết quả nhập dữ liệu:\n- Thành công: %d\n- Lỗi: %d", successCount,
                    errorCount);
            JOptionPane.showMessageDialog(this, message, "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);

            if (successCount > 0) {
                confirmed = true;
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
