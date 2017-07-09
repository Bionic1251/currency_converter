package tween.oaks.converter.services;

import com.tradable.api.entities.Instrument;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.instrument.InstrumentServiceListener;
import com.tradable.api.services.instrument.InstrumentUpdateEvent;
import tween.oaks.converter.model.SymbolListener;
import tween.oaks.converter.model.SymbolProvider;

import java.util.*;

/**
 * @author ochtarfear
 * @since 6/6/13
 */
public abstract class AbstractSymbolProvider implements SymbolProvider {

    private final Set<String> items = new HashSet<>();
    private final List<SymbolListener> listeners = new ArrayList<>();

    public AbstractSymbolProvider(InstrumentService instrumentService){
        instrumentService.addListener(new InstrumentServiceListener() {
            @Override
            public void instrumentsUpdated(InstrumentUpdateEvent instrumentUpdateEvent) {
                processInstruments(instrumentUpdateEvent.getUpdatedInstruments().values());
            }
        });
        Collection<Instrument> instruments = instrumentService.getInstruments();
        processInstruments(instruments);

    }

    private void processInstruments(Collection<Instrument> instruments) {
        Set<String> newItems = new HashSet<>();
        Set<String> ss = getItems(instruments);
        for (String symbol : ss) {
            if (items.add(symbol)){
                newItems.add(symbol);
            }
        }
        if (!newItems.isEmpty() && !listeners.isEmpty()){
            for (SymbolListener listener: listeners){
                listener.symbolsAdded(newItems);
            }
        }
    }

    protected abstract Set<String> getItems(Collection<Instrument> instruments);

    @Override
    public void addSymbolListener(SymbolListener listener) {
        listeners.add(listener);
        listener.symbolsAdded(new HashSet<>(items));
    }



}
