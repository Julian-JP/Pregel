package Evaluation;

import Algorithms.PageRank;
import Algorithms.TriangleCount;
import Algorithms.TriangleCountAlternative;
import Measurements.Log;
import Pregel.Graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TriangleCountEvaluation {
    public static void eval() throws IOException {

        List<Double> error = new ArrayList<>();
        List<Long> msg = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            Graph<Integer, Boolean> triangleGraph1 = readAstroGraph();
            Graph<Integer, Boolean> triangleGraph2 = readAstroGraph();

            Log l1 = TriangleCountAlternative.error(triangleGraph1 , triangleGraph2.sampleEdgesBidirectional(i*0.05));
            System.out.println("Evaluation: Edgesampling with s=" + (i*0.05) + "\t\terror: " + l1.error + "\t\t" + l1.messages1 + " vs " + l1.messages2 + " messages(" + (l1.messages2/(double) l1.messages1) + ")");
            error.add(l1.error);
            msg.add(l1.messages2);
        }

        for (int i = 0; i < error.size(); i++) {
            System.out.println("(" + msg.get(i) + "," + error.get(i) + ")");
        }
    }

    private static Graph<Integer, Boolean> readAstroGraph() throws IOException {
        File astroPh = new File("/run/media/julian/CBE7-42BF/Seminar/CA-AstroPh.txt");
        int maxAstroPh = 18772;
        boolean addOppositeAstroPh = false;
        return Graph.computeSimpleGraph(astroPh, "\t", maxAstroPh, addOppositeAstroPh);
    }

    private static Graph<Integer, Boolean> readFacebookGraph() throws IOException {
        File facebookCombined = new File("/run/media/julian/CBE7-42BF/Seminar/facebook_combined.txt");
        int maxFacebook = 4039;
        boolean addOppositeFacebook = true;
        return Graph.computeSimpleGraph(facebookCombined, " ", maxFacebook, addOppositeFacebook);
    }
}
