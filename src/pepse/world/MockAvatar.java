package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;
import pepse.util.EnergyCounter;

import java.awt.event.KeyEvent;

public class MockAvatar extends Avatar{
    //private GameObjectCollection gameObjects;
    /**
     * Create a new Avatar object.
     *
     * @param pos           - top left corner of avatar
     * @param inputListener - input from user
     * @param imageReader   - image reader
     * @param gameObject    - game objects
     */
    public MockAvatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader,
                      GameObjectCollection gameObject, Avatar avatar) {
        super(pos, inputListener, imageReader, gameObject);
        gameObject.removeGameObject(this.energyCounterNumeric, Layer.FOREGROUND);
        this.energyCounterNumeric = avatar.energyCounterNumeric;
        //this.gameObjects = gameObject;
    }

    @Override
    protected void handleMockAvatar() {
    }
}
