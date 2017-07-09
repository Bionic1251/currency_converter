package tween.oaks.converter.model;

import com.tradable.api.services.conversion.ConversionRateListener;
import com.tradable.api.services.conversion.ConversionRateService;

import java.util.*;

/**
 * @author ochtarfear
 * @since 3/24/13
 */
public class SymbolValuesHolderImpl implements SymbolValuesHolder {

    private final ConversionRateService conversionRateService;
    private final Map<String, SymbolNode> symbols = new HashMap<>();
    private final List<SymbolListener> listeners = new ArrayList<>();

    public SymbolValuesHolderImpl(ConversionRateService conversionRateService, SymbolProvider symbolProvider) {
        this.conversionRateService = conversionRateService;

        symbolProvider.addSymbolListener(new SymbolListener() {
            @Override
            public void symbolsAdded(Collection<String> symbols) {
                processSymbols(symbols);
            }
        });

    }

    private void processSymbols(Collection<String> symbols) {
        Set<String> newSymbols = new HashSet<>();
        for (String symbol : symbols) {
            if (addSymbol(symbol)){
                newSymbols.add(symbol);
            }
        }
        if (!newSymbols.isEmpty() && !listeners.isEmpty()){
            for (SymbolListener listener: listeners){
                listener.symbolsAdded(newSymbols);
            }
        }
    }

    private boolean addSymbol(String symbol) {
        if (symbols.isEmpty()){
            putCurrency(symbol);
            return true;
        }
        if (symbols.containsKey(symbol)){
            return false;
        } else {
            putCurrency(symbol);
            return true;
        }

        // the following code is for adding new symbol only when its conversion rate is available
        // if desired, uncomment it and comment the else block above
/*
        for (String s: symbols.keySet()){
            if (!Double.isNaN(conversionRateService.getRate(symbol, s))){
                putCurrency(symbol);
                return true;
            }
        }
        for (String s: symbols.keySet()){
            conversionRateService.getRate(symbol, s, new ConversionRateListener() {
                @Override
                public void conversionRateAvailable(String s, String s2, double v) {
                    if (!symbols.containsKey(s)){
                        putCurrency(s);
                        for (SymbolListener listener: listeners){
                            listener.symbolsAdded(Arrays.asList(s));
                        }
                    }
                }
            });
        }
        return false;
*/
    }

    private void putCurrency(String currency) {
        symbols.put(currency, new SymbolNode(currency));
    }

    @Override
    public void addSymbolListener(SymbolListener listener) {
        listeners.add(listener);
        listener.symbolsAdded(symbols.keySet());
    }

    @Override
    public void setSymbolValue(String symbol, final double value) {
        if (!symbols.containsKey(symbol)){
            throw new IllegalArgumentException("Holder doesn't contain " + symbol);
        }
        for (Map.Entry<String, SymbolNode> entry: symbols.entrySet()){
            String nextCurrency = entry.getKey();
            final SymbolNode symbolNode = entry.getValue();
            if (!nextCurrency.equals(symbol)){
                try {
                    double rate = conversionRateService.getRate(symbol, nextCurrency);
                    symbolNode.setValue(rate * value);
                    if (Double.isNaN(rate)){
                        conversionRateService.getRate(symbol, nextCurrency, new ConversionRateListener() {
                            @Override
                            public void conversionRateAvailable(String from, String to, double conversionRate) {
                                if (!Double.isNaN(conversionRate)){
                                    symbolNode.setValue(conversionRate * value);
                                }
                            }
                        });
                    }
                } catch (IllegalArgumentException e){
                    // skip it
                }
            } else {
                symbolNode.setValue(value);
            }
        }
    }

    @Override
    public void setSymbolValueListener(String symbol, SymbolValueListener listener) {
        if (!symbols.containsKey(symbol)){
            throw new IllegalArgumentException("Holder doesn't contain " + symbol);
        }
        symbols.get(symbol).setListener(listener);
    }

    @Override
    public void removeSymbolValueListener(String symbol) {
        if (!symbols.containsKey(symbol)){
            throw new IllegalArgumentException("Holder doesn't contain " + symbol);
        }
        symbols.get(symbol).setListener(null);
    }

}
