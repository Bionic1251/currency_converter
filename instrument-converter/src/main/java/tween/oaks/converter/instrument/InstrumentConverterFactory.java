package tween.oaks.converter.instrument;

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
import tween.oaks.converter.services.*;

import java.util.HashSet;

public class InstrumentConverterFactory implements WorkspaceModuleFactory {

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
        return "instrument-converter.factory";
    }

    @Override
    public WorkspaceModule createModule() {
        HashSet<Destroyable> destroyables = new HashSet<>();
        SymbolProvider currenciesProvider = new InstrumentsProvider(instrumentService);
        FairConversionRateService fairConversionRateService = new FairConversionRateService(conversionRateService, currenciesProvider);
        destroyables.add(fairConversionRateService);
        SymbolValuesHolder currencyValuesGraph = new SymbolValuesHolderImpl(fairConversionRateService, currenciesProvider);
        return new InstrumentConverter(currencyValuesGraph, destroyables);
    }
}
