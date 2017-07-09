package tween.oaks.converter.gui;

import tween.oaks.converter.model.SymbolValueListener;
import tween.oaks.converter.gui.spinner.FloatingPointSpinner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public abstract class ValuePanel extends JPanel implements SymbolValueListener {
    private static final String NOT_AVAILABLE = "not available";
    private FloatingPointSpinner valueSpinner;
    private final String symbol;
    private static final Double MAXIMUM_PRICE = 999999999999.99;
    private boolean graphMutate = false;

    private ValuePanelListener listener;
    private final JLabel notAvailableMessage;

    private boolean root;

    public ValuePanel(String symbol) {
        this.symbol = symbol;
        setOpaque(false);
        setPreferredSize(new Dimension(160, 60));
        setLayout(null);

        addLabel(symbol);

        CloseButton closeButton = new CloseButton();
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.closeButtonClicked(ValuePanel.this);
                }
            }
        });
        closeButton.setBounds(141, 5, 14, 14);
        add(closeButton);

        valueSpinner = new FloatingPointSpinner(8, MAXIMUM_PRICE);
        valueSpinner.setFractionDigits(2);
        Component visualComponent = valueSpinner.getVisualComponent();
        visualComponent.setBounds(6, 28, 148, 26);
        add(visualComponent);

        notAvailableMessage = new JLabel(NOT_AVAILABLE);
        notAvailableMessage.setFont(getCurrentFont(14));
        notAvailableMessage.setBounds(37, 28, 120, 20);
        notAvailableMessage.setForeground(Color.GRAY.darker().darker());
        notAvailableMessage.setVisible(false);
        add(notAvailableMessage);

    }

    protected abstract void addLabel(String symbol);

    protected static Image loadIcon(String currency) {
        Image image;
        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            URL url = ValuePanel.class.getClassLoader().getResource("images/" + currency + ".png");
            if (url == null){
                url = ValuePanel.class.getClassLoader().getResource("images/Unknown.png");
            }
            image = tk.createImage(url);
            tk.prepareImage(image, -1, -1, null);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setListener(ValuePanelListener listener) {
        this.listener = listener;
    }

    public void addChangeValueListener(final ChangeListener listener) {
        valueSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // a little ugly
                if (!graphMutate) {
                    listener.stateChanged(e);
                }
            }
        });
    }

    public void addFocusListener(final FocusListener listener) {
        valueSpinner.addFocusListener(listener);
    }

    public void addButtonMouseListener(final MouseListener listener) {
        valueSpinner.addButtonsMouseListener(listener);
    }

    public double getValue() {
        return valueSpinner.getValue();
    }

    @Override
    public void valueUpdated(String currency, double value) {
        graphMutate = true;
        if (!valueSpinner.getValue().equals(value)) {
            valueSpinner.setValue(value);
        }
        graphMutate = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Color color1;
        Color color2;
        if (root){
            color1 = new Color(0xbbbbbb);
            color2 = new Color(0x333333);
        } else {
            color1 = new Color(0xCCCCCC);
            color2 = new Color(0x999999);
        }
        GradientPaint paint = new GradientPaint(0, 0, color1, 0, getHeight(), color2, false);
        g2.setPaint(paint);
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 7, 7);
        g2.fill(rect);
    }

    protected static Font getCurrentFont() {
        Font defaultFont = UIManager.getFont("Label.font");
        return new Font(defaultFont.getFontName(), defaultFont.getStyle(), defaultFont.getSize() + 2);
    }

    protected static Font getCurrentFont(int size) {
        Font defaultFont = UIManager.getFont("Label.font");
        return new Font(defaultFont.getFontName(), defaultFont.getStyle(), size);
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isCurrencyAvailable() {
        return valueSpinner.isVisible();
    }

    public void setCurrencyAvailable(boolean available) {
        valueSpinner.setVisible(available);
        notAvailableMessage.setVisible(!available);
    }

    @Override
    public boolean requestFocusInWindow() {
        return valueSpinner.requestFocusInWindow();
    }

    public void setRoot(boolean root) {
        this.root = root;
        repaint();
    }
}
