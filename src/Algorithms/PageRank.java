package Algorithms;

import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Node;
import Pregel.Pregel;
import Tools.Pair;

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

        AtomicLong messageCount = new AtomicLong(0);

        BiFunction<Pair<Double, NV>, List<Double>, Pair<Double, NV>> vertexFunction = (vertexVal, message) -> {
            Double restTemp = 0.0;
            for (int i = 0; i < message.size(); i+= skippedMessages+1) {
                messageCount.incrementAndGet();
                restTemp += message.get(i);
            }
            //vertexVal.setFirst(0.15 + 0.85 * restTemp * (skippedMessages + 1));
            vertexVal.setFirst(restTemp * (skippedMessages + 1));
            return vertexVal;
        };
        Consumer<Stream<Node<Pair<Double, NV>>>> analysis = (s) -> {
            if (s== null) {
                System.out.println("NULL-Value");
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
            System.out.println("Max: " + max.first() + "at Node: " + max.second() + "\tMin: " + min.first() + "at Node: " + min.second() + "\tAvg: " + (sum / temp.size()));
        };

        Function<EdgeTriplet<Pair<Double, NV>, EV>, Double> sendMsg = (triplet) -> triplet.srcAttr().first() / triplet.srcNodeDegree();

        Pregel.apply(pageRankGraph, maxSuperSteps, vertexFunction, sendMsg, analysis);

        System.out.println("Pagerank finished with " + messageCount + "messages");

        return pageRankGraph;
    }

}
