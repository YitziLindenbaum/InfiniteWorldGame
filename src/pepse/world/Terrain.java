package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.Noise;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;


public class Terrain{
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    public static final String GROUND_TAG = "ground";
    public static final String GROUND_FIRST_LAYER_TAG = "ground0";

    public static int GROUND_LAYER; //it's public to use in the game manager
    private static final float GROUND_HEIGHT = 2f / 3f;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final int seed;
    private final Function<Float, Float> noise;
    private final Random rand;


    /**
     * constructor
     * @param gameObjects - game objects
     * @param groundLayer - ground layer
     * @param windowDimensions -window dimensions
     * @param seed - seed to the random
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer,
                   Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        GROUND_LAYER = groundLayer + 1; //set the ground layer of the block that not in the first layer
        float groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT;
        this.seed = seed;
        rand = new Random(seed);
        //set the lambda noise
        this.noise = Noise.generateNoiseFunc(seed, groundHeightAtX0, windowDimensions.x());
    }

    /**
     * ground height at x
     * @param x coordinate
     * @return height at x - such that fit to the noise function
     */
    public float groundHeightAt(float x) {
        return noise.apply(x);
    }

    /**
     * create blocks in range
     * @param minX - the minimal x to start to create the ground
     * @param maxX - the maximal x to end the ground
     */
    public void createInRange(int minX, int maxX) {
        rand.setSeed(seed);
        int normalizeMinX = (minX/Block.SIZE) * Block.SIZE - Block.SIZE;
        int normalizeMaxX = (maxX/Block.SIZE) * Block.SIZE + Block.SIZE;
        //create
        for (float x = normalizeMinX, i = 0; x <= normalizeMaxX; x += Block.SIZE, i++) {
            float height = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            for (float y = height, j = 0; j < TERRAIN_DEPTH; y += Block.SIZE, j++) {
                Block block = new Block(Vector2.of(x, y),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                if(j == 0){
                    gameObjects.addGameObject(block, groundLayer);
                    block.setTag(GROUND_FIRST_LAYER_TAG);
                }
                else {
                    gameObjects.addGameObject(block, GROUND_LAYER);
                    block.setTag(GROUND_TAG);
                }
            }
        }

    }

}
