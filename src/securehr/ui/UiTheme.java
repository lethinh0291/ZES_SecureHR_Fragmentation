package securehr.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class UiTheme {
    public static final Color BG = new Color(246, 249, 254);
    public static final Color BG_BOTTOM = new Color(233, 242, 253);
    public static final Color CARD_BG = new Color(255, 255, 255, 168);
    public static final Color CARD_BORDER = new Color(255, 255, 255, 198);
    public static final Color PRIMARY = new Color(59, 130, 246);
    public static final Color PRIMARY_DARK = new Color(37, 99, 235);
    public static final Color PRIMARY_TEXT = new Color(255, 255, 255);
    public static final Color DANGER = new Color(190, 38, 61);
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED_TEXT = new Color(71, 85, 105);
    public static final Color BORDER = new Color(223, 231, 243);
    public static final Color SIDEBAR_BG = new Color(37, 99, 235);
    public static final Color SIDEBAR_TEXT = new Color(238, 244, 255);
    public static final Color SIDEBAR_MUTED = new Color(153, 168, 188);
    public static final Color NAV_ACTIVE = new Color(59, 130, 246);
    public static final Color NAV_INACTIVE = new Color(59, 73, 94);

    private static final Color TABLE_ROW_EVEN = new Color(255, 255, 255, 230);
    private static final Color TABLE_ROW_ODD = new Color(248, 252, 255, 225);
    private static final Color TABLE_ROW_HOVER = new Color(226, 238, 255, 230);
    private static final Color TABLE_ROW_SELECTED = new Color(203, 225, 255, 240);

    private UiTheme() {
    }

    public static void applyGlobalStyles() {
        // FlatLaf defaults for a cleaner enterprise look.
        UIManager.put("Component.arc", 16);
        UIManager.put("Button.arc", 14);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("TitlePane.unifiedBackground", true);

        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
    }

    public static JPanel createCardPanel() {
        JPanel panel = new GlassPanel(22, CARD_BG, CARD_BORDER);
        panel.putClientProperty("FlatLaf.style", "arc:16");
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 132)),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    public static JPanel createBackgroundPanel() {
        return new GradientBackgroundPanel(BG, BG_BOTTOM);
    }

    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSubtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createMetricValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createHint(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(MUTED_TEXT);
        return label;
    }

    public static JPanel createMetricCard(String title, JLabel valueLabel) {
        JPanel panel = createCardPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)));
        panel.setLayout(new java.awt.BorderLayout(0, 6));
        panel.add(createHint(title), java.awt.BorderLayout.NORTH);
        panel.add(valueLabel, java.awt.BorderLayout.CENTER);
        return panel;
    }

    public static JPanel createToolbarPanel() {
        JPanel panel = createCardPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        return panel;
    }

    public static void styleTextField(JTextField field) {
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10));
        field.setBorder(border);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
    }

    public static void stylePasswordField(JPasswordField field) {
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10));
        field.setBorder(border);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        applyButtonBase(button);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(PRIMARY);
        button.setForeground(PRIMARY_TEXT);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        installHoverEffect(button, PRIMARY, adjustColor(PRIMARY, -18));
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        applyButtonBase(button);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 16, 10, 16)));
        installHoverEffect(button, Color.WHITE, new Color(241, 245, 249));
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        applyButtonBase(button);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(DANGER);
        button.setForeground(PRIMARY_TEXT);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        installHoverEffect(button, DANGER, adjustColor(DANGER, -20));
        return button;
    }

    public static JButton createSidebarButton(String text, boolean active) {
        JButton button = new JButton(text);
        applyButtonBase(button);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.putClientProperty("JButton.buttonType", "borderless");
        button.setForeground(active ? Color.WHITE : SIDEBAR_TEXT);

        Color normal = active ? new Color(59, 130, 246, 78) : new Color(255, 255, 255, 0);
        Color hover = active ? new Color(59, 130, 246, 98) : new Color(255, 255, 255, 26);
        button.setBackground(normal);

        Border leftAccent = active
                ? new MatteBorder(0, 3, 0, 0, PRIMARY)
                : new MatteBorder(0, 3, 0, 0, new Color(0, 0, 0, 0));
        button.setBorder(BorderFactory.createCompoundBorder(
                leftAccent,
                new EmptyBorder(10, 12, 10, 10)));

        installSidebarHoverTransition(button, normal, hover, 220);
        return button;
    }

    private static void applyButtonBase(JButton button) {
        button.setFocusPainted(false);
    }

    private static void installHoverEffect(JButton button, Color normal, Color hover) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    private static void installSidebarHoverTransition(JButton button, Color normal, Color hover, int durationMs) {
        button.setBackground(normal);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateBackground(button, hover, durationMs);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateBackground(button, normal, durationMs);
            }
        });
    }

    private static void animateBackground(JButton button, Color target, int durationMs) {
        Object existing = button.getClientProperty("hoverTimer");
        if (existing instanceof Timer) {
            ((Timer) existing).stop();
        }

        Color start = button.getBackground();
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float progress = (System.currentTimeMillis() - startTime) / (float) durationMs;
            if (progress >= 1f) {
                button.setBackground(target);
                timer.stop();
                return;
            }

            // Ease-out cubic for a softer UI motion.
            float eased = 1f - (float) Math.pow(1f - progress, 3);
            button.setBackground(interpolateColor(start, target, eased));
        });

        button.putClientProperty("hoverTimer", timer);
        timer.start();
    }

    private static Color interpolateColor(Color from, Color to, float t) {
        int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * t);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * t);
        int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * t);
        int a = (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * t);
        return new Color(r, g, b, a);
    }

    private static Color adjustColor(Color source, int delta) {
        int r = Math.max(0, Math.min(255, source.getRed() + delta));
        int g = Math.max(0, Math.min(255, source.getGreen() + delta));
        int b = Math.max(0, Math.min(255, source.getBlue() + delta));
        return new Color(r, g, b, source.getAlpha());
    }

    public static JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(SIDEBAR_BG);
        panel.setBorder(new EmptyBorder(20, 16, 20, 16));
        return panel;
    }

    public static JLabel createSidebarTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return label;
    }

    public static JLabel createSidebarSubtitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(SIDEBAR_MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    public static JPanel createTopBar() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.putClientProperty("FlatLaf.style", "arc:0");
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        return panel;
    }

    public static JPanel createHeaderTitleBlock(String title) {
        JPanel block = new JPanel(new BorderLayout());
        block.setOpaque(false);

        JLabel titleLabel = createTitle(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        block.add(titleLabel, BorderLayout.CENTER);
        return block;
    }

    public static JPanel createStatusBadge(String text) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        badge.setOpaque(true);
        badge.setBackground(Color.WHITE);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(2, 10, 2, 10)));

        JLabel dot = new JLabel("●");
        dot.setForeground(new Color(163, 230, 53));

        badge.add(dot);
        badge.setToolTipText(text);
        return badge;
    }

    public static JPanel createStatusIndicator(String text) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        badge.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(new Color(163, 230, 53));
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel label = new JLabel(text);
        label.setForeground(new Color(100, 116, 139));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        badge.add(dot);
        badge.add(label);
        badge.setToolTipText(text);
        return badge;
    }

    public static JPanel createUserChip(String fullName, String username) {
        JPanel chip = new JPanel(new BorderLayout());
        chip.setOpaque(true);
        chip.setBackground(new Color(255, 255, 255, 235));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 10, 6, 10)));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 1));
        text.setOpaque(false);
        JLabel name = createHint(fullName);
        name.setForeground(TEXT);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel user = createHint("@" + username);
        user.setForeground(new Color(100, 116, 139));
        text.add(name);
        text.add(user);

        chip.add(text, BorderLayout.CENTER);
        return chip;
    }

    public static JPanel createUserInfo(String fullName, String username) {
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 1));
        info.setOpaque(false);
        JLabel name = createHint(fullName);
        name.setForeground(TEXT);
        name.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel user = createHint("@" + username);
        user.setForeground(new Color(100, 116, 139));
        info.add(name);
        info.add(user);
        return info;
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    public static JLabel createRoleBadge(String text) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(Color.WHITE);
        badge.setForeground(new Color(51, 65, 85));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(5, 10, 5, 10)));
        return badge;
    }

    public static JLabel createRoleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(false);
        label.setForeground(new Color(51, 65, 85));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setGridColor(BORDER);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.putClientProperty("FlatLaf.style",
                "rowSelectionArc:10; selectionArc:10; showHorizontalLines:true; showVerticalLines:false");

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                table.putClientProperty("hoverRow", row);
                table.repaint();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.putClientProperty("hoverRow", -1);
                table.repaint();
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                int hoverRow = -1;
                Object hv = t.getClientProperty("hoverRow");
                if (hv instanceof Integer) {
                    hoverRow = (Integer) hv;
                }

                if (isSelected) {
                    c.setBackground(TABLE_ROW_SELECTED);
                    c.setForeground(TEXT);
                } else if (row == hoverRow) {
                    c.setBackground(TABLE_ROW_HOVER);
                    c.setForeground(TEXT);
                } else {
                    c.setBackground((row % 2 == 0) ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    c.setForeground(TEXT);
                }
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 247, 252));
        header.setForeground(TEXT);
        header.setReorderingAllowed(false);
    }
}