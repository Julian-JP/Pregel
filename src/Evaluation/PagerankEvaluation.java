package Evaluation;

import Algorithms.PageRank;
import Measurements.Log;
import Pregel.Graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PagerankEvaluation {
    public static void eval() throws IOException {
        System.out.println("AstroPh:");
        evaluateIntern(new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt"), 18772, "\t", false);
        System.out.println("Facebook:");
        evaluateIntern(new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt"), 4039, " ", true);
    }

    private static void evaluateIntern(File file, int max, String separator, boolean addOpposite) throws IOException {
        List<Double> error1 = new ArrayList<>();
        List<Long> msg1 = new ArrayList<>();
        List<Double> error2 = new ArrayList<>();
        List<Long> msg2 = new ArrayList<>();

        Graph<Integer, Boolean> triangleGraph1 = Graph.computeSimpleGraph(file, separator, max, addOpposite);

        for (int i = 1; i < 20; i++) {
            Graph<Integer, Boolean> triangleGraph2 = Graph.computeSimpleGraph(file, separator, max, addOpposite);
            Graph<Integer, Boolean> triangleGraph3 = Graph.computeSimpleGraph(file, separator, max, addOpposite);

            Log l1 = PageRank.error(triangleGraph1 , 20, triangleGraph2.sampleEdgesBidirectional(i*0.05), 0, 10);
            Log l2 = PageRank.error(triangleGraph1 , 20, triangleGraph3, 20-i, 10);

            error1.add(l1.error);
            msg1.add(l1.messages2);
            error2.add(l2.error);
            msg2.add(l2.messages2);
        }

        System.out.println("Edgesampling:");
        for (int i = 0; i < error1.size(); i++) {
            System.out.println("(" + msg1.get(i) + "," + error1.get(i) + ")");
        }
        System.out.println(System.lineSeparator() + "Messagesampling:");
        for (int i = 0; i < error2.size(); i++) {
            System.out.println("(" + msg2.get(i) + "," + error2.get(i) + ")");
        }
    }
}
