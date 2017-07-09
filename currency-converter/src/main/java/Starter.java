import com.tradable.api.common.Destroyable;
import com.tradable.api.component.state.DefaultPersistedStateHolder;
import tween.oaks.converter.model.SymbolProvider;
import tween.oaks.converter.model.SymbolValuesHolderImpl;
import tween.oaks.converter.services.CurrenciesProvider;
import tween.oaks.converter.services.QuotesGraphConversionRateService;
import tween.oaks.converter.gui.Converter;
import tween.oaks.converter.services.mocks.MockInstrumentService;
import tween.oaks.converter.services.mocks.QuoteGenerator;
import tween.oaks.converter.currency.CurrencyConverter;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

/**
 * @author ochtarfear
 * @since 1/6/13
 */
public class Starter extends JFrame {

    public Starter() throws HeadlessException {
        setTitle("Simple example");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MockInstrumentService instrumentService = new MockInstrumentService();
        SymbolProvider currenciesProvider = new CurrenciesProvider(instrumentService);
        QuotesGraphConversionRateService conversionRateService = new QuotesGraphConversionRateService(new QuoteGenerator(), instrumentService);
        SymbolValuesHolderImpl currencyValuesHolder = new SymbolValuesHolderImpl(conversionRateService, currenciesProvider);

        Converter currencyConverter = new CurrencyConverter(currencyValuesHolder, Collections.<Destroyable>emptySet());
        DefaultPersistedStateHolder defaultPersistedStateHolder = new DefaultPersistedStateHolder();
        defaultPersistedStateHolder.setProperty("currencies", "AUD;USD;");
        currencyConverter.loadPersistedState(defaultPersistedStateHolder);

        getContentPane().add(currencyConverter.getVisualComponent());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Starter ex = new Starter();
                ex.setVisible(true);
            }
        });
    }

}
