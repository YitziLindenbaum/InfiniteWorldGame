package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

/**
 * Responsible for creating/placing terrain in world.
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    public static final String GROUND_TAG = "ground";
    public static final String GROUND_FIRST_LAYER_TAG = "ground0";

    //noise method constants
    private static final float NORMALIZE_PARAMETER = 1.1f;
    private static final float RANGE_NOISE = 10f;
    private static final float[] P_NOISE = {-1.5f, -1.4f, -1.3f, -1.2f, -1.2f, -1.1f, -1.0f, -0.9f, -0.8f,
        -0.7f, -0.6f,
        1.5f, 1.4f, 1.3f, 1.2f, 1.2f, 1.1f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f};
    public static final float[] P_FACTOR_TOTAL_NOISE = {-0.1f, 0.1f};

    public static int GROUND_LAYER; //it's public to use in the game manager
    private static final float GROUND_HEIGHT = 2f / 3f;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final int seed;
    private final Vector2 windowDimensions;
    private final Function<Float, Float> noise;
    private final Random rand;


    /**
     * Create a new Terrain object.
     *
     * @param gameObjects      - game objects
     * @param groundLayer      - ground layer
     * @param windowDimensions -window dimensions
     * @param seed             - seed to the random
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer,
                   Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        GROUND_LAYER = groundLayer + 1; //set the ground layer of the block that not in the first layer
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        rand = new Random(seed);
        //set the lambda noise
        this.noise = generateNoiseFunc();
    }

    /**
     * ground height at x
     *
     * @param x coordinate
     * @return height at x - such that fit to the noise function
     */
    public float groundHeightAt(float x) {
        return noise.apply(x);
    }

    /**
     * create blocks in range
     *
     * @param minX - the minimal x to start to create the ground
     * @param maxX - the maximal x to end the ground
     */
    public void createInRange(int minX, int maxX) {
        // reinitializing seed here gives reasonable consistency to terrain features.
        rand.setSeed(seed);
        int normalizeMinX = (minX / Block.SIZE) * Block.SIZE - Block.SIZE;
        int normalizeMaxX = (maxX / Block.SIZE) * Block.SIZE + Block.SIZE;
        //create
        for (float x = normalizeMinX, i = 0; x <= normalizeMaxX; x += Block.SIZE, i++) {
            float height = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            for (float y = height, j = 0; j < TERRAIN_DEPTH; y += Block.SIZE, j++) {
                Block block = new Block(Vector2.of(x, y),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                if (j == 0) {
                    gameObjects.addGameObject(block, groundLayer);
                    block.setTag(GROUND_FIRST_LAYER_TAG);
                } else {
                    gameObjects.addGameObject(block, GROUND_LAYER);
                    block.setTag(GROUND_TAG);
                }
            }
        }

    }

    /**
     * Set noise to be sin function with random parameters that make the ground to be non-permanent.
     */
    private Function<Float, Float> generateNoiseFunc() {

        //array of parameters to scala and factor of sin function
        float[] p = P_NOISE;
        float[] pFactorTotal = P_FACTOR_TOTAL_NOISE;

        float rangeFactor = groundHeightAtX0 / 2; //Helps normalize y values to window size
        //take the function we get in rang [0,10] and normalize x values to window size
        float normalizeRangeX = RANGE_NOISE / windowDimensions.x();

        float factorE = p[rand.nextInt(p.length)];
        float scalaE = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factorPi = p[rand.nextInt(p.length)];
        float scalaPi = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factor1 = p[rand.nextInt(p.length)];
        float scala1 = p[rand.nextInt(p.length)] * normalizeRangeX;

        float totalFactor = pFactorTotal[rand.nextInt(pFactorTotal.length)] * rangeFactor;


        return (Float x) -> NORMALIZE_PARAMETER * groundHeightAtX0 + totalFactor *
            (float) (factor1 * Math.sin(scala1 * x) + factorE * Math.sin(scalaE * Math.E * x) +
                factorPi * Math.sin(scalaPi * Math.PI * x));
    }

}
