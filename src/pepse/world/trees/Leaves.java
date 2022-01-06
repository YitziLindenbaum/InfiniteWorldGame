package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Leaves {

    private static final float LEAF_SIZE = Block.SIZE * 0.9f;
    public static final Color LEAF_COLOR = new Color(50, 200, 30);
    public static final int NUM_LEAVES_IN_ROW = 9;
    public static final int NUM_LEAVES_IN_COL = 9;
    private static final float FADEOUT_TIME = 40;
    private static final float FALL_SPEED = 20;
    private static final float DEAD_TIME = 5;
    private static final float MAX_LEAF_LIFE = 420;
    private static final Float FALLING_SWAY_SPEED = 15f;
    private static final float FALLING_SWAY_CYCLE_LENGTH = 4;
    private final GameObjectCollection gameObjects;
    private final Random rand;
    private final int rows;
    private final int cols;
    private float startX;
    private float startY;


    public Leaves(GameObjectCollection gameObjects, Random rand, float centerX, float centerY) {

        this.gameObjects = gameObjects;
        this.rand = rand;
        rows = 5 + 2 * rand.nextInt(3);
        cols = 5 + 2 * rand.nextInt(3);
        this.startX = centerX - ((rows - 1) / 2f  * LEAF_SIZE);
        this.startY = centerY - ((cols - 1) / 2f  * LEAF_SIZE);
    }

    /**
     * create leaves
     * @param startX -
     * @param startY -
     */
    public void createLeaves(){
        for(float row = 0, x = startX; row < rows; row++, x += LEAF_SIZE){
            for (float col = 0, y = startY; col < cols; col++, y += LEAF_SIZE){
                //create new game object
                createLeaf(x, y);
            }
        }
    }

    private void createLeaf(float x, float y) {
        GameObject leaf = new GameObject(Vector2.of(x, y), Vector2.ONES.mult(LEAF_SIZE),
            new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR,50)));
        leaf.setTag("leaf");
        gameObjects.addGameObject(leaf, Layer.STATIC_OBJECTS);

        //wait j/10 time as we were required in the exercise, then make leaves sway and narrow
        float waitTime = leaf.getTopLeftCorner().y() % 10;
        new ScheduledTask(leaf, waitTime, false, () -> {
            sway(leaf);
            narrow(leaf);
        });

        // control leaf life-cycle
        new ScheduledTask(leaf, MAX_LEAF_LIFE * rand.nextFloat(), false,
            () -> fall(leaf, x, y));
    }

    private void sway(GameObject leaf) {
        new Transition<Float>(leaf, leaf.renderer()::setRenderableAngle,
            //choose random degree in range [-30 - 30]
            -30 * rand.nextFloat(), 30 * rand.nextFloat(),
            Transition.LINEAR_INTERPOLATOR_FLOAT, 5f + rand.nextInt(6),
            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void narrow(GameObject leaf) {
        new Transition<Vector2>(leaf, leaf::setDimensions,
            //Change leaves width to be at a minimum 0.8 * SIZE_LEAF
            Vector2.ONES.mult(LEAF_SIZE), Vector2.of(LEAF_SIZE * 0.8f, LEAF_SIZE),
            Transition.LINEAR_INTERPOLATOR_VECTOR, 5f + rand.nextInt(6),
            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void fall(GameObject leaf, float x, float y) {
        leaf.renderer().fadeOut(FADEOUT_TIME, delayedRecreateLeaf(leaf, x, y));
        leaf.transform().setVelocityY(FALL_SPEED);
        new Transition<Float>(leaf, leaf.transform()::setVelocityX, FALLING_SWAY_SPEED, -FALLING_SWAY_SPEED,
            Transition.CUBIC_INTERPOLATOR_FLOAT,
            FALLING_SWAY_CYCLE_LENGTH, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private Runnable delayedRecreateLeaf(GameObject leaf, float x, float y) {
        Runnable delayedCreator = new Runnable() {
            @Override
            public void run() {
                new ScheduledTask(leaf, DEAD_TIME, false, () -> createLeaf(x, y));
            }
        };
        return delayedCreator;
    }
}
