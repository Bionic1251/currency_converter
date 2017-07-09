package tween.oaks.converter.services;

import com.tradable.api.services.marketdata.Quote;
import com.tradable.api.services.marketdata.QuoteTickEvent;
import com.tradable.api.services.marketdata.QuoteTickListener;
import com.tradable.api.services.marketdata.QuoteTickSubscription;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vsalmin
 * @since 17.11.12
 */
public class PeriodicTicker implements QuoteTickSubscription {

    private final int period;

    private final QuoteTickSubscription quoteTickSubscription;
    private final Set<String> symbols = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private volatile Timer timer;

    public PeriodicTicker(QuoteTickSubscription quoteTickSubscription, int periodInMillis) {
        this.quoteTickSubscription = quoteTickSubscription;
        this.period = periodInMillis;
    }

    @Override
    public Quote getAsk(String s) {
        return quoteTickSubscription.getAsk(s);
    }

    @Override
    public Quote getBid(String s) {
        return quoteTickSubscription.getBid(s);
    }

    @Override
    public void setListener(final QuoteTickListener quoteTickListener) {
        if (timer != null){
            timer.stop();
        }
        timer = new Timer(period, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final Set<String> updateSymbols = new HashSet<>(symbols);
                if (!updateSymbols.isEmpty()){
                    QuoteTickEvent quoteTickEvent = new QuoteTickEvent() {
                        @Override
                        public Set<String> getSymbols() {
                            return updateSymbols;
                        }
                    };
                    quoteTickListener.quotesUpdated(quoteTickEvent);
                }
            }
        });
        timer.setRepeats(true);
        timer.setInitialDelay(0);
        timer.start();
    }

    @Override
    public void addSymbol(String s) {
        symbols.add(s);
        quoteTickSubscription.addSymbol(s);
    }

    @Override
    public void addSymbols(Set<String> strings) {
        symbols.addAll(strings);
        quoteTickSubscription.addSymbols(strings);
    }

    @Override
    public void setSymbol(String s) {
        symbols.clear();
        symbols.add(s);
        quoteTickSubscription.setSymbol(s);
    }

    @Override
    public void setSymbols(Set<String> strings) {
        symbols.clear();
        symbols.addAll(strings);
        quoteTickSubscription.setSymbols(strings);
    }

    @Override
    public void removeSymbol(String s) {
        symbols.remove(s);
        quoteTickSubscription.removeSymbol(s);
    }

    @Override
    public void removeSymbols(Set<String> strings) {
        symbols.removeAll(strings);
        quoteTickSubscription.removeSymbols(strings);
    }

    @Override
    public void destroy() {
        quoteTickSubscription.destroy();
        timer.stop();
    }

    public QuoteTickSubscription getSubscription() {
        return quoteTickSubscription;
    }
}
