package dos.des.algorithm;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcPai {
    private static final Logger logger = LoggerFactory.getLogger(CalcPai.class);
    class CurrResult {
        public double pi;
        public double diff;
    }
    public static boolean inCircle(double x, double y) {
        return (y <= Math.sqrt(1 - x * x));
    }

    public static double CalcPaiByPointNumber(long num) {
        double inCircleNum = 0.0;
        for (long i = 0; i < num; i++) {
            if (CalcPai.inCircle(RandomUtils.nextDouble(), RandomUtils.nextDouble())) {
                inCircleNum++;
            }
        }
        double pai = inCircleNum * 4 / num;
        return pai;
    }
    // 50 ms
    public static void doCalcPi() {
        long now = System.currentTimeMillis();
        long num = 1000;
        for (long i = 1; i <= 10; i++) {
            double pai = CalcPai.CalcPaiByPointNumber(num);
        }
        logger.info("calc pi using {}", System.currentTimeMillis() - now);
    }
   
}