package tween.oaks.converter.currency;

import com.tradable.api.common.Destroyable;
import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleCategory;
import com.tradable.api.component.WorkspaceModuleFactory;
import com.tradable.api.services.conversion.ConversionRateService;
import com.tradable.api.services.instrument.InstrumentService;
import com.tradable.api.services.marketdata.QuoteTickService;
import org.springframework.beans.factory.annotation.Autowired;
import tween.oaks.converter.model.SymbolProvider;
import tween.oaks.converter.model.SymbolValuesHolder;
import tween.oaks.converter.model.SymbolValuesHolderImpl;
import tween.oaks.converter.services.CombinedConversionRateService;
import tween.oaks.converter.services.CurrenciesProvider;
import tween.oaks.converter.services.FairConversionRateService;
import tween.oaks.converter.services.QuotesGraphConversionRateService;

import java.util.HashSet;

public class CurrencyConverterFactory implements WorkspaceModuleFactory {

    @Autowired
    private QuoteTickService quoteTickerService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private ConversionRateService conversionRateService;

    @Override
    public WorkspaceModuleCategory getCategory() {
        return WorkspaceModuleCategory.MISCELLANEOUS;
    }

    @Override
    public String getDisplayName() {
        return AppSettings.APP_NAME;
    }

    @Override
    public String getFactoryId() {
        return "currency-converter.factory";
    }

    @Override
    public WorkspaceModule createModule() {
        HashSet<Destroyable> destroyables = new HashSet<>();
        SymbolProvider currenciesProvider = new CurrenciesProvider(instrumentService);
        FairConversionRateService firstService = new FairConversionRateService(conversionRateService, currenciesProvider);
        destroyables.add(firstService);
        QuotesGraphConversionRateService secondService = new QuotesGraphConversionRateService(quoteTickerService.createSubscription(), instrumentService);
        CombinedConversionRateService combinedConversionRateService = new CombinedConversionRateService(firstService, secondService);
        SymbolValuesHolder currencyValuesGraph = new SymbolValuesHolderImpl(combinedConversionRateService, currenciesProvider);
        return new CurrencyConverter(currencyValuesGraph, destroyables);
    }
}
