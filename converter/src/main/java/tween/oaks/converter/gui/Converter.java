package tween.oaks.converter.gui;

import com.tradable.api.common.Destroyable;
import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleProperties;
import com.tradable.api.component.state.DefaultPersistedStateHolder;
import com.tradable.api.component.state.PersistedStateHolder;
import tween.oaks.converter.gui.autocomplete.AutoCompleteChoiceListener;
import tween.oaks.converter.model.SymbolListener;
import tween.oaks.converter.model.SymbolValuesHolder;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public abstract class Converter extends JPanel implements WorkspaceModule {

    private static final double DEFAULT_VALUE = 100.0;
    public static final int UPDATE_PERIOD = 10000;

    private final SymbolValuesHolder symbolValuesHolder;

    private final JLayeredPane layeredPane = new JLayeredPane();
    private final JPanel mainPanel = new JPanel();

    private final Set<String> symbolsLeft = new HashSet<>();
    private final Map<String, ValuePanel> allPanels = new HashMap<>();
    private final LinkedHashMap<String, ValuePanel> currentPanels = new LinkedHashMap<>();
    private final Set<Destroyable> destroyables;
    private EmptyPanel emptyPanel = new EmptyPanel();
    private final StubPanel stubPanel = new StubPanel();
    private volatile ValuePanel rootPanel;
    private final Timer timer;

    public Converter(final SymbolValuesHolder symbolValuesHolder, Set<Destroyable> destroyables) {
        this.destroyables = destroyables;
        putClientProperty(WorkspaceModuleProperties.COMPONENT_TITLE, getTitle());

        this.symbolValuesHolder = symbolValuesHolder;

        Dimension d = new Dimension(500, 500);

        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setLocation(0, 0);
        mainPanel.setSize(d);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        setLayout(new BorderLayout());
        setPreferredSize(d);
        add(layeredPane, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mainPanel.setSize(getSize());
            }

        });

        DragMouseAdapter dragMouseAdapter = new DragMouseAdapter();
        layeredPane.addMouseListener(dragMouseAdapter);
        layeredPane.addMouseMotionListener(dragMouseAdapter);

        emptyPanel.setListener(new AutoCompleteChoiceListener() {
            @Override
            public void currencyChosen(String currency) {
                addValuePanel(currency);
            }
        });
        mainPanel.add(emptyPanel);

        symbolValuesHolder.addSymbolListener(new SymbolListener() {
            @Override
            public void symbolsAdded(Collection<String> symbols) {
                addSymbols(symbols);
            }
        });

        loadCurrenciesSetFromString(getDefaultSymbols());

        timer = new Timer(UPDATE_PERIOD, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rootPanel != null) {
                    recalculate(rootPanel);
                }
            }
        });
        timer.setRepeats(true);
        timer.setInitialDelay(500);
        timer.start();
    }

    protected abstract String getDefaultSymbols();

    protected abstract String getTitle();

    private void addSymbols(Collection<String> symbols) {
        List<String> filteredSymbols = new ArrayList<>(symbols);
        for (Iterator<String> iterator = filteredSymbols.iterator(); iterator.hasNext();) {
            String symbol = iterator.next();
            ValuePanel panel = currentPanels.get(symbol);
            if (panel != null && !panel.isCurrencyAvailable()){
                iterator.remove();
                initPanel(panel);
            }
        }

        symbolsLeft.addAll(filteredSymbols);

        if (!symbolsLeft.isEmpty()){
            emptyPanel.setCurrencies(symbolsLeft);
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        mainPanel.setPreferredSize(preferredSize);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        mainPanel.setSize(d);
    }

    private void addValuePanel(String currency) {
        final ValuePanel ValuePanel = getValuePanel(currency);

        mainPanel.add(ValuePanel, mainPanel.getComponents().length - 1);
        currentPanels.put(currency, ValuePanel);

        if (!symbolsLeft.remove(currency)) {
            ValuePanel.setCurrencyAvailable(false);
        } else {
            initPanel(ValuePanel);
        }

        emptyPanel.setCurrencies(symbolsLeft);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void initPanel(ValuePanel valuePanel) {
        valuePanel.setCurrencyAvailable(true);
        symbolValuesHolder.setSymbolValueListener(valuePanel.getSymbol(), valuePanel);
        symbolValuesHolder.setSymbolValue(valuePanel.getSymbol(), DEFAULT_VALUE);
        if (rootPanel != null){
            rootPanel.setRoot(false);
        }
        rootPanel = valuePanel;
        rootPanel.setRoot(true);
        valuePanel.requestFocusInWindow();
    }

    private void removePanel(ValuePanel ValuePanel) {
        mainPanel.remove(ValuePanel);
        currentPanels.remove(ValuePanel.getSymbol());

        if (ValuePanel.isCurrencyAvailable()){
            symbolsLeft.add(ValuePanel.getSymbol());
            symbolValuesHolder.removeSymbolValueListener(ValuePanel.getSymbol());
        }

        emptyPanel.setCurrencies(symbolsLeft);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private ValuePanel getValuePanel(String currency) {
        if (allPanels.containsKey(currency)){
            return allPanels.get(currency);
        } else {
            final ValuePanel valuePanel = createPanel(currency);
            valuePanel.addChangeValueListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    recalculate(valuePanel);
                }
            });
            valuePanel.addButtonMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    recalculate(valuePanel);
                }
            });
            valuePanel.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (rootPanel != null){
                        rootPanel.setRoot(false);
                    }
                    rootPanel = valuePanel;
                    rootPanel.setRoot(true);
                    recalculate(valuePanel);
                }

                @Override
                public void focusLost(FocusEvent e) {
                }
            });

            valuePanel.setListener(new ValuePanelListener() {
                @Override
                public void closeButtonClicked(ValuePanel panel) {
                    removePanel(panel);
                }
            });
            allPanels.put(currency, valuePanel);
            return valuePanel;
        }
    }

    protected abstract ValuePanel createPanel(String currency);

    private void recalculate(ValuePanel panel) {
        symbolValuesHolder.setSymbolValue(panel.getSymbol(), panel.getValue());
    }

    @Override
    public JComponent getVisualComponent() {
        return this;
    }

    @Override
    public void destroy() {
        timer.stop();
        for (Destroyable destroyable : destroyables) {
            destroyable.destroy();
        }
    }

    @Override
    public PersistedStateHolder getPersistedState() {
        DefaultPersistedStateHolder holder = new DefaultPersistedStateHolder();
        holder.setProperty("currencies", getCurrenciesAsString());
        return holder;
    }

    @Override
    public void loadPersistedState(PersistedStateHolder persistedStateHolder) {
        String currenciesString = persistedStateHolder.getProperty("currencies");
        if (currenciesString != null && !currenciesString.isEmpty()){
            removeAllPanels();
            loadCurrenciesSetFromString(currenciesString);
        }
    }

    private void removeAllPanels() {
        Collection<ValuePanel> values = new HashSet<>(currentPanels.values());
        for (ValuePanel cp: values){
            removePanel(cp);
        }
    }

    private void loadCurrenciesSetFromString(String currenciesString) {
        for (String node : parseCurrencies(currenciesString)) {
            addValuePanel(node);
        }
    }

    private List<String> parseCurrencies(String string) {
        return Arrays.asList(string.split(";"));
    }

    public String getCurrenciesAsString() {
        StringBuilder sb = new StringBuilder();
        for (String currency: currentPanels.keySet()){
            sb.append(currency).append(";");
        }
        return sb.toString();
    }

    public class DragMouseAdapter extends MouseAdapter {
        private ValuePanel dragPanel = null;
        private int stubPanelIndex = -1;
        private int dragPanelWidthDiv2;
        private int dragPanelHeightDiv2;

        @Override
        public void mousePressed(MouseEvent me) {
            JPanel panel = (JPanel) mainPanel.getComponentAt(me.getPoint());
            if (!(panel instanceof ValuePanel)) {
                return;
            }
            dragPanel = (ValuePanel) panel;

            stubPanelIndex = getComponentIndex(dragPanel, mainPanel);

            mainPanel.remove(dragPanel);
            mainPanel.add(stubPanel, stubPanelIndex);

            mainPanel.revalidate();
            mainPanel.repaint();

            Point point = SwingUtilities.convertPoint(mainPanel, dragPanel.getLocation(), layeredPane);

            dragPanelWidthDiv2 = me.getPoint().x - dragPanel.getLocation().x;
            dragPanelHeightDiv2 = me.getPoint().y - dragPanel.getLocation().y;

            dragPanel.setLocation(point);
            layeredPane.add(dragPanel, JLayeredPane.DRAG_LAYER);
            layeredPane.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (dragPanel == null) {
                return;
            }
            JPanel panel = (JPanel) mainPanel.getComponentAt(me.getPoint());
            if (panel instanceof ValuePanel){
                stubPanelIndex = getComponentIndex(panel, mainPanel);

                mainPanel.remove(stubPanel);
                mainPanel.add(stubPanel, stubPanelIndex);

                move(currentPanels, dragPanel.getSymbol(), stubPanelIndex);

                mainPanel.revalidate();
                mainPanel.repaint();
            }
            int x = me.getPoint().x - dragPanelWidthDiv2;
            int y = me.getPoint().y - dragPanelHeightDiv2;
            dragPanel.setLocation(x, y);
            layeredPane.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (dragPanel == null) {
                return;
            }
            layeredPane.remove(dragPanel);
            layeredPane.repaint();

            mainPanel.remove(stubPanel);
            mainPanel.add(dragPanel, stubPanelIndex);

            dragPanel = null;

            mainPanel.revalidate();
            mainPanel.repaint();
        }

    }

    private static <K, V> void move(LinkedHashMap<K, V> map, K key, int index) {
        if (!map.containsKey(key)){
            throw new IllegalArgumentException("map doesn't contain key: " + key);
        }
        LinkedHashMap<K, V> tempMap = new LinkedHashMap<>();
        V value = map.remove(key);
        int i = 0;
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<K, V> entry = iterator.next();
            if (i == index){
                tempMap.put(key, value);
            }
            tempMap.put(entry.getKey(), entry.getValue());
            iterator.remove();
            i++;
        }
        if (i == index){
            tempMap.put(key, value);
        }
        map.putAll(tempMap);
    }

    private static int getComponentIndex(Component child, JPanel parent){
        for (int i = 0; i < parent.getComponents().length; i++){
            if (child == parent.getComponents()[i]){
                return i;
            }
        }
        throw new IllegalArgumentException("Parent panel doesn't contain the given child");
    }

}
