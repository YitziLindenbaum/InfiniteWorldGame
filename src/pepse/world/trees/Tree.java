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
    private static final float SIZE_LEAF = Block.SIZE * 0.9f;
    public static final Color LEAF_COLOR = new Color(50, 200, 30);
    public static final int NUM_LEAVES_IN_ROW = 9;
    public static final int NUM_LEAVES_IN_COL = 9;
    public static final int HEIGHT_TREE_FROM_TERRAIN = Block.SIZE * 8;
    public static final Color TREE_COLOR = new Color(100, 50, 20);
    private GameObjectCollection gameObjects;
    private final Function<Float, Float> groundHeightAt;
    Random rand;

    public Tree(GameObjectCollection gameObjects, Function<Float, Float> groundHeightAt){
        this.gameObjects = gameObjects;
        this.groundHeightAt = groundHeightAt;
    }

    public void setSeed(int seed){
        rand = new Random(seed);
    }
    /**
     * create leaves
     * @param startX -
     * @param startY -
     */
    private void createLeaves(float startX, float startY , int numRows, int numCols, int treeLayer){
        for(float i = 0, x = startX; i < numRows; i++, x +=  SIZE_LEAF){
            for (float j = 0, y = startY; j < numCols; j++, y += SIZE_LEAF){
                //create new game object
                GameObject leaf = new GameObject(new Vector2(x,y), Vector2.ONES.mult(SIZE_LEAF),
                        new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR,50)));
                gameObjects.addGameObject(leaf, treeLayer);

                //wait j/10 time as we were required in the exercise
                new ScheduledTask(leaf, j / 10, false, () -> {

                    //swaying leaves
                    new Transition<Float>(leaf, leaf.renderer()::setRenderableAngle,
                            //choose random degree in range [-30 - 30]
                            -30 * rand.nextFloat(), 30 * rand.nextFloat(),
                            Transition.LINEAR_INTERPOLATOR_FLOAT, 5,
                            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

                    //Changing leaves width
                    new Transition<Vector2>(leaf, leaf::setDimensions,
                            //Change leaves width to be at a minimum 0.8 * SIZE_LEAF
                            Vector2.ONES.mult(SIZE_LEAF),new Vector2(SIZE_LEAF * 0.8f, SIZE_LEAF),
                            Transition.LINEAR_INTERPOLATOR_VECTOR, 5,
                            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
                });
            }
        }
    }

    /**
     * create trees in range
     * @param minX -
     * @param maxX -
     */
    public void createInRange(int minX, int maxX){
        //normalize X to be integer number that is divided by Block.SIZE
        int normalizeMinX = (minX/Block.SIZE) * Block.SIZE - Block.SIZE;
        int normalizeMaxX = (maxX/Block.SIZE) * Block.SIZE + Block.SIZE;

        for (int x = normalizeMinX; x <= normalizeMaxX; x += Block.SIZE){
            //Creates a tree at a probability of 0.1 as requested
            if(rand.nextInt(10) == 0){
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
        int layer = Layer.STATIC_OBJECTS + rand.nextInt(200);
        // randomly (coin-flip) choose that tree blocks avatar
        if(rand.nextBoolean()){
            tree.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            tree.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            layer = Layer.STATIC_OBJECTS;
        }
        gameObjects.addGameObject(tree, layer);

        tree.setTag("tree");

        //create leaves
        int rows = 2 + rand.nextInt(3);
        int cols = 2 + rand.nextInt(3);
        createLeaves(x - rows * SIZE_LEAF,  y - cols * SIZE_LEAF,
                rows *2 +1, cols * 2 +1, layer);
    }
}
