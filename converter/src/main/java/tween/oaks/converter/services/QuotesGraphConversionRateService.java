package tween.oaks.converter.services;

import com.tradable.api.entities.Instrument;
import com.tradable.api.entities.InstrumentType;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.instrument.InstrumentServiceListener;
import com.tradable.api.services.instrument.InstrumentUpdateEvent;
import com.tradable.api.services.marketdata.Quote;
import com.tradable.api.services.marketdata.QuoteTickEvent;
import com.tradable.api.services.marketdata.QuoteTickListener;
import com.tradable.api.services.marketdata.QuoteTickSubscription;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ochtarfear
 * @since 2/22/13
 */
public class QuotesGraphConversionRateService extends AbstractConversionRateService {

    private final ConversionRatesGraph graph = new ConversionRatesGraph();

    public QuotesGraphConversionRateService(final QuoteTickSubscription quoteTickSubscription, InstrumentService instrumentService) {

        quoteTickSubscription.setListener(new QuoteTickListener() {
            private void setConversionRate(String symbol, double mid) {
                if(symbol.contains("/")) {
                    String[] currencies = symbol.split("/");
                    graph.setConversionRate(currencies[0], currencies[1], mid);
                }else {
                    graph.setConversionRate(symbol.substring(0, 3), symbol.substring(3), mid);
                }
            }

            @Override
            public void quotesUpdated(QuoteTickEvent quoteTickEvent) {
                for (String symbol: quoteTickEvent.getSymbols()){
                    Quote bid = quoteTickSubscription.getBid(symbol);
                    Quote ask = quoteTickSubscription.getAsk(symbol);
                    double mid = (bid.getPrice() + ask.getPrice()) / 2.0;
                    setConversionRate(symbol, mid);
                }
                checkRatesAvailability();
            }
        });

        Set<String> symbols = new HashSet<>();
        for (Instrument i: instrumentService.getInstruments()){
            if (i.getType().equals(InstrumentType.FOREX) && i.getSymbol().length() == 6 || i.getSymbol().contains("/")){
                symbols.add(i.getSymbol());
            }
        }
        quoteTickSubscription.addSymbols(symbols);

        instrumentService.addListener(new InstrumentServiceListener() {
            @Override
            public void instrumentsUpdated(InstrumentUpdateEvent instrumentUpdateEvent) {
                Set<String> symbols = new HashSet<>();
                for (Instrument i : instrumentUpdateEvent.getUpdatedInstruments().values()) {
                    if (i.getType().equals(InstrumentType.FOREX) && i.getSymbol().length() == 6 || i.getSymbol().contains("/")){
                        symbols.add(i.getSymbol());
                    }
                }
                quoteTickSubscription.addSymbols(symbols);
            }
        });

    }

    @Override
    public double convert(double v, String s) {
        throw new UnsupportedOperationException("Graph doesn't know the account's currency");
    }

    @Override
    public double getRate(String currency1, String currency2) {
        return graph.getRate(currency1, currency2);
    }

}
