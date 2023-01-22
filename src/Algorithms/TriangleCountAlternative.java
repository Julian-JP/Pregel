package Algorithms;

import Measurements.Log;
import Measurements.SpearmanCorrelation;
import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;
import Tools.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TriangleCountAlternative {

    public static <NV, EV> long countapply(Graph<NV, EV> graph) {
        Graph<List<Integer>, Double> triangleGraph = graph.mapTo(x -> new ArrayList<>(), x -> 1.0);
        triangleGraph.removeSelfEdges();
        countapplyInternal(triangleGraph);
        return triangleGraph.toNodeStream().map(x -> x.getValue()).map(x -> x.size() == 1 ? x.get(0) : 0).reduce(0, Integer::sum) / 3;
    }

    public static <NV, EV> Log error(Graph<NV, EV> graph1, Graph<NV, EV> graph2) {
        Graph<List<Integer>, Double> triangleGraph1 = graph1.mapTo(x -> new ArrayList<>(), x -> 1.0);
        triangleGraph1.removeSelfEdges();
        Graph<List<Integer>, Double> triangleGraph2 = graph2.mapTo(x -> new ArrayList<>(), x -> 1.0);
        triangleGraph2.removeSelfEdges();
        long msg1 = countapplyInternal(triangleGraph1);
        long msg2 = countapplyInternal(triangleGraph2);

        double error = SpearmanCorrelation.compare(triangleGraph1.toNodeStream().map(x-> x.getValue().size() == 1 ? new Pair<>(x.getValue().get(0), x.getId()) : new Pair<>(0, x.getId())).sorted(Comparator.comparingInt(Pair::first)).map(Pair::second).toList(),
                triangleGraph2.toNodeStream().map(x-> x.getValue().size() == 1 ? new Pair<>(x.getValue().get(0), x.getId()) : new Pair<>(0, x.getId())).sorted(Comparator.comparingInt(Pair::first)).map(Pair::second).toList());

        return new Log(error, msg1, msg2, "");
    }

    private static long countapplyInternal(Graph<List<Integer>, Double> graph) {
        AtomicLong messageCount = new AtomicLong(0);
        BiFunction<List<Integer>, List<Integer>, List<Integer>> vertexFunction = (vertexVal, messages) -> {
            if (!vertexVal.isEmpty()) {
                int count = messages.stream().reduce(Integer::sum).get() / 2;
                ArrayList<Integer> ret = new ArrayList<>(1);
                ret.add(count);
                return ret;
            } else {
                return messages;
            }
        };
        Function<EdgeTriplet<List<Integer>, Double>, Integer> sendMsg = (triplet) -> {
            messageCount.incrementAndGet();
            if (triplet.srcAttr().isEmpty()) {
                return triplet.srcId();
            } else {
                HashSet<Integer> srcNeighbors = new HashSet<>();
                int count = 0;

                srcNeighbors.addAll(triplet.srcAttr());
                for (int i = 0; i < triplet.dstAttr().size(); i++) {
                    if (srcNeighbors.contains(triplet.dstAttr().get(i))) count++;
                }
                return count;
            }
        };
        Pregel.apply(graph, 2, vertexFunction, sendMsg);

        return messageCount.get();
    }
}