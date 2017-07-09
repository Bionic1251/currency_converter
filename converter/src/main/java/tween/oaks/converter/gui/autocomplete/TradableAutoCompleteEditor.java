package tween.oaks.converter.gui.autocomplete;

import javax.swing.*;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;

public class TradableAutoCompleteEditor extends JTextField
        implements ComboBoxEditor {

    private final int ARC = 7;
    private final Color borderColor = new Color(0xA9A9A9);
    private final Color textColor = new Color(0x008FFF);
    private final Color invalidTextColor = new Color(0x990000);

    private String[] source;

    private String userTypedText = "";
    private Boolean typedTextUpdatable = true;

    TradableTextFieldListener currencyChoiceListener;

    public TradableAutoCompleteEditor(String[] source) {
        setOpaque(true);
        setUI(new BasicFormattedTextFieldUI());
        setHorizontalAlignment(LEFT);
        setForeground(textColor);
        setMargin(new Insets(0, 5, 0, 0));
        this.source = source;
        addListeners();
    }

    public void setCurrencyChoiceListener(TradableTextFieldListener currencyChoiceListener) {
        this.currencyChoiceListener = currencyChoiceListener;
    }

    private void notifyChoiceListenerIfUserMadeTheirMind(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            notifyChoiceListener();
        }
    }

    private void notifyChoiceListener() {
        if (currencyChoiceListener != null) {
            currencyChoiceListener.chosen(getText());
        }
    }

    private void notifyKeyListener() {
        if (currencyChoiceListener != null) {
            currencyChoiceListener.keyReleased();
        }
    }

    private void addListeners() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                capitalizeCharOfEvent(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                manageTypedTextUpdate(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                saveUserTypedTextIfNecessary();
                validateField();
                notifyKeyListener();
                notifyChoiceListenerIfUserMadeTheirMind(e);
            }
        });
        //Here is the difference between PC and CC
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setText("");
            }
        });
    }

    private void capitalizeCharOfEvent(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (Character.isLowerCase(keyChar)) {
            e.setKeyChar(Character.toUpperCase(keyChar));
        }
    }

    private void saveUserTypedTextIfNecessary() {
        if (typedTextUpdatable) {
            userTypedText = getUnselectedText();
        }
    }

    private void manageTypedTextUpdate(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
            typedTextUpdatable = false;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            userTypedText = "";
        } else {
            typedTextUpdatable = true;
        }
    }

    public String getUserTypedText() {
        return userTypedText;
    }

    private String getUnselectedText() {
        if (getSelectedText() == null || getText() == null) {
            return getText();
        }
        return getText().substring(0, getSelectionStart());
    }

    public Component getEditorComponent() {
        return this;
    }

    public void setItem(Object anObject) {
        if (anObject == null) {
            return;
        }
        setText(anObject.toString());
    }

    public Object getItem() {
        return getText();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D rect = new RoundRectangle2D.Float(1, 1,
                getWidth() + ARC,
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
        RoundRectangle2D rect = new RoundRectangle2D.Float(1, 1,
                getWidth() + ARC,
                getHeight() - 2, ARC, ARC);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.fill(rect);
        super.paintComponent(g);
    }

    public void validateField() {
        if (source == null) {
            return;
        }
        for (String item : source) {
            if (item.equals(getText())) {
                setForeground(textColor);
                return;
            }
        }
        setForeground(invalidTextColor);
    }
}