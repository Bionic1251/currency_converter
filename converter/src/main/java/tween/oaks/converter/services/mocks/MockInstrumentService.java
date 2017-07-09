package tween.oaks.converter.services.mocks;

import com.tradable.api.entities.Instrument;
import com.tradable.api.entities.InstrumentType;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.instrument.InstrumentServiceListener;
import com.tradable.api.services.instrument.InstrumentUpdateEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @user: tween
 * @date: 11/6/12
 */
public class MockInstrumentService implements InstrumentService {

    private final Map<Integer, Instrument> instruments;

    {
        HashMap<Integer, Instrument> i = new HashMap<>();
        i.put(0, new DummyInstrument(0, "EURUSD"));
        i.put(1, new DummyInstrument(1, "EURJPY"));
        i.put(2, new DummyInstrument(2, "GBPUSD"));
        i.put(3, new DummyInstrument(3, "EURGBP"));
        i.put(4, new DummyInstrument(4, "RUBNZD"));
        i.put(5, new DummyInstrument(5, "USDJPY"));
        i.put(6, new DummyInstrument(6, "AUDUSD"));
        i.put(7, new DummyInstrument(7, "USDCAD"));
        i.put(8, new DummyInstrument(8, "NZDUSD"));
        i.put(9, new DummyInstrument(9, "USDCHF"));
        i.put(10, new DummyInstrument(10, "GBPJPY"));
        i.put(11, new DummyInstrument(11, "XAUUSD"));
        i.put(12, new DummyInstrument(12, "XAGUSD"));
        i.put(13, new DummyInstrument(13, "SN1USD"));
        i.put(14, new DummyInstrument(14, "OILUSD"));
        this.instruments = Collections.unmodifiableMap(i);
    }

    @Override
    public void addListener(InstrumentServiceListener instrumentServiceListener) {
        instrumentServiceListener.instrumentsUpdated(new InstrumentUpdateEvent() {
            @Override
            public Map<Integer, Instrument> getUpdatedInstruments() {
                return instruments;
            }

            @Override
            public boolean isReset() {
                return true;
            }
        });
    }

    @Override
    public void removeListener(InstrumentServiceListener instrumentServiceListener) {

    }

    @Override
    public Collection<Instrument> getInstruments() {
        return instruments.values();
    }

    @Override
    public Instrument getInstrument(int i) {
        return instruments.get(i);
    }

    @Override
    public Instrument getInstrument(String s) {
        for (Instrument i: instruments.values()){
            if (i.getSymbol().equals(s)){
                return i;
            }
        }
        return null;
    }

    private class DummyInstrument implements Instrument {


        private final int id;
        private final String symbol;

        private DummyInstrument(int id, String symbol) {
            this.id = id;
            this.symbol = symbol;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public String getDescription() {
            return "no description";
        }

        @Override
        public InstrumentType getType() {
            return InstrumentType.FOREX;
        }

        @Override
        public double getPrecision() {
            return 5;
        }

        @Override
        public Double getPriceIncrement() {
            return 0.00001;
        }

        @Override
        public Double getPipPrecision() {
            return 5.0;
        }

        @Override
        public Double getMinOrderSize() {
            return 1.0;
        }

        @Override
        public Double getMaxOrderSize() {
            return 100000000.0;
        }

        @Override
        public Double getOrderSizeIncrement() {
            return 1.0;
        }
    }
}
