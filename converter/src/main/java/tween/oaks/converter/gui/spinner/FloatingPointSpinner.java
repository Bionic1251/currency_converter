package tween.oaks.converter.gui.spinner;


import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * @author ochtarfear
 * @since 1/26/13
 */
public class FloatingPointSpinner {
    public static final String OVER_MAXIMUM_STRING = "OVER LIMIT";
    public static final String NAN_STRING = "no conversion rate";

    private final JSpinner spinner;
    private volatile int fractionDigits = 0;
    private volatile int cursorPosition = -1;
    private final VariableStepSpinnerNumberModel model;
    private final NumberFormatter formatter;
    private volatile Double maximum;
    private boolean overMaximum = false;
    private boolean nan = false;

    public FloatingPointSpinner(int columns, final Double maximum) {
        this.maximum = maximum;

        model = new VariableStepSpinnerNumberModel(maximum);
        spinner = new JSpinner(model);
        JSpinner.NumberEditor editor = new CursorSavingNumericEditor(spinner);

        final JFormattedTextField textField = editor.getTextField();
        formatter = new NumberFormatter() {
            @Override
            public void install(JFormattedTextField ftf) {
                super.install(ftf);
                int textLength = textField.getText().length();
                textField.setCaretPosition(cursorPosition >= 0 && cursorPosition < textLength? cursorPosition : textLength);
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value instanceof Double && Double.isNaN((Double)value)){
                    overMaximum = false;
                    nan = true;
                    return NAN_STRING;
                } else if (FloatingPointSpinner.this.maximum.compareTo((Double) value) < 0) {
                    overMaximum = true;
                    nan = false;
                    return OVER_MAXIMUM_STRING;
                } else {
                    overMaximum = false;
                    nan = false;
                    return super.valueToString(value);
                }
            }
        };

        formatter.setMinimum(0.0);
        formatter.setMaximum(maximum);
        formatter.setCommitsOnValidEdit(true);
        formatter.setAllowsInvalid(false);

        textField.setFormatterFactory(new JFormattedTextField.AbstractFormatterFactory() {
            @Override
            public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                return formatter;
            }
        });

        model.setStepProvider(new StepProvider() {
            @Override
            public Double getStep() {
                int b = textField.getText().length() - textField.getCaretPosition();
                b -= fractionDigits > 0 ? fractionDigits + 1 : 0;
                if (b >= 0) {
                    b -= b / 4; // to allow for thousands separation
                } else {
                    b += 1; // to allow for fractal part separation
                }
                return Math.pow(10, b);
            }
        });

        textField.setColumns(columns);
        spinner.setEditor(editor);

        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int caretPosition = textField.getCaretPosition();
                int textLength = textField.getText().length();
                boolean hasFraction = fractionDigits > 0;
                boolean keyDelete = e.getKeyChar() == KeyEvent.VK_DELETE;
                boolean keyBackspace = e.getKeyChar() == KeyEvent.VK_BACK_SPACE;
                boolean cursorOnDecimalMark = (textLength - caretPosition) == fractionDigits + 1;
                boolean cursorAfterDecimalMark = (textLength - caretPosition) == fractionDigits;
                boolean deleteDecimalMarkWithBackspace = hasFraction && keyBackspace && cursorAfterDecimalMark;
                boolean deleteDecimalMarkWithDelete = hasFraction && keyDelete && cursorOnDecimalMark;

                if (deleteDecimalMarkWithBackspace){
                    textField.setCaretPosition(caretPosition - 1);
                    e.consume();
                }

                if (deleteDecimalMarkWithDelete) {
                    textField.setCaretPosition(caretPosition + 1);
                    e.consume();
                }
            }
        });
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (overMaximum){
                    setValue(maximum);
                } else if (nan){
                    setValue(100.0);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                cursorPosition = textField.getCaretPosition();
            }
        });
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                cursorPosition = textField.viewToModel(e.getPoint());
            }
        });
    }

    public Component getVisualComponent() {
        return spinner;
    }

    public void setMaximum(Double maximum){
        this.maximum = maximum;
        model.setMaximum(maximum);
    }

    public void addChangeListener(ChangeListener changeListener) {
        spinner.addChangeListener(changeListener);
    }

    public void setFractionDigits(int fractionDigits) {
        if (fractionDigits < 0) {
            throw new IllegalArgumentException("fraction digits number must be non-negative");
        }
        this.fractionDigits = fractionDigits;
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMaximumFractionDigits(fractionDigits);
        format.setMinimumFractionDigits(fractionDigits);
    }

    public static double roundToDecimals(double d, int c) {
        double pow = Math.pow(10, c);
        long temp = Math.round(d * pow);
        return ((double) temp) / pow;
    }

    public void setValue(Double value) {
        value = Double.isNaN(value)? Double.NaN: roundToDecimals(value, fractionDigits);
        spinner.setValue(value);
        JFormattedTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        textField.setCaretPosition(textField.getText().length());
    }

    public Double getValue() {
        return ((Number) spinner.getValue()).doubleValue();
    }

    public void addFocusListener(FocusListener focusListener) {
        ((JSpinner.NumberEditor) spinner.getEditor()).getTextField().addFocusListener(focusListener);
    }

    public void addButtonsMouseListener(MouseListener mouseListener) {
        for (Component component : spinner.getComponents()) {
            if (!(component instanceof CursorSavingNumericEditor))
                component.addMouseListener(mouseListener);
        }
    }

    public void resetCursorPosition() {
        cursorPosition = -1;
    }

    public void setVisible(boolean visible){
        spinner.setVisible(visible);
    }

    public boolean isVisible(){
        return spinner.isVisible();
    }

    public boolean requestFocusInWindow() {
        return ((JSpinner.NumberEditor) spinner.getEditor()).getTextField().requestFocusInWindow();
    }
}
