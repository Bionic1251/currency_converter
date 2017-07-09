package tween.oaks.converter.model;

/**
 * @author ochtarfear
 * @since 2/24/13
 */
class SymbolNode {

    private final String symbol;

    private volatile SymbolValueListener listener;
    private volatile double value = -1;

    SymbolNode(String symbol) {
        this.symbol = symbol;
    }

    void setValue(double value){
        double oldValue = this.value;
        this.value = value;

        if (listener != null && oldValue != value){
            listener.valueUpdated(symbol, value);
        }
    }

    String getSymbol() {
        return symbol;
    }

    void setListener(SymbolValueListener listener) {
        this.listener = listener;
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }

        if (!(obj instanceof SymbolNode)){
            return false;
        }

        return symbol.equals(((SymbolNode) obj).getSymbol());
    }
}
