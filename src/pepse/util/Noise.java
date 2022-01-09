package pepse.util;

import java.util.Random;
import java.util.function.Function;

public class Noise {
    //noise method constants
    private static final float NORMALIZE_PARAMETER = 1.1f;
    private static final float RANGE_NOISE = 10f;
    private static final float[] P_NOISE = {-1.5f, -1.4f, -1.3f, -1.2f, -1.2f, -1.1f, -1.0f, -0.9f, -0.8f, -0.7f, -0.6f,
            1.5f, 1.4f, 1.3f, 1.2f, 1.2f, 1.1f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f};
    private static final float[] P_FACTOR_TOTAL_NOISE = {-0.1f, 0.1f};
    /**
     * set noise to be sin function with random parameters that make the ground to be non-permanent
     */
    public static Function<Float, Float> generateNoiseFunc(int seed, float dimY, float dimX) {

        Random rand = new Random(seed);
        //array of parameters to scala and factor of sin function
        float[] p = P_NOISE;
        float[] pFactorTotal = P_FACTOR_TOTAL_NOISE;

        float rangeFactor = dimY / 2; //Helps normalize y values to window size
        //take the function we get in rang [0,10] and normalize x values to window size
        float normalizeRangeX = RANGE_NOISE / dimX;

        float factorE = p[rand.nextInt(p.length)];
        float scalaE = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factorPi = p[rand.nextInt(p.length)];
        float scalaPi = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factor1 = p[rand.nextInt(p.length)];
        float scala1 = p[rand.nextInt(p.length)] * normalizeRangeX;

        float totalFactor = pFactorTotal[rand.nextInt(pFactorTotal.length)] * rangeFactor;


        return (Float x) -> NORMALIZE_PARAMETER * dimY + totalFactor * (float) (factor1 * Math.sin(scala1 * x) +
                factorE * Math.sin(scalaE * Math.E * x) + factorPi * Math.sin(scalaPi * Math.PI * x));
    }
}
