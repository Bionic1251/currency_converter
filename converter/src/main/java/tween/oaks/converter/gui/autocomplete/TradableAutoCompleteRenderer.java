package tween.oaks.converter.gui.autocomplete;

import javax.swing.*;
import java.awt.*;

public class TradableAutoCompleteRenderer extends JLabel
        implements ListCellRenderer {

    private final Color textColor = new Color(0xffffff);
    private final Color backgroundColor = new Color(0x000000);
    private final Color selectedBackgroundColor = new Color(0x0780CD);

    private final int fontSize = 4;

    private final TradableAutoCompleteEditor tf;

    public TradableAutoCompleteRenderer(JComboBox cb) {
        setOpaque(true);
        setForeground(textColor);
        setHorizontalAlignment(LEFT);

        tf = (TradableAutoCompleteEditor) cb.getEditor();
    }

    public Component getListCellRendererComponent(JList jList, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(selectedBackgroundColor);
        } else {
            setBackground(backgroundColor);
        }
        String typedText = tf.getUserTypedText();
        String currentText = value.toString().toUpperCase();
        int startIndex = currentText.indexOf(typedText);
        if (typedText == null || startIndex == -1 || isSelected) {
            setText("<html><font size=\"" + fontSize + "\">" + currentText + "</font></html>");
        } else {
            int endIndex = startIndex + typedText.length();
            String startText = currentText.substring(0, startIndex);
            String highlightedText = currentText.substring(startIndex,
                    endIndex);
            String endText = currentText.substring(endIndex);
            setText("<html><font size=\"" + fontSize + "\">" + startText + "<font color=#4D8EBD>" +
                    highlightedText + "</font>" + endText + "</font></html>");
        }
        return this;
    }
}
