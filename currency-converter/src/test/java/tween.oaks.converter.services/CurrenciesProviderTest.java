package tween.oaks.converter.services;

import com.tradable.api.entities.Instrument;
import com.tradable.api.entities.InstrumentType;
import com.tradable.api.services.instrument.InstrumentService;
import junit.framework.Assert;
import org.junit.Test;
import tween.oaks.converter.services.mocks.MockInstrumentService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CurrenciesProviderTest extends CurrenciesProvider {
    public CurrenciesProviderTest() {
        super(new MockInstrumentService());
    }

    @Test
    public void testGetItems(){
        //CurrenciesProviderTest currenciesProvider = new CurrenciesProviderTest();
        List<Instrument> instruments = new ArrayList<Instrument>();
        instruments.add(new FakeInstrument("AUDCAD"));
        instruments.add(new FakeInstrument("EUR/USD"));
        instruments.add(new FakeInstrument("NZD/JPY"));
        Set<String> currencySet = getItems(instruments);
        Assert.assertTrue(currencySet.contains("AUD"));
        Assert.assertTrue(currencySet.contains("CAD"));
        Assert.assertTrue(currencySet.contains("EUR"));
        Assert.assertTrue(currencySet.contains("USD"));
        Assert.assertTrue(currencySet.contains("NZD"));
        Assert.assertTrue(currencySet.contains("JPY"));
        Assert.assertEquals(currencySet.size(), 6);
    }
    
    private class FakeInstrument implements Instrument {
        private String symbol;

        private FakeInstrument(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public int getId() {
            return 0;  
        }

        @Override
        public String getSymbol() {
            return symbol;
        }

        @Override
        public String getDescription() {
            return null;  
        }

        @Override
        public InstrumentType getType() {
            return InstrumentType.FOREX;
        }

        @Override
        public double getPrecision() {
            return 0;  
        }

        @Override
        public Double getPriceIncrement() {
            return null;  
        }

        @Override
        public Double getPipPrecision() {
            return null;  
        }

        @Override
        public Double getMinOrderSize() {
            return null;  
        }

        @Override
        public Double getMaxOrderSize() {
            return null;  
        }

        @Override
        public Double getOrderSizeIncrement() {
            return null;  
        }
    }
}

