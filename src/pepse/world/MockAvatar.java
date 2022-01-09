package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;
import pepse.util.EnergyCounter;

import java.awt.event.KeyEvent;

/**
 * Represents the "sidekick" avatar.
 */
public class MockAvatar extends Avatar{
    /**
     * Create a new MockAvatar object.
     * @param pos           - top left corner of avatar
     * @param inputListener - input from user
     * @param imageReader   - image reader
     * @param gameObject    - GameObjectCollection to which to add.
     */
    public MockAvatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader,
                      GameObjectCollection gameObject, Avatar avatar) {
        super(pos, inputListener, imageReader, gameObject);
        gameObject.removeGameObject(this.energyCounterNumeric, Layer.FOREGROUND);
        this.energyCounterNumeric = avatar.energyCounterNumeric;
        //this.gameObjects = gameObject;
    }


    /**
     * Override method that handles the mock avatar, so that the mock avatar cannot create another mock
     * avatar.
     */
    @Override
    protected void handleMockAvatar() {}
}
