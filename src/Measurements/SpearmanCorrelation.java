package Measurements;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < ranking2.size(); i++) {
            BigDecimal prod = BigDecimal.valueOf((i - mapping.get(ranking2.get(i)))).pow(2);
            sum = sum.add(prod);
        }


        if (sum.doubleValue() == 0.0) return 0.0;

        //Working in log space since the values are too big
        BigDecimal n = BigDecimal.valueOf(ranking1.size());
        BigDecimal denom = n.multiply(n.pow(2).subtract(BigDecimal.valueOf(1)));

        BigDecimal fraction = (BigDecimal.valueOf(6).multiply(sum)).divide(denom, 8, RoundingMode.HALF_UP);

        if (fraction.compareTo(BigDecimal.valueOf(1)) > 0) {
            return BigDecimal.valueOf(1).subtract(fraction).abs().doubleValue();
        }
        return fraction.doubleValue();
    }
}
