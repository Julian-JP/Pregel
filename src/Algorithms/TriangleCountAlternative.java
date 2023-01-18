package Algorithms;

import Measurements.SpearmanCorrelation;
import Pregel.Graph;
import Pregel.Pregel;
import Pregel.EdgeTriplet;
import Tools.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TriangleCountAlternative {

    public static <NV, EV> long countpply(Graph<NV, EV> graph) {
        Graph<List<Integer>, Double> triangleGraph = graph.mapTo(x -> new ArrayList<>(), x -> 0.0);
        triangleGraph.removeSelfEdges();
        Graph<List<Integer>, Double> resTriangle = Pregel.apply(triangleGraph, 2, vertexAlternativeFunctionTriangle, sendMsgAlternativeTriangle);

        return resTriangle.toNodeStream().map(x -> x.getValue()).map(x -> x.size() == 1 ? x.get(0) : 0).reduce(0, Integer::sum) / 3;
    }

    private static BiFunction<List<Integer>, List<Integer>, List<Integer>> vertexAlternativeFunctionTriangle = (vertexVal, messages) -> {
        if (!vertexVal.isEmpty()) {
            int count = messages.stream().reduce((x,y) -> x +y).get() / 2;
            ArrayList<Integer> ret = new ArrayList<>(1);
            ret.add(count);
            return ret;
        } else {
            return messages;
        }
    };
    private static Function<EdgeTriplet<List<Integer>, Double>, Integer> sendMsgAlternativeTriangle = (triplet) -> {
        if (triplet.srcAttr().isEmpty()) {
            return triplet.srcId();
        } else {
            HashSet<Integer> srcNeighbors = new HashSet<>();
            int count = 0;

            for (int i = 0; i < triplet.srcAttr().size(); i++) {
                srcNeighbors.add(triplet.srcAttr().get(i));
            }
            for (int i = 0; i < triplet.dstAttr().size(); i++) {
                if (srcNeighbors.contains(triplet.dstAttr().get(i))) count++;
            }
            return count;
        }
    };

    public static <NV, EV> double error(Graph<NV, EV> graph1, Graph<NV, EV> graph2) {
        BiFunction<Pair<List<Integer>, NV>, List<Integer>, Pair<List<Integer>, NV>> vertexFunctionTriangle = (vertexVal, messages) -> {
            if (!vertexVal.first().isEmpty()) {
                int count = messages.stream().reduce((x,y) -> x +y).get() / 2;
                ArrayList<Integer> ret = new ArrayList<>(1);
                ret.add(count);
                return new Pair<>(ret, vertexVal.second());
            } else {
                return new Pair<>(messages, vertexVal.second());
            }
        };
        Function<EdgeTriplet<Pair<List<Integer>, NV>, EV>, Integer> sendMsgTriangle = (triplet) -> {
            if (triplet.srcAttr().first().isEmpty()) {
                return triplet.srcId();
            } else {
                HashSet<Integer> srcNeighbors = new HashSet<>();
                int count = 0;

                for (int i = 0; i < triplet.srcAttr().first().size(); i++) {
                    srcNeighbors.add(triplet.srcAttr().first().get(i));
                }
                for (int i = 0; i < triplet.dstAttr().first().size(); i++) {
                    if (srcNeighbors.contains(triplet.dstAttr().first().get(i))) count++;
                }
                return count;
            }
        };



        Graph<Pair<List<Integer>, NV>, EV> triangleGraph1 = graph1.mapTo(x -> new Pair<List<Integer>, NV>(new ArrayList<>(), x), x -> x);
        triangleGraph1.removeSelfEdges();
        Graph<Pair<List<Integer>, NV>, EV> triangleGraph2 = graph2.mapTo(x -> new Pair<List<Integer>, NV>(new ArrayList<>(), x), x -> x);
        triangleGraph2.removeSelfEdges();
        Graph<Pair<List<Integer>, NV>, EV> resTriangle1 = Pregel.apply(triangleGraph1, 2, vertexFunctionTriangle, sendMsgTriangle);
        Graph<Pair<List<Integer>, NV>, EV> resTriangle2 = Pregel.apply(triangleGraph2, 2, vertexFunctionTriangle, sendMsgTriangle);

        return SpearmanCorrelation.compare(resTriangle1.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList(),
                resTriangle2.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList());
    }
}
