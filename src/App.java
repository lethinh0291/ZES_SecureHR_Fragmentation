import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import securehr.ui.LoginFrame;
import securehr.ui.UiTheme;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception secondIgnored) {
                    // Keep default look and feel as last fallback.
                }
            }

            UiTheme.applyGlobalStyles();

            new LoginFrame().setVisible(true);
        });
    }
}
