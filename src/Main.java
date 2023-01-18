import Algorithms.PageRank;
import Algorithms.TriangleCount;
import Algorithms.TriangleCountAlternative;
import Measurements.SpearmanCorrelation;
import Pregel.Edge;
import Pregel.Graph;
import Pregel.Node;
import Tools.Pair;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Graphs from https://snap.stanford.edu/data/
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

        File astroPh = new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt");
        int maxAstroPh = 18772;
        boolean addOppositeAstroPh = false;

        File twitch = new File("/run/media/julian/CBE7-42BF/Seminar/large_twitch_edges.csv");
        int maxTwitch = 168114;
        boolean addOppositeTwitch = true;


        Double[] probstemp = {1.0, 0.825, 0.825, 0.55, 0.55, 0.825, 1.0, 0.825, 0.825, 0.55, 1.0};
        List<Double> probs = Arrays.asList(probstemp);
        List<Double> probsInv = probs.stream().map(x -> 1 - x).toList();
        double[] test = {0.75, 0.75, 0.75, 0.75, 0.75, 0.75, 0.75, 0.75, 0.75, 0.75, 0.75};
        BigDecimal[] res = new BigDecimal[12];

        for (int i = 0; i < 12; i++) {
            res[i] = new BigDecimal(0.0);
        }

        double r1 = 0.0;
        double r2 = 0.0;
        double testres;
        for (int i = 0; i < 2048; i++) {
            int ones = 0;
            BigDecimal prob = new BigDecimal(1.0);
            for (int j = 0; j < 11; ++j) {
                String bits = Integer.toBinaryString(i);
                while (bits.length() < 11) {
                    bits = "0" + bits;
                }
                if (bits.charAt(j) == '1') {
                    prob = prob.multiply(BigDecimal.valueOf(probs.get(j)));
                    ones++;
                } else {
                    prob = prob.multiply(BigDecimal.valueOf(1 - probs.get(j)));
                }

            }
            res[ones] = res[ones].add(prob);
        }

        BigDecimal temp = new BigDecimal(0);
        for (int i = 0; i < res.length; i++) {
            temp = temp.add(res[i].multiply(BigDecimal.valueOf(i)));
        }
        System.out.println(res);

        Graph<Integer, Boolean> triangleGraph1;
        Graph<Integer, Boolean> triangleGraph2;
        Graph<Integer, Boolean> triangleGraph3;
        Graph<Integer, Boolean> triangleGraph4;
        Graph<Integer, Boolean> triangleGraph5;
        Graph<Integer, Boolean> triangleGraph6;
        Graph<Integer, Boolean> triangleGraph7;
        Graph<Integer, Boolean> triangleGraph8;
        Graph<Integer, Boolean> triangleGraph9;
        Graph<Integer, Boolean> triangleGraph10;
        Graph<Integer, Boolean> pagerankGraph1;
        Graph<Integer, Boolean> pagerankGraph2;
        Graph<Integer, Boolean> pagerankTwitch;
        Graph<Integer, Boolean> pagerankTwitch2;
        try {
//            triangleGraph1 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph2 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph3 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph4 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph5 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph6 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph7 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph8 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph9 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
//            triangleGraph10 = Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
            pagerankGraph1 = Graph.computeSimpleGraph(roadNWCA, "\t", maxRoadNWCA, addOppositeRoadNWCA);
            pagerankGraph2 = Graph.computeSimpleGraph(roadNWCA, "\t", maxRoadNWCA, addOppositeRoadNWCA);

//            pagerankTwitch = Graph.computeSimpleGraph(twitch, ",", maxTwitch, addOppositeTwitch);
//            pagerankTwitch2 = Graph.computeSimpleGraph(twitch, ",", maxTwitch, addOppositeTwitch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(TriangleCountAlternative.error(pagerankGraph1.sampleEdgesBidirectional(0.01), pagerankGraph2));


        //System.out.println(PageRank.error(pagerankTwitch, 0, 0, pagerankTwitch2, 3, 20));
//        System.out.println(PageRank.error(pagerankTwitch, 50, pagerankTwitch2, 2, 30));

//        System.out.println("0.50: " + TriangleCount.error(triangleGraph1.sampleEdgesBidirectional(0.5), triangleGraph2));
//        System.out.println("0.45: " + TriangleCount.error(triangleGraph3.sampleEdgesBidirectional(0.45), triangleGraph2));
//        System.out.println("0.40: " + TriangleCount.error(triangleGraph4.sampleEdgesBidirectional(0.4), triangleGraph2));
//        System.out.println("0.35: " + TriangleCount.error(triangleGraph5.sampleEdgesBidirectional(0.35), triangleGraph2));
//        System.out.println("0.30: " + TriangleCount.error(triangleGraph6.sampleEdgesBidirectional(0.3), triangleGraph2));
//        System.out.println("0.25: " + TriangleCount.error(triangleGraph7.sampleEdgesBidirectional(0.25), triangleGraph2));
//        System.out.println("0.20: " + TriangleCount.error(triangleGraph8.sampleEdgesBidirectional(0.2), triangleGraph2));
//        System.out.println("0.15: " + TriangleCount.error(triangleGraph9.sampleEdgesBidirectional(0.15), triangleGraph2));
//        System.out.println("0.10: " + TriangleCount.error(triangleGraph10.sampleEdgesBidirectional(0.1), triangleGraph2));


        //System.out.println("Triangles: " + TriangleCount.countApply(triangleGraph1));
        //System.out.println("Triangles: " + TriangleCount.countApply(triangleGraph2));
//        triangleGraph2.sampleEdgesBidirectional(0.1);
//        System.out.println(PageRank.error(triangleGraph1, triangleGraph2, 10));


//        List<Pair<Double, Integer>> res2 = PageRank.apply(pagerankGraph2, 10, 1).toNodeStream().map(Node::getValue).toList();


//        pageRankFinal.toNodeStream().map(x -> x.getValue().second() + ":  " + x.getValue().first()).forEach(System.out::println);

//        List<Pair<Double, Integer>> res1 = PageRank.apply(pagerankGraph1, 10, 0).toNodeStream().map(Node::getValue).toList();
//
//        double max = 0.0;
//        double min = Double.POSITIVE_INFINITY;
//        double sum = 0.0;
//
//        Double highestRaw = 0.0;
//        Double lowestRaw = Double.POSITIVE_INFINITY;
//        Double highestApproximated = 0.0;
//        Double lowestApproximated = Double.POSITIVE_INFINITY;
//        Integer rawMax = 0;
//        Integer apprMax = 0;
//        Integer rawMin = 0;
//        Integer apprMin = 0;
//
//        for (int i = 0; i < res1.size(); i++) {
//            double dif = Math.abs(res1.get(i).first() - res2.get(i).first());
//            if (dif > max) max = dif;
//            if (dif < min) min = dif;
//            if (res1.get(i).first() > highestRaw) {
//                highestRaw = res1.get(i).first();
//                rawMax = res1.get(i).second();
//            }
//            if (res1.get(i).first() < lowestRaw) {
//                lowestRaw = res1.get(i).first();
//                rawMin = res1.get(i).second();
//            }
//            if (res2.get(i).first() > highestApproximated) {
//                highestApproximated = res2.get(i).first();
//                apprMax = res2.get(i).second();
//            }
//            if (res2.get(i).first() < lowestApproximated) {
//                lowestApproximated = res2.get(i).first();
//                apprMin = res2.get(i).second();
//            }
//
//            sum += dif;
//        }
//
//        System.out.println("Max: " + max + "\nMin: " + min + "Avg: " + (sum / res2.size()) + "\n\nRaw:\n Highest(" + rawMax + "): " + highestRaw + "\tLowest(" + rawMin + "): " + lowestRaw + "\nApproximated:\n Highest(" + apprMax + "): " + highestApproximated + "\tLowest(" + apprMin + "): " + lowestApproximated);
//
//        List<Pair<Double, Integer>> temp1 = res1.stream().sorted(Comparator.comparingDouble(Pair::first)).toList();
//        List<Pair<Double, Integer>> temp2 = res2.stream().sorted(Comparator.comparingDouble(Pair::first)).toList();
//        HashMap<Integer, Pair<Double, Integer>> posMap = new HashMap<>(temp1.size());
//
//        for (Pair<Double, Integer> doubleIntegerPair : temp1) {
//            posMap.put(doubleIntegerPair.second(), doubleIntegerPair);
//        }
//
//        int countBelowThreashold = 0;
//        for (Pair<Double, Integer> doubleIntegerPair : temp2) {
//            Double a = posMap.get(doubleIntegerPair.second()).first();
//            double dif = a - doubleIntegerPair.first();
//            if (Math.abs(dif) < 0.0001) {
//                countBelowThreashold++;
//            }
//        }
//
//        System.out.println("Correct value: " + countBelowThreashold + "\tIncorrect: " + (temp2.size() - countBelowThreashold));


    }
}