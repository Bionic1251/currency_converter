package tween.oaks.converter.gui;

import tween.oaks.converter.gui.autocomplete.AutoCompleteChoiceListener;
import tween.oaks.converter.gui.autocomplete.TradableComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class EmptyPanel extends StubPanel {

    private AutoCompleteChoiceListener listener;
    private Component symbolChooser = null;

    public void setCurrencies(Set<String> symbols) {
        if (symbols == null) {
            throw new NullPointerException("Symbols array must not be null");
        }
        if (symbolChooser != null) {
            remove(symbolChooser);
        }
        if (symbols.size() == 1) {
            addButton(symbols.iterator().next());
            setVisible(true);
        } else if (!symbols.isEmpty()) {
            addComboBox(symbols.toArray(new String[symbols.size()]));
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    private void addButton(final String currency) {
        final JButton button = new JButton(currency);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.currencyChosen(currency);
                }
            }
        });
        button.setSize(100, 24);
        button.setLocation(30, 16);
        add(button);
        symbolChooser = button;
    }

    private void addComboBox(String[] symbols) {
        final TradableComboBox symbolComboBox = new TradableComboBox(symbols);
        symbolComboBox.setSymbolListener(new AutoCompleteChoiceListener() {
            @Override
            public void currencyChosen(String currency) {
                dispatchCurrencyEvent(currency);
            }
        });
        symbolComboBox.setSize(120, 26);
        symbolComboBox.setLocation(20, 14);
        add(symbolComboBox, BorderLayout.CENTER);
        symbolChooser = symbolComboBox;
    }

    private void dispatchCurrencyEvent(String chosenCurrency) {
        if (listener != null) {
            listener.currencyChosen(chosenCurrency);
        }
    }

    public void setListener(AutoCompleteChoiceListener listener) {
        this.listener = listener;
    }

}
