package tween.oaks.converter.services;

import com.tradable.api.services.conversion.ConversionRateListener;
import com.tradable.api.services.conversion.ConversionRateService;

import java.util.*;

/**
 * @author ochtarfear
 * @since 5/19/13
 */
public abstract class AbstractConversionRateService implements ConversionRateService {

    private final Map<SymbolPair, List<ConversionRateListener>> listeners = new HashMap<>();

    protected void checkRatesAvailability() {
        for (Iterator<Map.Entry<SymbolPair, List<ConversionRateListener>>> iterator = listeners.entrySet().iterator(); iterator.hasNext();){
            Map.Entry<SymbolPair, List<ConversionRateListener>> entry = iterator.next();
            SymbolPair symbolPair = entry.getKey();
            double rate = getRate(symbolPair.symbol1, symbolPair.symbol2);
            if (!Double.isNaN(rate)){
                iterator.remove();
                for (ConversionRateListener listener: entry.getValue()){
                    listener.conversionRateAvailable(symbolPair.symbol1, symbolPair.symbol2, rate);
                }
            }
        }
    }

    @Override
    public void getRate(final String s1, final String s2, final ConversionRateListener conversionRateListener) {
        final double rate = getRate(s1, s2);
        if (!Double.isNaN(rate)){
            conversionRateListener.conversionRateAvailable(s1, s2, rate);
        } else {
            SymbolPair key = new SymbolPair(s1, s2);
            List<ConversionRateListener> conversionRateListeners = listeners.get(key);
            if (conversionRateListeners == null){
                conversionRateListeners = new ArrayList<>();
                listeners.put(key, conversionRateListeners);
            }
            conversionRateListeners.add(conversionRateListener);

        }
    }

    @Override
    public double convert(double v, String s1, String s2) {
        return v * getRate(s1, s2);
    }

    private static class SymbolPair {
        final String symbol1;
        final String symbol2;

        private SymbolPair(String symbol1, String symbol2) {
            if (symbol1 == null){
                throw new NullPointerException("symbol1 should not be null");
            }
            if (symbol2 == null){
                throw new NullPointerException("symbol2 should not be null");
            }

            this.symbol1 = symbol1;
            this.symbol2 = symbol2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SymbolPair that = (SymbolPair) o;

            if (!symbol1.equals(that.symbol1)) return false;
            if (!symbol2.equals(that.symbol2)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = symbol1.hashCode();
            result = 31 * result + symbol2.hashCode();
            return result;
        }
    }


}
