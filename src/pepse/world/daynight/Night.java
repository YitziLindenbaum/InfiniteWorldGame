package pepse.world.daynight;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {
    private static final Float NOON_OPACITY = 0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    public static final String NIGHT_TAG = "night";

    /**
     * create Night
     * @param gameObjects - objects in game
     * @param layer - night layer
     * @param windowDimensions - size
     * @param cycleLength - to change to darker sky
     * @return game object of night
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength){

        GameObject night = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        //change background opaqueness, make the sky be darker in half cycle length.
        new Transition<Float>(night, night.renderer()::setOpaqueness,
                NOON_OPACITY, MIDNIGHT_OPACITY, Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength/2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }
}
