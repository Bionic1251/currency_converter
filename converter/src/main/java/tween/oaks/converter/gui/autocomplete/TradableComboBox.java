package tween.oaks.converter.gui.autocomplete;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;

import javax.swing.*;
import java.awt.*;

public class TradableComboBox extends JComboBox {
    private AutoCompleteChoiceListener currencyListener;

    public TradableComboBox() {
        super();
    }

    public TradableComboBox(String[] items) {
        super(items);
        initialize(items);
    }

    public void initialize(final String[] items) {
        setPreferredSize(new Dimension(100, 26));

        final TradableAutoCompleteEditor editor = new TradableAutoCompleteEditor(items);
        setEditor(editor);

        TradableAutoCompleteRenderer renderer = new TradableAutoCompleteRenderer(this);
        setRenderer(renderer);

        final TradableComboBoxUI ui = (TradableComboBoxUI) TradableComboBoxUI.createUI(this);
        setUI(ui);

        setAutoCompleteSupport(items);
        setEditable(true);

        editor.setCurrencyChoiceListener(new TradableTextFieldListener() {
            @Override
            public void chosen(String text) {
                dispatchCurrencyEventIfNecessary(text, items);
            }

            @Override
            public void keyReleased() {
                ui.getList().updateUI();
            }
        });

        ui.setListListener(new TradableListListener() {
            @Override
            public void chosen(String text) {
                dispatchCurrencyEventIfNecessary(text, items);
            }
        });
    }

    private void setAutoCompleteSupport(String[] items) {
        AutoCompleteSupport support = AutoCompleteSupport.install(this,
                GlazedLists.eventListOf(items));
        support.setFilterMode(TextMatcherEditor.CONTAINS);
        support.setSelectsTextOnFocusGain(true);
        support.setHidesPopupOnFocusLost(true);
    }

    private void dispatchCurrencyEventIfNecessary(String chosenCurrency, String[] currencies) {
        for (String currency : currencies) {
            if (currency.equals(chosenCurrency) && currencyListener != null) {
                currencyListener.currencyChosen(chosenCurrency);
            }
        }
    }

    public void setSymbolListener(AutoCompleteChoiceListener currencyListener) {
        this.currencyListener = currencyListener;
    }
}
