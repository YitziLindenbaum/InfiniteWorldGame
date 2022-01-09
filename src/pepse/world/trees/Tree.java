package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Handles creation of trees in world.
 */
public class Tree {

    private static final int HEIGHT_TREE_FROM_TERRAIN = Block.SIZE * 8;
    private static final Color TREE_COLOR = new Color(100, 50, 20);
    public static final String TREE_TAG = "tree";
    private static final int CHANGE_TO_CREATE = 10;
    private static final int DESIRED_RESULT = 0;
    private static final int BOUND_TREE_HEIGHT = 7;
    private static final int TREE_COLOR_DELTA = 20;
    private static final int MIN_ROWS_LEAVES = 5;
    private static final int MIN_COLS_LEAVES = 5;
    private static final int DOUBLE_RES = 2;
    private static final int LEAF_ROWS_BOUND = 3;
    public static final int LEAF_COLS_BOUND = 3;
    private final GameObjectCollection gameObjects;
    private final Function<Float, Float> groundHeightAt;
    private final int treeLayer;
    private final int leavesLayer;
    private final int seed;
    private Random rand = new Random();


    /**
     * Create a new Tree object
     * @param gameObjects GameObjectCollection to which to add trees
     * @param groundHeightAt callback to know where ground is at given x coordinate
     * @param treeLayer Layer in which to place trees
     * @param leavesLayer Layer in which to place leaves
     * @param seed for Randomizer
     */
    public Tree(GameObjectCollection gameObjects, Function<Float, Float> groundHeightAt,
                int treeLayer, int leavesLayer, int seed){
        this.gameObjects = gameObjects;
        this.groundHeightAt = groundHeightAt;
        this.treeLayer = treeLayer;
        this.leavesLayer = leavesLayer;
        this.seed = seed;
    }



    /**
     * create trees in range
     * @param minX - for start to create trees
     * @param maxX - for end to create trees
     */
    public void createInRange(int minX, int maxX){
        //normalize X to be integer number that is divided by Block.SIZE
        int normalizeMinX = (minX/Block.SIZE) * Block.SIZE - Block.SIZE;
        int normalizeMaxX = (maxX/Block.SIZE) * Block.SIZE + Block.SIZE;

        for (int x = normalizeMinX; x <= normalizeMaxX; x += Block.SIZE){
            // Reinitialize the random generator using x, so that if the tree is ever
            // removed and recreated the results will be the same
            rand.setSeed(x + seed);

            // Create a tree with probability of 0.1 as requested
            if((rand.nextInt(CHANGE_TO_CREATE))  == DESIRED_RESULT){
                // get groundHeightAt(x), normalize to number that is divisible by Block.SIZE,
                // and add the desired extra height to the tree.
                int extraHeight = rand.nextInt(BOUND_TREE_HEIGHT) * Block.SIZE;
                float y = (float) Math.floor(groundHeightAt.apply((float) x) / Block.SIZE) * Block.SIZE -
                        HEIGHT_TREE_FROM_TERRAIN - extraHeight;

                createTree(x, y, extraHeight);
            }
        }

    }

    /**
     * Create a single tree
     * @param x top left corner x
     * @param y top left corner y
     * @param extraHeight - height
     */
    private void createTree(int x, float y, int extraHeight) {
        GameObject tree = new GameObject(Vector2.of(x, y),
                Vector2.of(Block.SIZE, HEIGHT_TREE_FROM_TERRAIN + extraHeight),
                new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR, TREE_COLOR_DELTA)));
        // randomly (coin-flip) choose that tree blocks avatar
        if (rand.nextBoolean()){
            tree.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            tree.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        }
        gameObjects.addGameObject(tree, treeLayer);
        tree.setTag(TREE_TAG);

        int rows = MIN_ROWS_LEAVES + DOUBLE_RES * rand.nextInt(LEAF_ROWS_BOUND);
        int cols = MIN_COLS_LEAVES + DOUBLE_RES * rand.nextInt(LEAF_COLS_BOUND);
        //set the first leaf top left corner
        int startX = x - (int)((rows - 1) / DOUBLE_RES  * Leaf.LEAF_SIZE);
        int startY = (int)(y - ((cols - 1) / DOUBLE_RES  * Leaf.LEAF_SIZE));
        createLeaves(startX, startY, rows, cols);
    }

    /**
     * Creates leaves at the given coordinates of the given size.
     * @param startX x coordinate of top left corner of block of leaves
     * @param startY y coordinate of the same
     * @param rows Number of rows of leaves
     * @param cols Number of columns of leaves
     */
    private void createLeaves(int startX, int startY, int rows, int cols){
        for(float row = 0, x = startX; row < rows; row++, x += Leaf.LEAF_SIZE){
            for (float col = 0, y = startY; col < cols; col++, y += Leaf.LEAF_SIZE){
                //create new game object
                new Leaf(Vector2.of(x, y), gameObjects, leavesLayer, rand);
            }
        }
    }
}
