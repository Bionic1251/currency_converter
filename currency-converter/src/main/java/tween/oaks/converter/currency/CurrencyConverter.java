package tween.oaks.converter.currency;

import com.tradable.api.common.Destroyable;
import tween.oaks.converter.gui.Converter;
import tween.oaks.converter.gui.ValuePanel;
import tween.oaks.converter.model.SymbolValuesHolder;

import java.util.Set;

/**
 * @author ochtarfear
 * @since 6/2/13
 */
public class CurrencyConverter extends Converter {

    public static final String DEFAULT_CURRENCIES_SET = "EUR;USD;AUD;GBP;JPY;";

    public CurrencyConverter(SymbolValuesHolder symbolValuesHolder, Set<Destroyable> destroyables) {
        super(symbolValuesHolder, destroyables);
    }

    @Override
    protected String getDefaultSymbols() {
        return DEFAULT_CURRENCIES_SET;
    }

    @Override
    protected String getTitle() {
        return AppSettings.APP_NAME;
    }

    @Override
    protected ValuePanel createPanel(String currency) {
        return new CurrencyPanel(currency);
    }
}
