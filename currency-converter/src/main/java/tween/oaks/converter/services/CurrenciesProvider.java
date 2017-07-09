package tween.oaks.converter.services;

import com.tradable.api.entities.Instrument;
import com.tradable.api.entities.InstrumentType;
import com.tradable.api.services.instrument.InstrumentService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ochtarfear
 * @since 6/3/13
 */
public class CurrenciesProvider extends AbstractSymbolProvider {

    public CurrenciesProvider(InstrumentService instrumentService) {
        super(instrumentService);
    }

    @Override
    protected Set<String> getItems(Collection<Instrument> instruments) {
        Set<String> newCurrencies = new HashSet<>();
        for (Instrument instrument : instruments) {
            if (instrument.getType().equals(InstrumentType.FOREX)) {
                String symbol = instrument.getSymbol();
                if (symbol.contains("/")) {
                    String[] currencies = symbol.split("/");
                    newCurrencies.add(currencies[0]);
                    newCurrencies.add(currencies[1]);
                } else if (symbol.length() == 6) {
                    newCurrencies.add(symbol.substring(0, 3));
                    newCurrencies.add(symbol.substring(3));
                }
            }
        }
        return newCurrencies;
    }

}

