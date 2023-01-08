import Algorithms.TriangleCount;
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

        Graph<List<Integer[]>, Double> triangleGraph;
        try {
            triangleGraph = new Graph<List<Integer[]>, Double>(roadNWCA, "\t", maxRoadNWCA, new ArrayList<>(), 1.0, addOppositeRoadNWCA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(TriangleCount.count(triangleGraph));
    }
}