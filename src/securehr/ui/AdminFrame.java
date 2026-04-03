package securehr.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
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

public class AdminFrame extends JFrame {
    private final EmployeeService employeeService;
    private final JTable table;
    private final AppUser currentUser;
    private final JTextField searchField;
    private final javax.swing.JLabel totalEmployeesValue;
    private final javax.swing.JLabel departmentsValue;
    private final javax.swing.JLabel totalPayrollValue;
    private TableRowSorter<DefaultTableModel> sorter;

    public AdminFrame(AppUser user) {
        super("ZES-SecureHR - Bảng điều khiển quản trị");
        this.currentUser = user;
        this.employeeService = new EmployeeService();
        this.table = new JTable();
        this.searchField = new JTextField(24);
        this.totalEmployeesValue = UiTheme.createMetricValue("0");
        this.departmentsValue = UiTheme.createMetricValue("0");
        this.totalPayrollValue = UiTheme.createMetricValue("0 VND");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        sidebar.add(UiTheme.createSidebarButton("Bảng điều khiển", true));
        sidebar.add(Box.createVerticalStrut(8));

        JButton employeeBtn = UiTheme.createSidebarButton("Nhân viên", false);
        employeeBtn.addActionListener(e -> {
            new EmployeeFrame(currentUser);
            dispose();
        });
        sidebar.add(employeeBtn);
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
                "Bảng điều khiển quản trị");
        topbar.add(titleBlock, BorderLayout.CENTER);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        headerRight.add(UiTheme.createStatusIndicator(""));
        headerRight.add(UiTheme.createRoleLabel("QUẢN TRỊ"));
        headerRight.add(UiTheme.createUserInfo(currentUser.getFullName(), currentUser.getUsername()));
        topbar.add(headerRight, BorderLayout.EAST);

        JPanel metricGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        metricGrid.setOpaque(false);
        metricGrid.add(UiTheme.createMetricCard("Tổng số nhân viên", totalEmployeesValue));
        metricGrid.add(UiTheme.createMetricCard("Phòng ban", departmentsValue));
        metricGrid.add(UiTheme.createMetricCard("Tổng quỹ lương", totalPayrollValue));

