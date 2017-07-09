package tween.oaks.converter.services;

import com.tradable.api.services.conversion.ConversionRateListener;
import com.tradable.api.services.conversion.ConversionRateService;

import javax.swing.*;

/**
 * @author ochtarfear
 * @since 3/24/13
 */
public class CombinedConversionRateService implements ConversionRateService {

    private final ConversionRateService firstService;
    private final ConversionRateService secondService;

    public CombinedConversionRateService(ConversionRateService firstService, ConversionRateService secondService) {
        this.firstService = firstService;
        this.secondService = secondService;
    }

    @Override
    public double convert(double v, String s) {
        throw new UnsupportedOperationException("Can't know the account's currency");
    }

    @Override
    public double convert(double v, String s1, String s2) {
        return v * getRate(s1, s2);
    }

    @Override
    public double getRate(String s1, String s2) {
        double rate = firstService.getRate(s1, s2);
        if (Double.isNaN(rate)){
            rate = secondService.getRate(s1, s2);
        }
        return rate;
    }

    @Override
    public void getRate(String s1, String s2, ConversionRateListener conversionRateListener) {
        final Listener listener = new Listener(conversionRateListener);

        ConversionRateListener rateListener = new ConversionRateListener() {
            @Override
            public void conversionRateAvailable(final String s1, final String s2, final double v) {
                if (!Double.isNaN(v) && !listener.notified) {
                    listener.notified = true;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.listener.conversionRateAvailable(s1, s2, v);
                        }
                    });
                }
            }
        };

        firstService.getRate(s1, s2, rateListener);
        secondService.getRate(s1, s2, rateListener);
    }

    private static class Listener {
        volatile boolean notified = false;
        final ConversionRateListener listener;

        private Listener(ConversionRateListener listener) {
            this.listener = listener;
        }
    }
}
