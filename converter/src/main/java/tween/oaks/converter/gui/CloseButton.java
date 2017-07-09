package tween.oaks.converter.gui;

import javax.swing.*;
import java.awt.*;

public class CloseButton extends JButton {
    private static final int HEIGHT = 14;
    private static final int WIDTH = 14;

    public CloseButton() {
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (model.isPressed()) {
            drawSelected(g2);
            return;
        }
        if (model.isRollover()) {
            drawRollOver(g2);
            return;
        }
        g2.setColor(new Color(0x333333));
        drawCross(g2);
    }

    private void drawSelected(Graphics2D g) {
        g.setColor(new Color(0x000000));
        drawCross(g);
    }

    private void drawRollOver(Graphics2D g) {
        g.setColor(new Color(0x0066EE));
        drawCross(g);
    }

    private void drawCross(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        g.drawLine(2, 2, HEIGHT - 3, WIDTH - 3);
        g.drawLine(WIDTH - 3, 2, 2, HEIGHT - 3);
    }
}
