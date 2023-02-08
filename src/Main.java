import Evaluation.PagerankEvaluation;
import Evaluation.TriangleCountEvaluation;

import java.io.File;
import java.io.IOException;

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

        try {
            System.out.println("Evaluation of Triangle Count");
            TriangleCountEvaluation.evaluateSparsifierEdgeSampling();
            TriangleCountEvaluation.evaluateRandomEdgeSampling();
            System.out.println("Evaluation of Page Rank");
            PagerankEvaluation.evaluateSparsifierEdgeSampling();
            PagerankEvaluation.evaluateRandomEdgeSampling();
            PagerankEvaluation.evaluateMessageSamplingDifferentParameters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}