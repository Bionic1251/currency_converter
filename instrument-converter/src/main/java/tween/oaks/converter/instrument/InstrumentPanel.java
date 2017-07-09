package tween.oaks.converter.instrument;

import tween.oaks.converter.gui.ValuePanel;

import javax.swing.*;
import java.awt.*;

public class InstrumentPanel extends ValuePanel {

    public InstrumentPanel(String symbol) {
        super(symbol);
    }

    @Override
    protected void addLabel(String symbol) {
        if (symbol.length() == 6){

            String currency2 = symbol.substring(3);
            Image image = loadIcon(currency2);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setBounds(36, 5, 24, 24);
            add(imageLabel);

            String currency1 = symbol.substring(0, 3);
            image = loadIcon(currency1);
            imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setBounds(31, 0, 24, 24);
            add(imageLabel);

        }

        JLabel symbolLabel = new JLabel();
        symbolLabel.setFont(getCurrentFont());
        symbolLabel.setBounds(64, 6, 100, 20);
        symbolLabel.setText(symbol);
        add(symbolLabel);
    }

}
