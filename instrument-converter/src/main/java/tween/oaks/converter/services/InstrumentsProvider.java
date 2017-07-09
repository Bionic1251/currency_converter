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
public class InstrumentsProvider extends AbstractSymbolProvider {

    public InstrumentsProvider(InstrumentService instrumentService) {
        super(instrumentService);
    }

    @Override
    protected Set<String> getItems(Collection<Instrument> instruments) {
        Set<String> newCurrencies = new HashSet<>();
        for (Instrument instrument : instruments) {
            if (instrument.getType().equals(InstrumentType.FOREX)){
                String symbol = instrument.getSymbol();
                newCurrencies.add(symbol);
            }
        }
        return newCurrencies;
    }

}

