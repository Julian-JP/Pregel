package Algorithms;

import Measurements.Log;
import Measurements.SpearmanCorrelation;
import Pregel.EdgeTriplet;
import Pregel.Graph;
import Pregel.Node;
import Pregel.Pregel;
import Tools.Pair;
import Pregel.ExtendedNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class PageRank {

    /**
     * Pagerank algorithm on the given graph
     * @param graph Input graph
     * @param maxSuperSteps maximum supersteps pagerank should be applied
     * @param skippedMessages How many messages should be skipped for on not skipped.
     *                        (e.g. skippedMessages=1: We skip on the next will be interpreted and then the next will again be skipped)
     * @return Graph with every vertex consisting of its original value and his pagerank
     */
    public static <NV, EV> Graph<Pair<Double, NV>, EV> apply(Graph<NV, EV> graph, int maxSuperSteps, int skippedMessages) {
        Graph<Pair<Double, NV>, EV> pageRankGraph = graph.mapTo(x -> new Pair<>((1.0 / graph.countNodes()), x), x -> x);
        long msg = internalApply(pageRankGraph, maxSuperSteps, skippedMessages, x ->{}, 0);
        System.out.println("Pagerank: Pagerank finished with " + msg + " messages");
        return pageRankGraph;
    }

    public static <NV, EV> Log error(Graph<NV, EV> camparisonGraph, int maxSuperSteps1, Graph<NV, EV> graph2, int skippedMessages2, int degreeThreshold, int iterationThreshold, int maxSuperSteps2) {
        //Convert graph to right format
        Graph<Pair<Double, NV>, EV> graph1Mapped = camparisonGraph.mapTo(x -> new Pair<>((1.0 / graph2.countNodes()), x), x -> x);
        Graph<Pair<Double, NV>, EV> graph2Mapped = graph2.mapTo(x -> new Pair<>((1.0 / graph2.countNodes()), x), x -> x);

        //Init relevant variables for the Log (return value)
        List<String> infos = new ArrayList<>();
        long msg1;
        long msg2;

        //Apply pagerank on the reference graph and store the pagerank ranking in a List
        msg1 = internalApply(graph1Mapped, maxSuperSteps1, 0, x -> {}, 0);
        final List<NV> testRanking = graph1Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList();


        //Analysis lambda for further analysis
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

        //Apply pagerank on the test graph until we reach the iteration threshold. If we still have supersteps we need to do
        //We use the non approximated vertex function
        msg2 = internalApply(graph2Mapped, iterationThreshold, skippedMessages2, analysis, degreeThreshold);
        if (iterationThreshold < maxSuperSteps2) {
            //If we reach the iteration threshold we don't use the approximated function we just use the original by just setting the degree-threshold
            //To the maximum which mean every vertex has lower degree and hence will compute it's original function
            msg2 += internalApply(graph2Mapped, maxSuperSteps2-iterationThreshold, skippedMessages2, analysis, Integer.MAX_VALUE);
        }

        double err =  SpearmanCorrelation.compare(graph1Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList(),
                graph2Mapped.toNodeStream().sorted(Comparator.comparingDouble(x -> x.getValue().first())).map(x -> x.getValue().second()).toList());

        return new Log(err, msg1, msg2, infos.stream().reduce("Infos: ", (x,y) -> x + System.lineSeparator() + y));
    }

    /**
     * Apply pagerank on the given graph
     * @param graph Graph which has already the correct format for pagerank (Right vertexvalue types)
     * @param maxSuperSteps Maximumsupersteps pagerank should run (Result will be the object this reference is referencing to
     * @param skippedMessages Number of skipped messages per non-skipped message
     * @param analysis Analysis function to colllect more data during evaluation
     * @param degreeThreshold Smallest vertexdegree which should evaluate the approximated function (all vertecies with
     *                        degree < degreeThreshold will use the original unapproximated function
     * @return Number of messages
     */
    private static <NV, EV> long internalApply(Graph<Pair<Double, NV>, EV> graph, int maxSuperSteps, int skippedMessages, Consumer<Stream<Node<Pair<Double, NV>>>> analysis, int degreeThreshold) {

        AtomicLong messageCount = new AtomicLong(0);

        BiFunction<ExtendedNode<Pair<Double, NV>, EV>, List<Double>, Pair<Double, NV>> vertexFunction = (vertex, message) -> {
            Pair<Double, NV> vertexVal = vertex.getValue();

            Double restTemp = 0.0;
            //Use original function if the degree smaller than degreeThreshold
            if (vertex.getNeighbors().size() < degreeThreshold) {
                for (int i = 0; i < message.size(); ++i) {
                    messageCount.incrementAndGet();
                    restTemp += message.get(i);
                }
                vertexVal.setFirst(0.15 + 0.85 * restTemp);
                return vertexVal;
            }

            //Else use approximated function
            for (int i = 0; i < message.size(); i+= skippedMessages+1) {
                messageCount.incrementAndGet();
                restTemp += message.get(i);
            }
            vertexVal.setFirst(0.15 + 0.85 * restTemp * (skippedMessages + 1));
            return vertexVal;
        };

        Function<EdgeTriplet<Pair<Double, NV>, EV>, Double> sendMsg = (triplet) -> triplet.srcAttr().first() / triplet.srcNodeDegree();


        Pregel.apply(graph, maxSuperSteps, vertexFunction, sendMsg, analysis);
        return messageCount.get();
    }
}
