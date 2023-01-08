package Algorithms;

import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TriangleCount {
    public static <NV, EV> long count(Graph<NV, EV> graph) {

        Graph<List<Integer[]>, Double> triangleGraph = graph.mapTo(x -> new ArrayList<>(), x -> 0.0);

        Graph<List<Integer[]>, Double> resTriangle = Pregel.apply(triangleGraph, new ArrayList<>(), 2, vertexFunctionTriangle, sendMsgTriangle);


        return resTriangle.toNodeStream().map(x->x.getValue()).map(List::size).reduce(0, Integer::sum) / 3;
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
    private static Function<EdgeTriplet<List<Integer[]>, Double>, List<Integer[]>> sendMsgTriangle = new Function<>() {
        @Override
        public List<Integer[]> apply(EdgeTriplet<List<Integer[]>, Double> triplet) {
            if (triplet.srcAttr().isEmpty()) {
                Integer[] temp = {triplet.srcId(), triplet.dstId()};

                ArrayList<Integer[]> ret = new ArrayList<>(1);
                ret.add(temp);
                return ret;
            } else {
                return triplet.srcAttr();
            }
        }
    };
}
