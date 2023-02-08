package Evaluation;

import Algorithms.PageRank;
import Measurements.Log;
import Pregel.Graph;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class PagerankEvaluation {

    /**
     * Evaluates the performance of Random edge sampling on Graphs Email Enron, AstroPH, facebook, road-network-Pennsylvania
     */
    public static void evaluateRandomEdgeSampling() throws IOException {
        File enron = new File("/run/media/julian/CBE7-42BF/Seminar/Email-Enron.txt");
        File astroPh = new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt");
        File facebook = new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt");
        File roadPA = new File("/run/media/julian/CBE7-42BF/Seminar/roadNet-PA.txt");

        evaluateEdgeSamplingIntern("email-Enron", "random Edge-Sampling", enron, "\t", 36692, false,
                100, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(enron, "\t", 36692, false).sampleEdgesRandomBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("ca-AstroPh", "random Edge-Sampling", astroPh, "\t", 18772, false,
                100, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(astroPh, "\t", 18772, false).sampleEdgesRandomBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("ego-Facebook", "random Edge-Sampling", facebook, " ", 4039, true,
                100, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(facebook, " ", 4039, true).sampleEdgesRandomBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("roadNet-PA", "random Edge-Sampling", roadPA, "\t", 1088092, false,
                100, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(roadPA, "\t", 1088092, false).sampleEdgesRandomBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
    }

    /**
     * Evaluates the performance of edge sampling using the sparsifier on Graphs Email Enron, AstroPH, facebook, road-network-Pennsylvania
     */
    public static void evaluateSparsifierEdgeSampling() throws IOException {
        File enron = new File("/run/media/julian/CBE7-42BF/Seminar/Email-Enron.txt");
        File astroPh = new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt");
        File facebook = new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt");
        File roadPA = new File("/run/media/julian/CBE7-42BF/Seminar/roadNet-PA.txt");

        evaluateEdgeSamplingIntern("email-Enron", "Edge-Sampling with Sparsifier", enron, "\t", 36692, false,
                200, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(enron, "\t", 36692, false).sampleEdgesBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("ca-AstroPh", "Edge-Sampling with Sparsifier", astroPh, "\t", 18772, false,
                200, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(astroPh, "\t", 18772, false).sampleEdgesBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("ego-Facebook", "Edge-Sampling with Sparsifier", facebook, " ", 4039, true,
                200, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(facebook, " ", 4039, true).sampleEdgesBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
        evaluateEdgeSamplingIntern("roadNet-PA", "Edge-Sampling with Sparsifier", roadPA, "\t", 1088092, false,
                200, 0.01, s -> {
                    try {
                        return Graph.computeSimpleGraph(roadPA, "\t", 1088092, false).sampleEdgesBidirectional(s);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(System.lineSeparator() + System.lineSeparator());
    }

    /**
     * Evaluates the performance of message sampling using different parameter on Graphs Email Enron, AstroPH, facebook, road-network-Pennsylvania
     */
    public static void evaluateMessageSamplingDifferentParameters() throws IOException {
        File enron = new File("/run/media/julian/CBE7-42BF/Seminar/Email-Enron.txt");
        File astroPh = new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt");
        File facebook = new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt");
        File roadPA = new File("/run/media/julian/CBE7-42BF/Seminar/roadNet-PA.txt");

        int[] degreeThresholds = {2, 4, 6, 8, 10, 15};
        int[] iterationThresholds = {10, 9, 8, 7, 6, 5};
        int[] skippedMessages = {1, 2, 3, 4, 5, 10};

        evaluateInternMessageSamplingDifferentParameter("email-Enron", "Message-Sampling", enron, "\t", 36692, false,
                degreeThresholds, iterationThresholds, skippedMessages);
        evaluateInternMessageSamplingDifferentParameter("ca-AstroPh", "Message-Sampling", astroPh, "\t", 18772, false,
                degreeThresholds, iterationThresholds, skippedMessages);
        evaluateInternMessageSamplingDifferentParameter("ego-Facebook", "Message-Sampling", facebook, " ", 4039, true,
                degreeThresholds, iterationThresholds, skippedMessages);
        evaluateInternMessageSamplingDifferentParameter("roadNet-PA", "Message-Sampling", roadPA, "\t", 1088092, false,
                degreeThresholds, iterationThresholds, skippedMessages);
    }

    private static void evaluateEdgeSamplingIntern(String graphName, String methodName, File file, String separator, int maxVertices, boolean addOppositeEdge,
                                                   int steps, double stepSize, Function<Double, Graph<Integer, Boolean>> graphGenerator) throws IOException {
        long messagesNeededUnApprox = 0;

        Graph<Integer, Boolean> pageRankGraph1 = Graph.computeSimpleGraph(file, separator, maxVertices, addOppositeEdge);
        Graph<Integer, Boolean> pageRankGraph2;

        System.out.println("Messages,Error,Sparsification-Parameter,Graph,SparsificationMethod");
        for (int i = 0; i < steps; i++) {
            pageRankGraph2 = graphGenerator.apply(i * stepSize);

            Log l1 = PageRank.error(pageRankGraph1, 20, pageRankGraph2, 0, 0, 10, 10);
            messagesNeededUnApprox = l1.messages1;

            System.out.println(l1.messages2 + "," + l1.error + "," + (i * stepSize) + "," + graphName + "," + methodName);
        }
        System.out.println("Unapproximated pagerank with double the amount of supersteps took " + messagesNeededUnApprox + " messages");
    }


    private static void evaluateInternMessageSamplingDifferentParameter(String graphName, String methodName, File file, String separator, int maxVertices, boolean addOppositeEdge,
                                                                        int[] degreeThresholds, int[] iterationThresholds, int[] skippedMessages) throws IOException {
        Graph<Integer, Boolean> pageRankGraph1 = Graph.computeSimpleGraph(file, separator, maxVertices, addOppositeEdge);
        long messagesNeededUnApprox = 0;

        System.out.println("Messages,Error,DegreeThreshold,IterationThreshold,skippedMessages,Graph,SparsificationMethod");
        for (int degreeThreshold : degreeThresholds) {
            for (int iterationThreshold : iterationThresholds) {
                for (int skippedMessage : skippedMessages) {
                    Graph<Integer, Boolean> pageRankGraph2 = Graph.computeSimpleGraph(file, separator, maxVertices, addOppositeEdge);

                    Log log = PageRank.error(pageRankGraph1, 40, pageRankGraph2, skippedMessage, degreeThreshold, iterationThreshold, 10);
                    messagesNeededUnApprox = log.messages1;
                    System.out.println(log.messages2 + "," + log.error + "," + degreeThreshold + "," + iterationThreshold + "," + skippedMessage + "," + graphName + "," + methodName);
                }
            }
        }
        System.out.println("Unapproximated pagerank with four times the amount of supersteps took " + messagesNeededUnApprox + " messages");
    }

}