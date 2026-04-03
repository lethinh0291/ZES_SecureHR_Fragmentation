package securehr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import securehr.model.AppUser;
import securehr.service.EmployeeService;

public class EmployeeFrame extends JFrame {
    private final EmployeeService employeeService;
    private final JTable table;
    private final AppUser currentUser;
    private final JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public EmployeeFrame(AppUser user) {
        super("ZES-SecureHR - Quản lý nhân viên");
        this.currentUser = user;
        this.employeeService = new EmployeeService();
        this.table = new JTable();
        this.searchField = new JTextField(24);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1320, 760));
        setLocationRelativeTo(null);
        setContentPane(UiTheme.createBackgroundPanel());
        getContentPane().setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = UiTheme.createSidebar();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 0));

        sidebar.add(UiTheme.createSidebarTitle("ZES-SecureHR"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(UiTheme.createSidebarSubtitle("Bảng điều khiển"));
        sidebar.add(Box.createVerticalStrut(24));

        JButton dashboardBtn = UiTheme.createSidebarButton("Bảng điều khiển", false);
        dashboardBtn.addActionListener(e -> {
            new AdminFrame(currentUser);
            dispose();
        });
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(UiTheme.createSidebarButton("Nhân viên", true));
        sidebar.add(Box.createVerticalStrut(8));

        JButton salaryBtn = UiTheme.createSidebarButton("Lương & Thưởng", false);
        salaryBtn.addActionListener(e -> {
            new SalaryFrame(currentUser);
            dispose();
        });
        sidebar.add(salaryBtn);
        sidebar.add(Box.createVerticalStrut(8));

        JButton reportBtn = UiTheme.createSidebarButton("Báo cáo", false);
        reportBtn.addActionListener(e -> {
            new ReportFrame(currentUser);
            dispose();
        });
        sidebar.add(reportBtn);
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = UiTheme.createDangerButton("Đăng xuất");
        logoutBtn.addActionListener(e -> handleLogout());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(UiTheme.createSidebarSubtitle("Vai trò: QUẢN TRỊ"));
        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel topbar = UiTheme.createTopBar();
        topbar.setLayout(new BorderLayout());

        JPanel titleBlock = UiTheme.createHeaderTitleBlock(
                "Danh sách nhân viên");
        topbar.add(titleBlock, BorderLayout.CENTER);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        headerRight.add(UiTheme.createStatusIndicator("Hệ thống hoạt động ổn định"));
        headerRight.add(UiTheme.createRoleLabel("QUẢN TRỊ"));
        headerRight.add(UiTheme.createUserInfo(currentUser.getFullName(), currentUser.getUsername()));
        topbar.add(headerRight, BorderLayout.EAST);

        JPanel centerCard = UiTheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(10, 10));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.add(UiTheme.createSectionTitle("Danh sách nhân viên"), BorderLayout.WEST);
        cardHeader.add(UiTheme.createHint("Hiển thị toàn bộ dữ liệu nhân viên từ hệ thống"),
                BorderLayout.SOUTH);
        centerCard.add(cardHeader, BorderLayout.NORTH);

        JPanel toolbar = buildActionPanel();
        centerCard.add(toolbar, BorderLayout.SOUTH);

        UiTheme.styleTable(table);
        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleScrollPane(tableScroll);
        centerCard.add(tableScroll, BorderLayout.CENTER);

        content.add(topbar, BorderLayout.NORTH);
        content.add(centerCard, BorderLayout.CENTER);

        return content;
    }

    private JPanel buildActionPanel() {
        JPanel toolbar = UiTheme.createToolbarPanel();
        toolbar.setLayout(new BorderLayout(10, 0));

        UiTheme.styleTextField(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearchFilter();
            }
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(UiTheme.createHint("Tìm kiếm:"));
        left.add(searchField);

        JButton addBtn = UiTheme.createPrimaryButton("Thêm");
        JButton editBtn = UiTheme.createPrimaryButton("Chỉnh sửa");
        JButton deleteBtn = UiTheme.createDangerButton("Xóa");
        JButton refreshBtn = UiTheme.createPrimaryButton("Làm mới");

        addBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng thêm sẽ được cập nhật",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        editBtn.addActionListener(e -> handleEditEmployee());
        deleteBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng xóa sẽ được cập nhật",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        refreshBtn.addActionListener(e -> refreshTable());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(addBtn);
        right.add(editBtn);
        right.add(deleteBtn);
        right.add(refreshBtn);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    private void applySearchFilter() {
        if (sorter == null)
            return;
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }
    }

    private void refreshTable() {
        try {
            DefaultTableModel model = employeeService.getAdminFullData();
            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để chỉnh sửa", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String maNV = model.getValueAt(selectedRow, 0).toString();
        String hoTen = model.getValueAt(selectedRow, 1).toString();
        String phongBan = model.getValueAt(selectedRow, 2).toString();
        String chucVu = model.getValueAt(selectedRow, 3).toString();
        String email = model.getValueAt(selectedRow, 4).toString();

        EditEmployeeDialog dialog = new EditEmployeeDialog(this, maNV, hoTen, phongBan, chucVu, email);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            refreshTable();
        }
    }

    private void handleLogout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
