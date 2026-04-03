package securehr.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;

import javax.swing.JPanel;

public class GradientBackgroundPanel extends JPanel {
    private final Color top;
    private final Color bottom;

    public GradientBackgroundPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Soft light blooms for a more organic background feel.
        float radius1 = Math.max(getWidth(), getHeight()) * 0.45f;
        RadialGradientPaint glow1 = new RadialGradientPaint(
                getWidth() * 0.15f,
                getHeight() * 0.08f,
                radius1,
                new float[] { 0f, 1f },
                new Color[] { new Color(255, 255, 255, 110), new Color(255, 255, 255, 0) },
                CycleMethod.NO_CYCLE);
        g2.setPaint(glow1);
        g2.fillRect(0, 0, getWidth(), getHeight());

        float radius2 = Math.max(getWidth(), getHeight()) * 0.40f;
        RadialGradientPaint glow2 = new RadialGradientPaint(
                getWidth() * 0.90f,
                getHeight() * 0.92f,
                radius2,
                new float[] { 0f, 1f },
                new Color[] { new Color(191, 219, 254, 90), new Color(191, 219, 254, 0) },
                CycleMethod.NO_CYCLE);
        g2.setPaint(glow2);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
        super.paintComponent(g);
    }
}
