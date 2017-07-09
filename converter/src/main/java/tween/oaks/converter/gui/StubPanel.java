package tween.oaks.converter.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * @author ochtarfear
 * @since 3/3/13
 */
public class StubPanel extends JPanel {

    private static final float DASH_PHASE = 0f;
    private static final float[] DASH_PATTERN = {
            5f,                                                                 // upper left corner
            7f, 10f, 7f, 10f, 7f,                                               // left vertical
            15f,                                                                // lower left corner
            7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f,  // lower horizontal
            15f,                                                                // lower right corner
            7f, 10f, 7f, 10f, 7f,                                               // right vertical
            15f,                                                                // upper right corner
            7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f, 12f, 7f,  // upper horizontal
            10f                                                                  // upper left corner
    };

    public StubPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(160, 60));
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
/*        GradientPaint paint = new GradientPaint(0, 0, new Color(0x555555), 0, getHeight(), new Color(0x212121), true);
        g2.setPaint(paint);*/
        RoundRectangle2D rect = new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight()-3, 7, 7);
        Stroke defaultStroke = g2.getStroke();
        BasicStroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH_PATTERN, DASH_PHASE);
        g2.setColor(new Color(0x999999));
        g2.setStroke(stroke);
        g2.draw(rect);
        g2.setStroke(defaultStroke);
    }
}
