package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class Terrain {
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private float groundHeightAtX0;
    private int seed;

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * (2f/3f);
        this.seed = seed;
    }
    public float groundHeightAt(float x){
        return 0;
    }
    public void createInRange(int minX, int maxX){
        RectangleRenderable runnable =
                 new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

    }
}