        JPanel centerCard = UiTheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(10, 10));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);
        cardHeader.add(UiTheme.createSectionTitle("Danh sách nhân viên và dữ liệu lương"), BorderLayout.WEST);
        cardHeader.add(UiTheme.createHint("Dữ liệu đã tách DB1/DB2 và đồng bộ qua stored procedure"),
                BorderLayout.SOUTH);
        centerCard.add(cardHeader, BorderLayout.NORTH);

        JPanel toolbar = buildActionPanel();
        centerCard.add(toolbar, BorderLayout.SOUTH);

        UiTheme.styleTable(table);
        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleScrollPane(tableScroll);
        centerCard.add(tableScroll, BorderLayout.CENTER);

        content.add(topbar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(metricGrid, BorderLayout.NORTH);
        center.add(centerCard, BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);

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

        JButton refreshBtn = UiTheme.createSecondaryButton("Làm mới");
        refreshBtn.addActionListener(e -> refreshTable());

        JButton addBtn = UiTheme.createPrimaryButton("Thêm mới");
        addBtn.addActionListener(e -> addEmployee());

        JButton editBtn = UiTheme.createPrimaryButton("Chỉnh sửa");
        editBtn.addActionListener(e -> editEmployee());

        JButton deleteBtn = UiTheme.createDangerButton("Xóa");
        deleteBtn.addActionListener(e -> deleteEmployee());

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

    private void refreshTable() {
        try {
            DefaultTableModel model = employeeService.getAdminFullData();
            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            applySearchFilter();
            updateMetrics(model);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void applySearchFilter() {
        if (sorter == null) {
            return;
        }

        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
    }

    private void updateMetrics(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        totalEmployeesValue.setText(String.valueOf(rowCount));

        int deptCol = findColumnIndex(model, "PhongBan");
        Set<String> departments = new HashSet<>();
        if (deptCol >= 0) {
            for (int i = 0; i < rowCount; i++) {
                Object value = model.getValueAt(i, deptCol);
                if (value != null && !value.toString().trim().isEmpty()) {
                    departments.add(value.toString().trim());
                }
            }
        }
        departmentsValue.setText(String.valueOf(departments.size()));

        int tongLuongCol = findColumnIndex(model, "TongLuong");
        double totalPayroll = 0;
        if (tongLuongCol >= 0) {
            for (int i = 0; i < rowCount; i++) {
                totalPayroll += safeToDouble(model.getValueAt(i, tongLuongCol));
            }
        }

        DecimalFormat f = new DecimalFormat("#,##0");
        totalPayrollValue.setText(f.format(totalPayroll) + " VND");
    }

    private int findColumnIndex(DefaultTableModel model, String columnName) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(String.valueOf(model.getColumnName(i)))) {
                return i;
            }
        }
        return -1;
    }

    private double safeToDouble(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Double.parseDouble(value.toString().replace(",", ""));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void addEmployee() {
        JTextField maNvField = new JTextField();
        JTextField hoTenField = new JTextField();
        JTextField phongBanField = new JTextField();
        JTextField chucVuField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField luongCoBanField = new JTextField();
        JTextField heSoField = new JTextField();
        JTextField phuCapField = new JTextField();
        JTextField thuongField = new JTextField();

        UiTheme.styleTextField(maNvField);
        UiTheme.styleTextField(hoTenField);
        UiTheme.styleTextField(phongBanField);
        UiTheme.styleTextField(chucVuField);
        UiTheme.styleTextField(emailField);
        UiTheme.styleTextField(luongCoBanField);
        UiTheme.styleTextField(heSoField);
        UiTheme.styleTextField(phuCapField);
        UiTheme.styleTextField(thuongField);

        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        inputPanel.add(UiTheme.createSectionTitle("Mã NV"));
        inputPanel.add(maNvField);
        inputPanel.add(UiTheme.createSectionTitle("Họ và tên"));
        inputPanel.add(hoTenField);
        inputPanel.add(UiTheme.createSectionTitle("Phòng Ban"));
        inputPanel.add(phongBanField);
        inputPanel.add(UiTheme.createSectionTitle("Chức Vụ"));
        inputPanel.add(chucVuField);
        inputPanel.add(UiTheme.createSectionTitle("Email"));
        inputPanel.add(emailField);
        inputPanel.add(UiTheme.createSectionTitle("Luơng Cơ Bản"));
        inputPanel.add(luongCoBanField);
        inputPanel.add(UiTheme.createSectionTitle("Hệ Số"));
        inputPanel.add(heSoField);
        inputPanel.add(UiTheme.createSectionTitle("Phụ Cấp"));
        inputPanel.add(phuCapField);
        inputPanel.add(UiTheme.createSectionTitle("Thưởng"));
        inputPanel.add(thuongField);

        int option = JOptionPane.showConfirmDialog(
                this,
                inputPanel,
                "Thêm nhân viên và lương",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (maNvField.getText().trim().isEmpty() || hoTenField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "MaNV va Ho ten khong duoc de trong.");
                return;
            }

            employeeService.addEmployeeAndSalary(
                    maNvField.getText().trim(),
                    hoTenField.getText().trim(),
                    phongBanField.getText().trim(),
                    chucVuField.getText().trim(),
                    emailField.getText().trim(),
                    Double.parseDouble(luongCoBanField.getText().trim()),
                    Double.parseDouble(heSoField.getText().trim()),
                    Double.parseDouble(phuCapField.getText().trim()),
                    Double.parseDouble(thuongField.getText().trim()));

            JOptionPane.showMessageDialog(this, "Thêm thành công.");
            refreshTable();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Các trường lương phải là số.");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteEmployee() {
        String maNv = JOptionPane.showInputDialog(this, "Nhập mã nhân viên để xoá:");
        if (maNv == null) {
            return;
        }

        maNv = maNv.trim();
        if (maNv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập MaNV để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn chắc chắn muốn xóa nhân viên " + maNv + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            employeeService.deleteEmployeeAndSalary(maNv);
            JOptionPane.showMessageDialog(this, "Xoá thành công.");
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Lỗi thao tác",
                JOptionPane.ERROR_MESSAGE);
    }

    private void editEmployee() {
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
