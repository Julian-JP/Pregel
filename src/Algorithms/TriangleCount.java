package Algorithms;

import Measurements.SpearmanCorrelation;
import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;
import Tools.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TriangleCount {
    public static <NV, EV> long countApply(Graph<NV, EV> graph) {

        Graph<List<Integer[]>, Double> triangleGraph = graph.mapTo(x -> new ArrayList<>(), x -> 0.0);
        triangleGraph.removeSelfEdges();
        Graph<List<Integer[]>, Double> resTriangle = Pregel.apply(triangleGraph, 2, vertexFunctionTriangle, sendMsgTriangle);

        return resTriangle.toNodeStream().map(x -> x.getValue()).map(List::size).reduce(0, Integer::sum) / 3;
    }

    public static <NV, EV> double error(Graph<NV, EV> graph1, Graph<NV, EV> graph2) {
        BiFunction<Pair<List<Integer[]>, NV>, List<List<Integer[]>>, Pair<List<Integer[]>, NV>> vertexFunctionTriangle = (vertexVal, messages) -> {
            if (!vertexVal.first().isEmpty()) {
                HashSet<ArrayList<Integer>> set = new HashSet<>();
                return new Pair<>(messages.stream().flatMap(Collection::stream).filter(x -> {
                    ArrayList<Integer> temp = new ArrayList<>(2);
                    temp.add(x[1]);
                    temp.add(x[0]);

                    if (set.contains(temp)) {
                        return true;
                    } else {
                        ArrayList<Integer> hLst = new ArrayList<>(2);
                        hLst.add(x[0]);
                        hLst.add(x[1]);
                        set.add(hLst);
                        return false;
                    }
                }).collect(Collectors.toList()), vertexVal.second());
            } else {
                return new Pair<>(messages.stream().flatMap(Collection::stream).collect(Collectors.toList()), vertexVal.second());
            }
        };
        Function<EdgeTriplet<Pair<List<Integer[]>, NV>, EV>, List<Integer[]>> sendMsgTriangle = (triplet) -> {
            if (triplet.srcAttr().first().isEmpty()) {
                Integer[] temp = {triplet.srcId(), triplet.dstId()};

                ArrayList<Integer[]> ret = new ArrayList<>(1);
                ret.add(temp);
                return ret;
            } else {
                return triplet.srcAttr().first();
            }
        };



        Graph<Pair<List<Integer[]>, NV>, EV> triangleGraph1 = graph1.mapTo(x -> new Pair<List<Integer[]>, NV>(new ArrayList<>(), x), x -> x);
        triangleGraph1.removeSelfEdges();
        Graph<Pair<List<Integer[]>, NV>, EV> triangleGraph2 = graph2.mapTo(x -> new Pair<List<Integer[]>, NV>(new ArrayList<>(), x), x -> x);
        triangleGraph2.removeSelfEdges();
        Graph<Pair<List<Integer[]>, NV>, EV> resTriangle1 = Pregel.apply(triangleGraph1, 2, vertexFunctionTriangle, sendMsgTriangle);
        Graph<Pair<List<Integer[]>, NV>, EV> resTriangle2 = Pregel.apply(triangleGraph2, 2, vertexFunctionTriangle, sendMsgTriangle);

        return SpearmanCorrelation.compare(resTriangle1.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList(),
                resTriangle2.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList());
    }
    private static BiFunction<List<Integer[]>, List<List<Integer[]>>, List<Integer[]>> vertexFunctionTriangle = (vertexVal, messages) -> {
        if (!vertexVal.isEmpty()) {
            HashSet<ArrayList<Integer>> set = new HashSet<>();
            return messages.stream().flatMap(Collection::stream).filter(x -> {
                ArrayList<Integer> temp = new ArrayList<>(2);
                temp.add(x[1]);
                temp.add(x[0]);

                if (set.contains(temp)) {
                    return true;
                } else {
                    ArrayList<Integer> hLst = new ArrayList<>(2);
                    hLst.add(x[0]);
                    hLst.add(x[1]);
                    set.add(hLst);
                    return false;
                }
            }).collect(Collectors.toList());
        } else {
            return messages.stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
    };
    private static Function<EdgeTriplet<List<Integer[]>, Double>, List<Integer[]>> sendMsgTriangle = (triplet) -> {
        if (triplet.srcAttr().isEmpty()) {
            Integer[] temp = {triplet.srcId(), triplet.dstId()};

            ArrayList<Integer[]> ret = new ArrayList<>(1);
            ret.add(temp);
            return ret;
        } else {
            return triplet.srcAttr();
        }
    };
}
