package pepse.world;


import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky in the game.
 */
public class Sky{
    public static final String SKY = "sky";
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * create sky
     * @param gameObjects - game objects
     * @param windowDimensions - window dimensions
     * @param skyLayer - sky layer
     * @return sky
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY);
        return sky;
    }

}
