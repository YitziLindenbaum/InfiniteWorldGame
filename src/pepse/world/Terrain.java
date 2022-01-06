package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;


public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final Color SECOND_GROUND_COLOR = new Color(202, 113, 64);
    private static final Color THIRD_GROUND_COLOR = new Color(222, 133, 84);
    private static final Color[] GROUND_COLORS = {BASE_GROUND_COLOR, SECOND_GROUND_COLOR, THIRD_GROUND_COLOR};
    private static final int TERRAIN_DEPTH = 20;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final int seed;
    private final Vector2 windowDimensions;
    private final Function<Float, Float> noise;
    private final Random rand;


    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * (2f / 3f);
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        rand = new Random(seed);
        //set the lambda noise
        this.noise = generateNoiseFunc();
    }

    public float groundHeightAt(float x) {
        //create objects of dots that help to debug
//        gameObjects.addGameObject(new GameObject(new Vector2(x,noise.apply(x) ),
//                new Vector2(3.0f,3.0f),
//                new OvalRenderable(Color.BLACK)),Layer.FOREGROUND);

        return noise.apply(x);
    }

    public void createInRange(int minX, int maxX) {
        rand.setSeed(seed);
        //create
        for (float x = minX, i = 0; x <= maxX; x += Block.SIZE, i++) {
            float height = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            for (float y = height, j = 0; j < TERRAIN_DEPTH; y += Block.SIZE, j++) {
                Block block = new Block(Vector2.of(x, y),
                    new RectangleRenderable(ColorSupplier.approximateColor(GROUND_COLORS[(int) (i + j) % 3])));
                gameObjects.addGameObject(block, groundLayer);
                block.setTag("ground");
            }
        }

    }

    /**
     * set noise to be sin function with random parameters that make the ground to be non-permanent
     */
    private Function<Float, Float> generateNoiseFunc() {

        //array of parameters to scala and factor of sin function
        float[] p = {-1.5f, -1.4f, -1.3f, -1.2f, -1.2f, -1.1f, -1.0f, -0.9f, -0.8f, -0.7f, -0.6f,
            1.5f, 1.4f, 1.3f, 1.2f, 1.2f, 1.1f, 1.0f, 0.9f, 0.8f, 0.7f, 0.6f};
        float[] pFactorTotal = {-0.1f, 0.1f};

        float rangeFactor = groundHeightAtX0 / 2; //Helps normalize y values to window size
        //take the function we get in rang [0,10] and normalize x values to window size
        float normalizeRangeX = 10f / windowDimensions.x();

        float factorE = p[rand.nextInt(p.length)];
        float scalaE = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factorPi = p[rand.nextInt(p.length)];
        float scalaPi = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factor1 = p[rand.nextInt(p.length)];
        float scala1 = p[rand.nextInt(p.length)] * normalizeRangeX;

        float totalFactor = pFactorTotal[rand.nextInt(pFactorTotal.length)] * rangeFactor;


//        System.out.println(totalFactor/rangeFactor + "(" + factor1 + "sin(" + scala1/normalizeRangeX +
//        "x) + "+
//                factorE + "sin("+scalaE/normalizeRangeX+"e x) + " + factorPi + "sin("+
//                scalaPi/normalizeRangeX+"pi x))");


        return (Float x) -> 1.1f * groundHeightAtX0 + totalFactor * (float) (factor1 * Math.sin(scala1 * x) +
            factorE * Math.sin(scalaE * Math.E * x) + factorPi * Math.sin(scalaPi * Math.PI * x));
    }
}
