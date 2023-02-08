package Algorithms;

import Measurements.SpearmanCorrelation;
import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;
import Pregel.ExtendedNode;
import Tools.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TriangleCount {
    public static <NV, EV> double error(Graph<NV, EV> graph1, Graph<NV, EV> graph2) {
        BiFunction<ExtendedNode<Pair<List<Integer[]>, NV>, Double>, List<List<Integer[]>>, Pair<List<Integer[]>, NV>> vertexFunctionTriangle = (vertex, messages) -> {
            Pair<List<Integer[]>, NV> vertexVal = vertex.getValue();
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
        Function<EdgeTriplet<Pair<List<Integer[]>, NV>, Double>, List<Integer[]>> sendMsgTriangle = (triplet) -> {
            if (triplet.srcAttr().first().isEmpty()) {
                Integer[] temp = {triplet.srcId(), triplet.dstId()};

                ArrayList<Integer[]> ret = new ArrayList<>(1);
                ret.add(temp);
                return ret;
            } else {
                return triplet.srcAttr().first();
            }
        };



        Graph<Pair<List<Integer[]>, NV>, Double> triangleGraph1 = graph1.mapTo(x -> new Pair<List<Integer[]>, NV>(new ArrayList<>(), x), x -> 1.0);
        triangleGraph1.removeSelfEdges();
        Graph<Pair<List<Integer[]>, NV>, Double> triangleGraph2 = graph2.mapTo(x -> new Pair<List<Integer[]>, NV>(new ArrayList<>(), x), x -> 1.0);
        triangleGraph2.removeSelfEdges();
        Graph<Pair<List<Integer[]>, NV>, Double> resTriangle1 = Pregel.apply(triangleGraph1, 2, vertexFunctionTriangle, sendMsgTriangle);
        Graph<Pair<List<Integer[]>, NV>, Double> resTriangle2 = Pregel.apply(triangleGraph2, 2, vertexFunctionTriangle, sendMsgTriangle);

        return SpearmanCorrelation.compare(resTriangle1.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList(),
                resTriangle2.toNodeStream().sorted(Comparator.comparingInt(x -> x.getValue().first().size())).map(x -> x.getValue().second()).toList());
    }
    private static BiFunction<ExtendedNode<List<Integer[]>, Double>, List<List<Integer[]>>, List<Integer[]>> vertexFunctionTriangle = (vertex, messages) -> {
        List<Integer[]> vertexVal = vertex.getValue();

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
