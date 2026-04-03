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

public class UserFrame extends JFrame {
    private final EmployeeService employeeService;
    private final AppUser currentUser;
    private final JTable table;
    private final JTextField searchField;
    private final javax.swing.JLabel roleValue;
    private final javax.swing.JLabel rowCountValue;
    private TableRowSorter<DefaultTableModel> sorter;

    public UserFrame(AppUser user) {
        super("ZES-SecureHR - Bảng người dùng");
        this.currentUser = user;
        this.employeeService = new EmployeeService();
        this.table = new JTable();
        this.searchField = new JTextField(20);
        this.roleValue = UiTheme.createMetricValue(user.getRole());
        this.rowCountValue = UiTheme.createMetricValue("0");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1220, 700));
        setLocationRelativeTo(null);
        setContentPane(UiTheme.createBackgroundPanel());
        getContentPane().setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        loadByRole();
        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = UiTheme.createSidebar();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));

        sidebar.add(UiTheme.createSidebarTitle("ZES-SecureHR"));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(UiTheme.createSidebarSubtitle("Cổng tự phục vụ"));
        sidebar.add(Box.createVerticalStrut(24));

        sidebar.add(UiTheme.createSidebarButton("Tổng quan", true));
        sidebar.add(Box.createVerticalStrut(8));

        JButton dataBtn = UiTheme.createSidebarButton("Dữ liệu được cấp", false);
        dataBtn.addActionListener(e -> {
            new DataAccessFrame(currentUser);
            dispose();
        });
        sidebar.add(dataBtn);
        sidebar.add(Box.createVerticalStrut(8));

        JButton statsBtn = UiTheme.createSidebarButton("Thống kê", false);
        statsBtn.addActionListener(e -> {
            new StatisticsFrame(currentUser);
            dispose();
        });
        sidebar.add(statsBtn);
        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = UiTheme.createDangerButton("Đăng xuất");
        logoutBtn.addActionListener(e -> handleLogout());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(UiTheme.createSidebarSubtitle("Chức vụ: " + currentUser.getRole()));
        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel topbar = UiTheme.createTopBar();
        topbar.setLayout(new BorderLayout());

        JPanel titleBlock = UiTheme.createHeaderTitleBlock(
                "Bảng người dùng");
        topbar.add(titleBlock, BorderLayout.CENTER);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);
        headerRight.add(UiTheme.createStatusIndicator("Đang đồng bộ dữ liệu"));
        headerRight.add(UiTheme.createRoleLabel(currentUser.getRole()));
        headerRight.add(UiTheme.createUserInfo(currentUser.getFullName(), currentUser.getUsername()));
        topbar.add(headerRight, BorderLayout.EAST);

        JPanel metricGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        metricGrid.setOpaque(false);
        metricGrid.add(UiTheme.createMetricCard("Vai trò hiện tại", roleValue));
        metricGrid.add(UiTheme.createMetricCard("Số dòng dữ liệu", rowCountValue));

        JPanel dataCard = UiTheme.createCardPanel();
        dataCard.setLayout(new BorderLayout(10, 10));
        dataCard.add(UiTheme.createSectionTitle("Dữ liệu được phép truy cập"), BorderLayout.NORTH);

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

        JButton refreshButton = UiTheme.createPrimaryButton("Làm mới");
        JButton editButton = UiTheme.createPrimaryButton("Chỉnh sửa");
        editButton.addActionListener(e -> handleEditData());
        refreshButton.addActionListener(e -> loadByRole());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(editButton);
        right.add(refreshButton);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        dataCard.add(toolbar, BorderLayout.SOUTH);

        UiTheme.styleTable(table);
        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleScrollPane(tableScroll);
        dataCard.add(tableScroll, BorderLayout.CENTER);

        content.add(topbar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(metricGrid, BorderLayout.NORTH);
        center.add(dataCard, BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);

        return content;
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

    private void handleLogout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void loadByRole() {
        try {
            DefaultTableModel model;
            if ("ACCOUNTANT".equalsIgnoreCase(currentUser.getRole())) {
                model = employeeService.getSalaryDataForAccountant();
            } else {
                // Default user (HR_USER) can only read public profile data from DB1.
                model = employeeService.getPublicEmployeeData();
            }

            table.setModel(model);
            sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);
            rowCountValue.setText(String.valueOf(model.getRowCount()));
            applySearchFilter();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Lỗi tải dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditData() {
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
            loadByRole();
        }
    }
}
