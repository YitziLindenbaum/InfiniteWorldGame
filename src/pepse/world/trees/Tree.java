package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

public class Tree {
    /**
     * It remains to deal with leaf fall and leaf collision in the ground
     */

    public static final int HEIGHT_TREE_FROM_TERRAIN = Block.SIZE * 8;
    public static final Color TREE_COLOR = new Color(100, 50, 20);
    private final GameObjectCollection gameObjects;
    private final Function<Float, Float> groundHeightAt;
    private int seed;
    Random rand;

    public Tree(GameObjectCollection gameObjects, Function<Float, Float> groundHeightAt, int seed){
        this.gameObjects = gameObjects;
        this.groundHeightAt = groundHeightAt;
        this.seed = seed;
        rand = new Random();
    }

    public void setSeed(int seed){
        this.seed = seed;
    }


    /**
     * create trees in range
     * @param minX -
     * @param maxX -
     */
    public void createInRange(int minX, int maxX){
        //rand.setSeed(seed);
        //normalize X to be integer number that is divided by Block.SIZE
        int normalizeMinX = (minX/Block.SIZE) * Block.SIZE - Block.SIZE;
        int normalizeMaxX = (maxX/Block.SIZE) * Block.SIZE + Block.SIZE;

        for (int x = normalizeMinX; x <= normalizeMaxX; x += Block.SIZE){
            //Creates a tree at a probability of 0.1 as requested
            rand.setSeed(x); // Reinitialize the random generator using x, so that if the tree is ever
            // removed and recreated the results will be the same
            if((rand.nextInt(10))  == 0){
                // get groundHeightAt(x), normalize to number that is divided by Block.SIZE,
                // and add the desired extra height to the tree.
                int extraHeight = rand.nextInt(7) * Block.SIZE;
                float y = (float) Math.floor(groundHeightAt.apply((float) x) / Block.SIZE) * Block.SIZE -
                        HEIGHT_TREE_FROM_TERRAIN - extraHeight;

                createTree(x, y, extraHeight);
            }
        }

    }

    private void createTree(int x, float y, int extraHeight) {
        GameObject tree = new GameObject(Vector2.of(x, y),
                Vector2.of(Block.SIZE, HEIGHT_TREE_FROM_TERRAIN + extraHeight),
                new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR,20)));
        // randomly (coin-flip) choose that tree blocks avatar
        if (rand.nextBoolean()){
            tree.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            tree.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        }
        gameObjects.addGameObject(tree, Layer.STATIC_OBJECTS);

        tree.setTag("tree");

        new Leaves(gameObjects, rand, x, y).createLeaves();
    }
}
