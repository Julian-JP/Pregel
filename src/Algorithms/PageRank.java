package Algorithms;

import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;
import Tools.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PageRank {

    public static <NV, EV> Graph<Pair<Double, NV>, EV> apply(Graph<NV, EV> graph, int maxSuperSteps) {
        Graph<Pair<Double, NV>, EV> pageRankGraph = graph.mapTo(x -> new Pair<>((1.0 / graph.countNodes()), x), x -> x);

        BiFunction<Pair<Double, NV>, List<Double>, Pair<Double, NV>> vertexFunction = (vertexVal, message) -> {
            vertexVal.setFirst(0.15 + 0.85 * message.stream().reduce(0.0, Double::sum));
            return vertexVal;
        };

        Function<EdgeTriplet<Pair<Double, NV>, EV>, Double> sendMsg = (triplet) -> triplet.srcAttr().first() / triplet.srcNodeDegree();

        return Pregel.apply(pageRankGraph, maxSuperSteps, vertexFunction, sendMsg);
    }

}
