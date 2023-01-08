import Algorithms.TriangleCount;
import Pregel.Edge;
import Pregel.Graph;
import Pregel.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        List<Node<String>> nodes = new ArrayList<>(4);
        nodes.add(new Node<>("Home", 0));
        nodes.add(new Node<>("About", 1));
        nodes.add(new Node<>("Product", 2));
        nodes.add(new Node<>("Links", 3));
        nodes.add(new Node<>("Ext Site A", 4));
        nodes.add(new Node<>("Ext Site B", 5));
        nodes.add(new Node<>("Ext Site C", 6));
        nodes.add(new Node<>("Ext Site D", 7));
        nodes.add(new Node<>("Review A", 8));
        nodes.add(new Node<>("Review B", 9));
        nodes.add(new Node<>("Review C", 10));
        nodes.add(new Node<>("Review D", 11));

        List<Edge<Integer>> edges = new ArrayList<>(5);
        edges.add(new Edge<>(1, 0, 1));
        edges.add(new Edge<>(1, 0, 2));
        edges.add(new Edge<>(1, 0, 3));

        edges.add(new Edge<>(1, 1, 0));
        edges.add(new Edge<>(1, 2, 0));
        edges.add(new Edge<>(1, 3, 0));
        edges.add(new Edge<>(1, 3, 4));
        edges.add(new Edge<>(1, 3, 5));
        edges.add(new Edge<>(1, 3, 6));
        edges.add(new Edge<>(1, 3, 7));
        edges.add(new Edge<>(1, 3, 8));
        edges.add(new Edge<>(1, 3, 9));
        edges.add(new Edge<>(1, 3, 10));
        edges.add(new Edge<>(1, 3, 11));

        edges.add(new Edge<>(1, 8, 0));
        edges.add(new Edge<>(1, 9, 0));
        edges.add(new Edge<>(1, 10, 0));
        edges.add(new Edge<>(1, 11, 0));

        Graph<String, Integer> pageRank = new Graph<String, Integer>(nodes, edges);
        //Graph<Pair<Double, String>, Integer> pageRankFinal = PageRank.apply(pageRank, 10);

        //pageRankFinal.toNodeStream().map(x -> x.getValue().second() + ":  " + x.getValue().first()).forEach(System.out::println);

        //PageRank.apply(triangleGraph, 10);

        System.out.println(TriangleCount.apply(triangleGraph));
    }
}