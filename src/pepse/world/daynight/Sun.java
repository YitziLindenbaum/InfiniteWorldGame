package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Sun {
    private static final Float START_ANGEL =  0f;
    private static final Float FINAL_ANGEL =  360f;
    public static final String SUN_TAG = "sun";
    private static final float SUN_SIZE = 100;

    /**
     * create sun
     * @param gameObjects - objects in game
     * @param layer - sun layer
     * @param windowDimensions -
     * @param cycleLength -
     * @return sun
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
            Vector2 windowDimensions, float cycleLength){
        GameObject sun = new GameObject(
                Vector2.ZERO, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCenter(new Vector2(SUN_SIZE/2, windowDimensions.y() + SUN_SIZE/2));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);

        //set sun center changed in (a*cos(x), b*sin(x)) path
        new Transition<Float>(sun,angle -> sun.setCenter(new Vector2(
                windowDimensions.x()/2 - (float) Math.cos(Math.toRadians(angle)) *
                        (windowDimensions.x() - SUN_SIZE)/2,
                 windowDimensions.y()/2 - (float) Math.sin(Math.toRadians(angle)) *
                         (windowDimensions.y() - SUN_SIZE)/2)),
                START_ANGEL, FINAL_ANGEL, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
