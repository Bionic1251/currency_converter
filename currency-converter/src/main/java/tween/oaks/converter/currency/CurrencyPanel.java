package tween.oaks.converter.currency;

import tween.oaks.converter.gui.ValuePanel;

import javax.swing.*;
import java.awt.*;

public class CurrencyPanel extends ValuePanel {

    public CurrencyPanel(String currency) {
        super(currency);
    }

    @Override
    protected void addLabel(String symbol) {
        Image image = loadIcon(symbol);

        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageLabel.setBounds(37, 4, 24, 24);
        add(imageLabel);

        JLabel symbolLabel = new JLabel();
        symbolLabel.setFont(getCurrentFont());
        symbolLabel.setBounds(64, 6, 100, 20);
        symbolLabel.setText(symbol);
        add(symbolLabel);
    }

}
