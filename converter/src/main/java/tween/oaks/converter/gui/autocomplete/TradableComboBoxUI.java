package tween.oaks.converter.gui.autocomplete;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TradableComboBoxUI extends BasicComboBoxUI {
    private TradableListListener listener;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        getList().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                notifyListListener();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

    }

    public void setListListener(TradableListListener listener) {
        this.listener = listener;
    }

    private void notifyListListener() {
        if (listener != null) {
            listener.chosen(getListSelectedValue());
        }
    }

    private String getListSelectedValue() {
        if (getList() == null) {
            return "";
        }
        return getList().getSelectedValue().toString();
    }

    public static ComboBoxUI createUI(JComponent c) {
        return new TradableComboBoxUI();
    }

    @Override
    protected JButton createArrowButton() {
        return new TradableArrowButton();
    }

    public JList getList() {
        return listBox;
    }
}
