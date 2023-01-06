import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Pregel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        File facebookCombined = new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt");
        int maxFacebook = 4039;
        boolean addOppositeFacebook = true;

        File emailEnron = new File("/run/media/julian/CBE7-42BF/Seminar/Email-Enron.txt");
        int maxEnron = 36692;
        boolean addOppositeEnron = false;

        File roadNWPA = new File("/run/media/julian/CBE7-42BF/Seminar/roadNet-PA.txt");
        int maxRoadNWPA = 1088092;
        boolean addOppositeRoadNWPA = false;

        File roadNWCA = new File("/run/media/julian/CBE7-42BF/Seminar/roadNet-CA.txt");
        int maxRoadNWCA = 1965206;
        boolean addOppositeRoadNWCA = false;

        BiFunction<List<Integer[]>, List<List<Integer[]>>, List<Integer[]>> vertexFunctionTriangle = (vertexVal, messages) -> {
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

        Function<EdgeTriplet<List<Integer[]>, Double>, List<Integer[]>> sendMsgTriangle = new Function<>() {
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

        Graph<List<Integer[]>, Double> triangleGraph;
        try {
            triangleGraph = new Graph<List<Integer[]>, Double>(roadNWCA, "\t", maxRoadNWCA, new ArrayList<>(), 1.0, addOppositeRoadNWCA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Graph<List<Integer[]>, Double> resTriangle = Pregel.apply(triangleGraph, new ArrayList<>(), 2, vertexFunctionTriangle, sendMsgTriangle);
        System.out.println(resTriangle.toNodeStream().map(x->x.getValue()).map(List::size).reduce(0, Integer::sum) / 3);
    }
}