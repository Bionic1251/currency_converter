package tween.oaks.converter.gui.autocomplete;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TradableArrowButton extends JButton {

    private final int ARC = 7;
    private final int triangleSize = 8;

    private final Color color1 = new Color(0x929292);
    private final Color color2 = new Color(0x1B1B1B);

    private final Color rolloverColor1 = new Color(0x8C8C8C);
    private final Color rolloverColor2 = new Color(0x373737);

    private final Color pressedColor1 = new Color(0x3D3D3D);
    private final Color pressedColor2 = new Color(0x010101);

    private final Color borderColor = new Color(0xA9A9A9);

    public TradableArrowButton() {
        setOpaque(false);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D rect = new RoundRectangle2D.Float(-ARC, 1,
                getWidth() + ARC - 1,
                getHeight() - 2, ARC, ARC);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(rect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D rect = new RoundRectangle2D.Float(1, 1, getWidth() - 2,
                getHeight() - 2, ARC, ARC);


        GradientPaint gradientPaint;
        if (model.isRollover()) {
            if (model.isPressed()) {
                gradientPaint = new GradientPaint(0, 0,
                        pressedColor1,
                        0, getHeight(), pressedColor2);
            } else {
                gradientPaint = new GradientPaint(0, 0,
                        rolloverColor1,
                        0, getHeight(), rolloverColor2);
            }
        } else {
            gradientPaint = new GradientPaint(0, 0,
                    color1,
                    0, getHeight(), color2);
        }
        g2.setPaint(gradientPaint);

        g2.fill(rect);
        g2.fillRect(1, 1, getWidth() / 2, getHeight() - 1);
        drawTriangle(g2);
    }

    private void drawTriangle(Graphics2D g2) {
        int hTriangle = (int) Math.sqrt(triangleSize * triangleSize * 3 / 4);
        int w = getWidth();
        int h = getHeight();
        int x0 = (w - triangleSize) / 2;
        int y0 = (h - triangleSize) / 2;
        int x1 = (w + triangleSize) / 2;
        int y1 = y0;
        int x2 = w / 2;
        int y2 = (h + hTriangle) / 2;
        int[] xPoints = {x0, x1, x2};
        int[] yPoints = {y0, y1, y2};

        g2.setColor(Color.WHITE);
        g2.fillPolygon(xPoints, yPoints, xPoints.length);
    }
}
