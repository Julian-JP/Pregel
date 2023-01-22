package Algorithms;

import Measurements.Log;
import Measurements.SpearmanCorrelation;
import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Node;
import Pregel.Pregel;
import Tools.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class PageRank {

    public static <NV, EV> Graph<Pair<Double, NV>, EV> apply(Graph<NV, EV> graph, int maxSuperSteps) {
        return apply(graph, maxSuperSteps, 0);
    }
    public static <NV, EV> Graph<Pair<Double, NV>, EV> apply(Graph<NV, EV> graph, int maxSuperSteps, int skippedMessages) {
        Graph<Pair<Double, NV>, EV> pageRankGraph = graph.mapTo(x -> new Pair<>((1.0 / graph.countNodes()), x), x -> x);
        long msg = internalApply(pageRankGraph, maxSuperSteps, skippedMessages, x ->{});
        System.out.println("Pagerank: Pagerank finished with " + msg + " messages");
        return pageRankGraph;
    }

    public static <NV, EV> Log error(Graph<NV, EV> camparisonGraph, int maxSuperSteps1, Graph<NV, EV> graph2, int skippedMessages2, int maxSuperSteps2) {
        Graph<Pair<Double, NV>, EV> graph1Mapped = camparisonGraph.mapTo(x -> new Pair<>((1.0 / graph2.countNodes()), x), x -> x);
        Graph<Pair<Double, NV>, EV> graph2Mapped = graph2.mapTo(x -> new Pair<>((1.0 / graph2.countNodes()), x), x -> x);
        List<String> infos = new ArrayList<>();
        long msg1 = internalApply(graph1Mapped, maxSuperSteps1, 0, x -> {});
        long msg2;

        final List<NV> testRanking = graph1Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList();

        Consumer<Stream<Node<Pair<Double, NV>>>> analysis = (s) -> {
            if (s== null) {
                System.out.println("Pagerank: NULL-Value");
                return;
            }

            List<Node<Pair<Double, NV>>> temp = s.toList();
            Double sum = temp.stream().map(x -> x.getValue().first()).reduce(0.0, Double::sum);
            Pair<Double, NV> max = temp.stream().map(x -> x.getValue()).reduce(new Pair<>(Double.NEGATIVE_INFINITY, null), (x,y) -> {
                if (x.first() < y.first()) return y;
                else return x;
            });
            Pair<Double, NV> min = temp.stream().map(x -> x.getValue()).reduce(new Pair<>(Double.POSITIVE_INFINITY, null), (x,y) -> {
                if (x.first() < y.first()) return x;
                else return y;
            });
            List<NV> ranking = graph2Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList();
            double spearman = SpearmanCorrelation.compare(testRanking, ranking);

            infos.add("Pagerank: Max: " + max.first() + "at Node: " + max.second() + "\tMin: " + min.first() + "at Node: " + min.second() + "\tAvg: " + (sum / temp.size()) + "\tSpearmanCor dif to first Graph: " + spearman);
        };

        msg2 = internalApply(graph2Mapped, maxSuperSteps2, skippedMessages2, analysis);

        double err =  SpearmanCorrelation.compare(graph1Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList(),
                graph2Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList());

        return new Log(err, msg1, msg2, infos.stream().reduce("Infos: ", (x,y) -> x + System.lineSeparator() + y));
    }

    private static <NV, EV> long internalApply(Graph<Pair<Double, NV>, EV> graph, int maxSuperSteps, int skippedMessages, Consumer<Stream<Node<Pair<Double, NV>>>> analysis) {

        AtomicLong messageCount = new AtomicLong(0);

        BiFunction<Pair<Double, NV>, List<Double>, Pair<Double, NV>> vertexFunction = (vertexVal, message) -> {
            Double restTemp = 0.0;
            //if (message.size() < skippedMessages+1) {
            //    for (int i = 0; i < message.size(); ++i) {
            //        messageCount.incrementAndGet();
            //        restTemp += message.get(i);
            //    }
            //    //vertexVal.setFirst(0.15 + 0.85 * restTemp * (skippedMessages + 1));
            //    vertexVal.setFirst(restTemp);
            //    return vertexVal;
            //}
            for (int i = 0; i < message.size(); i+= skippedMessages+1) {
                messageCount.incrementAndGet();
                restTemp += message.get(i);
            }
            //vertexVal.setFirst(0.15 + 0.85 * restTemp * (skippedMessages + 1));
            vertexVal.setFirst(restTemp * (skippedMessages + 1));
            return vertexVal;
        };

        Function<EdgeTriplet<Pair<Double, NV>, EV>, Double> sendMsg = (triplet) -> triplet.srcAttr().first() / triplet.srcNodeDegree();

        Pregel.apply(graph, maxSuperSteps, vertexFunction, sendMsg, analysis);

        return messageCount.get();
    }
}
