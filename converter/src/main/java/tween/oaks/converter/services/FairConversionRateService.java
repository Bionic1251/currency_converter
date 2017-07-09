package tween.oaks.converter.services;

import com.tradable.api.common.Destroyable;
import com.tradable.api.services.conversion.ConversionRateService;
import tween.oaks.converter.model.SymbolListener;
import tween.oaks.converter.model.SymbolProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * @author ochtarfear
 * @since 4/11/13
 */
public class FairConversionRateService extends AbstractConversionRateService implements Destroyable {

    private final ConversionRateService conversionRateService;
    private final Set<String> symbols = new HashSet<>();
    private final ConversionRatesGraph graph = new ConversionRatesGraph();
    private final Timer timer;

    public FairConversionRateService(ConversionRateService conversionRateService, SymbolProvider symbolProvider) {
        this.conversionRateService = conversionRateService;

        symbolProvider.addSymbolListener(new SymbolListener() {

            @Override
            public void symbolsAdded(Collection<String> addedSymbols) {
                symbols.addAll(addedSymbols);
            }
        });

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRates();
                checkRatesAvailability();
            }
        });
        timer.setRepeats(true);
        timer.setInitialDelay(1000);
        timer.start();
    }

    private void updateRates() {
        for (String symbol1: symbols) {
            for (String symbol2 : symbols) {
                if (symbol1.equals(symbol2)){
                    continue;
                }

                double rate = conversionRateService.getRate(symbol1, symbol2);
                if (!Double.isNaN(rate)){
                    graph.setConversionRate(symbol1, symbol2, rate);
                }
            }
        }
    }

    @Override
    public double convert(double v, String s1) {
        throw new UnsupportedOperationException("Graph doesn't know the account's currency");
    }

    @Override
    public double getRate(String s1, String s2) {
        return graph.getRate(s1, s2);
    }

    @Override
    public void destroy(){
        timer.stop();
    }
}
