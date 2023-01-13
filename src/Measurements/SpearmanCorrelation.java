package Measurements;

import java.util.HashMap;
import java.util.List;

public class SpearmanCorrelation {
    public static <V> double compare(List<V> ranking1, List<V> ranking2) {
        if (ranking1.size() != ranking2.size()) {
            return Double.NaN;
        }

        HashMap<V, Integer> mapping = new HashMap<>(ranking1.size());

        for (int i = 0; i < ranking1.size(); i++) {
            mapping.put(ranking1.get(i), i);
        }

        long sum = 0;
        for (int i = 0; i < ranking2.size(); i++) {
            sum += (long) (i - mapping.get(ranking2.get(i))) * (i - mapping.get(ranking2.get(i)));
        }

        double n = ranking1.size();

        if (sum == 0) return 0.0;

        return Math.exp(Math.log(6) + Math.log(sum) - 2.0 * Math.log(n) - Math.log(n - 1));
    }
}
