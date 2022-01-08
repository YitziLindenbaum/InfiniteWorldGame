package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    private static final int SUN_HALO_SIZE = 300;
    public static final String SUN_HALO_TAG = "sunHalo";

    /**
     * create sun halo
     * @param gameObjects - game objects
     * @param layer - sun halo layer
     * @param sun - game object
     * @param color - sun halo color
     * @return sun halo
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    GameObject sun, Color color){
        GameObject sunHalo = new GameObject(
                Vector2.ZERO, new Vector2(SUN_HALO_SIZE, SUN_HALO_SIZE),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUN_HALO_TAG);
        //set sun halo following the sun
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        return sunHalo;

    }

}
