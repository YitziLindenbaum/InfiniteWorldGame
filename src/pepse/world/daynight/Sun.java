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

    public static GameObject create(
            GameObjectCollection gameObjects, int layer,
            Vector2 windowDimensions, float cycleLength){
        GameObject sun = new GameObject(
                Vector2.ZERO, new Vector2(100, 100),
                new OvalRenderable(Color.YELLOW));
        sun.setCenter(new Vector2(50, windowDimensions.y() + 50));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag("sun");

        new Transition<Float>(sun,angle -> sun.setCenter(new Vector2(
                windowDimensions.x()/2 - (float) Math.cos(angle) * (windowDimensions.x() - 100)/2,
                 windowDimensions.y()/2 - (float) Math.sin(angle) * (windowDimensions.y() - 100)/2)),
                START_ANGEL, FINAL_ANGEL, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
