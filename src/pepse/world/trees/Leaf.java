package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * Represents leaves on trees.
 */
public class Leaf extends GameObject{

    //constants
    public static final float LEAF_SIZE = Block.SIZE * 0.9f;
    public static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final float FADEOUT_TIME = 40;
    private static final float FALL_SPEED = 20;
    private static final float DEAD_TIME = 5;
    private static final float MAX_LEAF_LIFE = 420;
    private static final Float FALLING_SWAY_SPEED = 15f;
    private static final float FALLING_SWAY_CYCLE_LENGTH = 4;
    public static final String LEAF_TAG = "leaf";
    private static final int WAIT_PARAMETER = 10;
    private static final int MIN_ANGLE = -30;
    private static final int MAX_ANGLE = 30;
    private static final float MIN_TIME_SWAY = 5f;
    private static final int RANGE_TIME_SWAY = 6;
    private static final float NARROW_PARAMETER = 0.8f;
    private static final float MIN_TIME_NARROW = 5f;
    private static final int RANGE_TIME_NARROW = 6;
    private static final int LEAF_COLOR_DELTA = 50;

    //leaf variables
    private static GameObjectCollection gameObjects;
    private static int leafLayer;
    private static Random rand;
    private Transition<Float> horizontalTransition;
    private final Vector2 topLeftCorner;

    /**
     * Create a new Leaf object
     * @param topLeftCorner of the leaf
     * @param gameObjects - all objects in the game
     * @param leavesLayer - leaf layer
     * @param rand - random
     */
    public Leaf(Vector2 topLeftCorner, GameObjectCollection gameObjects,
                int leavesLayer, Random rand) {
        super(topLeftCorner, Vector2.ONES.mult(LEAF_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR, LEAF_COLOR_DELTA)));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        setTag(LEAF_TAG);
        gameObjects.addGameObject(this, leavesLayer);
        Leaf.gameObjects = gameObjects;
        Leaf.leafLayer = leavesLayer;
        Leaf.rand = rand;
        this.topLeftCorner = new Vector2(topLeftCorner.x(),topLeftCorner.y());
        //wait j/10 time as we were required in the exercise, then make leaves sway and narrow
        float waitTime = getTopLeftCorner().y() % WAIT_PARAMETER;
        new ScheduledTask(this, waitTime, false, () -> {
            sway();
            narrow();
        });

        // control leaf life-cycle
        new ScheduledTask(this, MAX_LEAF_LIFE * rand.nextFloat(), false,
                this::fall);
    }

    /**
     * Handles collision of leaf with the first layer of the ground, make the leaf stop moving.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeComponent(horizontalTransition); //remove the horizontal velocity
        transform().setVelocity(Vector2.ZERO);
    }

    /**
     * Responsible for leaf swinging (when on tree).
     */
    private void sway() {
        new Transition<Float>(this, this.renderer()::setRenderableAngle,
                //choose random degree in range [-30 - 30]
                MIN_ANGLE * rand.nextFloat(), MAX_ANGLE * rand.nextFloat(),
                Transition.LINEAR_INTERPOLATOR_FLOAT, MIN_TIME_SWAY + rand.nextInt(RANGE_TIME_SWAY),
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * Responsible for leaf narrowing (when on tree).
     */
    private void narrow() {
         new Transition<Vector2>(this, this::setDimensions,
                //Change leaves width to be at a minimum 0.8 * SIZE_LEAF
                Vector2.ONES.mult(LEAF_SIZE), Vector2.of(LEAF_SIZE * NARROW_PARAMETER, LEAF_SIZE),
                Transition.LINEAR_INTERPOLATOR_VECTOR, MIN_TIME_NARROW + rand.nextInt(RANGE_TIME_NARROW),
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }


    /**
     * Responsible for leaf falling
     */
    private void fall() {
        this.renderer().fadeOut(FADEOUT_TIME, delayedRecreateLeaf());
        this.transform().setVelocityY(FALL_SPEED); //response of the vertical velocity
        //response of horizontal velocity (while falling)
        horizontalTransition = new Transition<Float>(this, this.transform()::setVelocityX,
                FALLING_SWAY_SPEED, -FALLING_SWAY_SPEED, Transition.CUBIC_INTERPOLATOR_FLOAT,
                FALLING_SWAY_CYCLE_LENGTH, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

    }

    /**
     * Responsible for delayed recreate leaf
     * @return Runnable
     */
    private Runnable delayedRecreateLeaf() {
        // written out for readability, to avoid lambdas-within-lambdas
        return () -> new ScheduledTask(this, DEAD_TIME, false, () -> {
            new Leaf(topLeftCorner, gameObjects, leafLayer, rand);
            gameObjects.removeGameObject(this, leafLayer);
        });
    }
}
