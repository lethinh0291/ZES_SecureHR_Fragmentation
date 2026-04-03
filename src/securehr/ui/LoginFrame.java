package securehr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import securehr.model.AppUser;
import securehr.service.AuthService;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthService authService;

    public LoginFrame() {
        super("SecureHR - Đăng nhập");
        this.authService = new AuthService();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(900, 520));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel wrapper = UiTheme.createBackgroundPanel();
        wrapper.setLayout(new BorderLayout());

        JPanel leftBrand = new JPanel(new BorderLayout(0, 18));
        leftBrand.setBackground(UiTheme.PRIMARY_DARK);
        leftBrand.setBorder(BorderFactory.createEmptyBorder(44, 38, 44, 38));
        leftBrand.setPreferredSize(new Dimension(360, 0));

        JPanel brandText = new JPanel();
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));
        brandText.setOpaque(false);

        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrap.setOpaque(false);
        JLabel brandTitle = UiTheme.createTitle("ZES-SecureHR");
        brandTitle.setForeground(Color.WHITE);
        titleWrap.add(brandTitle);

        JPanel subtitleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subtitleWrap.setOpaque(false);
        JLabel subtitle = UiTheme.createSubtitle("Nền tảng quản lý nhân sự và bảng lương");
        subtitle.setForeground(new Color(200, 214, 237));
        subtitleWrap.add(subtitle);

        JPanel featureWrap = new JPanel();
        featureWrap.setOpaque(false);
        featureWrap.setLayout(new BoxLayout(featureWrap, BoxLayout.Y_AXIS));
        featureWrap.add(createBrandFeature("Bảng điều khiển phân quyền cho Quản trị/HR/Kế toán"));
        featureWrap.add(Box.createVerticalStrut(10));
        featureWrap.add(createBrandFeature("Dữ liệu nhạy cảm tách DB1/DB2 theo mô hình phân mảnh"));
        featureWrap.add(Box.createVerticalStrut(10));
        featureWrap.add(createBrandFeature("Tác vụ thêm/xóa đồng bộ toàn vẹn qua stored procedure"));

        brandText.add(titleWrap);
        brandText.add(Box.createVerticalStrut(10));
        brandText.add(subtitleWrap);
        brandText.add(Box.createVerticalStrut(26));
        brandText.add(featureWrap);

        leftBrand.add(brandText, BorderLayout.NORTH);

        JPanel rightForm = new JPanel(new GridBagLayout());
        rightForm.setOpaque(false);
        rightForm.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JPanel card = UiTheme.createCardPanel();
        card.setPreferredSize(new Dimension(420, 360));
        card.setLayout(new BorderLayout(14, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        top.add(UiTheme.createSectionTitle("Đăng nhập hệ thống"), BorderLayout.NORTH);
        top.add(UiTheme.createSubtitle("Sử dụng tài khoản ứng dụng để truy cập hệ thống"), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        gbc.gridy = 0;
        formPanel.add(UiTheme.createSectionTitle("Tên đăng nhập"), gbc);
        gbc.gridy = 1;
        usernameField = new JTextField();
        UiTheme.styleTextField(usernameField);
        formPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        formPanel.add(UiTheme.createSectionTitle("Mật khẩu"), gbc);
        gbc.gridy = 3;
        passwordField = new JPasswordField();
        UiTheme.stylePasswordField(passwordField);
        formPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        JLabel hints = UiTheme.createHint("Tài khoản demo: admin/admin123, hr/hr123, accountant/acc123");
        formPanel.add(hints, gbc);

        JButton loginButton = UiTheme.createPrimaryButton("Đăng nhập");
        loginButton.addActionListener(e -> handleLogin());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);

        card.add(top, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        rightForm.add(card);

        wrapper.add(leftBrand, BorderLayout.WEST);
        wrapper.add(rightForm, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel createBrandFeature(String text) {
        JLabel label = UiTheme.createSubtitle("• " + text);
        label.setForeground(new Color(176, 193, 222));
        return label;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            AppUser user = authService.authenticate(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Thông tin đăng nhập không hợp lệ hoặc tài khoản không hoạt động.");
                return;
            }

            openDashboard(user);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot connect/authenticate with SQL Server.\n" + ex.getMessage(),
                    "Lỗi cơ sở dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(AppUser user) {
        dispose();

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            new AdminFrame(user).setVisible(true);
            return;
        }

        new UserFrame(user).setVisible(true);
    }
}
