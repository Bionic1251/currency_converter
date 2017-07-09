package tween.oaks.converter.model;

/**
 * @author ochtarfear
 * @since 2/24/13
 */
public interface SymbolValuesHolder extends SymbolProvider {

    void setSymbolValue(String symbol, double value);

    void setSymbolValueListener(String symbol, SymbolValueListener listener);

    void removeSymbolValueListener(String symbol);

}
