package tween.oaks.converter.services;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.List;

/**
 * @author ochtarfear
 * @since 5/19/13
 */
public class ConversionRatesGraph {

    private final DirectedGraph<String, RateEdge> graph = new SimpleDirectedGraph<>(RateEdge.class);

    private static class RateEdge {
        double quote;

        public RateEdge(double quote) {
            this.quote = quote;
        }
    }

    protected void setConversionRate(String symbol1, String symbol2, double rate){
        if (!graph.containsVertex(symbol1)){
            graph.addVertex(symbol1);
        }
        if (!graph.containsVertex(symbol2)){
            graph.addVertex(symbol2);
        }
        if (graph.containsEdge(symbol1, symbol2)){
            graph.removeEdge(symbol1, symbol2);
            graph.removeEdge(symbol2, symbol1);
        }
        graph.addEdge(symbol1, symbol2, new RateEdge(rate));
        graph.addEdge(symbol2, symbol1, new RateEdge(1.0 / rate));

    }

    public double getRate(String symbol1, String symbol2) {
        if (!graph.containsVertex(symbol1) || !graph.containsVertex(symbol2)){
            return Double.NaN;
        }
        List<RateEdge> path = DijkstraShortestPath.findPathBetween(graph, symbol1, symbol2);
        if (path == null){
            return Double.NaN;
        }
        double rate = 1.0;
        for (RateEdge e: path){
            rate *= e.quote;
        }
        return rate;
    }


}
