package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.util.EnergyCounter;

import java.awt.event.KeyEvent;

public class MockAvatar extends Avatar{
    private static final String PATRICK_STAND_PATH = "assets/patrickStand.png";
    private static final String PATRICK_WALK_RIGHT_IMG_1_PATH = "assets/patrickWalk/img1.png";
    private static final String PATRICK_WALK_RIGHT_IMG_2_PATH = "assets/patrickWalk/img2.png";
    private static final String PATRICK_WALK_RIGHT_IMG_3_PATH = "assets/patrickWalk/img3.png";
    private static final String PATRICK_WALK_RIGHT_IMG_4_PATH = "assets/patrickWalk/img4.png";
    private static final String PATRICK_WALK_RIGHT_IMG_5_PATH = "assets/patrickWalk/img5.png";
    private static final float TIME_BETWEEN_CLIPS = 0.5F;
    public static final int SIZE_MOCK_AVATAR = 85;

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
        this.animation = new AnimationRenderable(new ImageRenderable[]{
                imageReader.readImage(PATRICK_WALK_RIGHT_IMG_1_PATH, true),
                imageReader.readImage(PATRICK_WALK_RIGHT_IMG_2_PATH, true),
                imageReader.readImage(PATRICK_WALK_RIGHT_IMG_3_PATH, true),
                imageReader.readImage(PATRICK_WALK_RIGHT_IMG_4_PATH, true),
                imageReader.readImage(PATRICK_WALK_RIGHT_IMG_5_PATH, true)
        }, TIME_BETWEEN_CLIPS);
        this.standAnimation = imageReader.readImage(PATRICK_STAND_PATH, true);
        this.setDimensions(Vector2.ONES.mult(SIZE_MOCK_AVATAR));

    }

    @Override
    protected void handleMockAvatar() {
    }
}
