package securehr.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GlassPanel extends JPanel {
    private final int arc;
    private final Color fillColor;
    private final Color borderColor;

    public GlassPanel(int arc, Color fillColor, Color borderColor) {
        this.arc = arc;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Soft shadow layer for depth.
        g2.setColor(new Color(10, 24, 48, 18));
        g2.fillRoundRect(2, 4, w - 6, h - 6, arc, arc);

        // Glass body.
        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, w - 4, h - 4, arc, arc);

        // Thin bright edge to mimic light refraction.
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 4, h - 4, arc, arc);

        // Gentle top sheen for a smoother glass feel.
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 68), 0, h / 2f, new Color(255, 255, 255, 0)));
        g2.fillRoundRect(1, 1, w - 6, (h / 2), arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
