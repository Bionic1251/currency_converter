package tween.oaks.converter.instrument;

import com.tradable.api.common.Destroyable;
import tween.oaks.converter.gui.Converter;
import tween.oaks.converter.gui.ValuePanel;
import tween.oaks.converter.model.SymbolValuesHolder;

import java.util.Set;

/**
 * @author ochtarfear
 * @since 6/2/13
 */
public class InstrumentConverter extends Converter {

    public static final String DEFAULT_INSTRUMENTS_SET = "EURUSD;USDJPY;";

    public InstrumentConverter(SymbolValuesHolder symbolValuesHolder, Set<Destroyable> destroyables) {
        super(symbolValuesHolder, destroyables);
    }

    @Override
    protected String getDefaultSymbols() {
        return DEFAULT_INSTRUMENTS_SET;
    }

    @Override
    protected String getTitle() {
        return AppSettings.APP_NAME;
    }

    @Override
    protected ValuePanel createPanel(String symbol) {
        return new InstrumentPanel(symbol);
    }
}
