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

/**
 * Represents the "sidekick" avatar.
 */
public class MockAvatar extends Avatar{
    private static final String PATRICK_STAND_PATH = "assets/patrickStand.png";
    private static final String PATRICK_WALK_RIGHT_IMG_1_PATH = "assets/patrickWalk/img1.png";
    private static final String PATRICK_WALK_RIGHT_IMG_2_PATH = "assets/patrickWalk/img2.png";
    private static final String PATRICK_WALK_RIGHT_IMG_3_PATH = "assets/patrickWalk/img3.png";
    private static final String PATRICK_WALK_RIGHT_IMG_4_PATH = "assets/patrickWalk/img4.png";
    private static final String PATRICK_WALK_RIGHT_IMG_5_PATH = "assets/patrickWalk/img5.png";
    private static final float TIME_BETWEEN_CLIPS = 0.5F;
    public static final int SIZE_MOCK_AVATAR = 85;
    private static final float LENIENT_MAX_DIST = 400;
    private static final float STRICT_MAX_DIST = 1000;
    private final Avatar parentAvatar;

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
        this.parentAvatar = avatar;
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


    /**
     * Override method that handles the mock avatar, so that the mock avatar cannot create another mock
     * avatar.
     */
    @Override
    protected void handleMockAvatar() {}

    /**
     * Override method that increases energy so that mock avatar doesn't increase energy.
     */
    @Override
    protected void increaseEnergy() {}

    /**
     * Override method that increases energy so that mock avatar doesn't decrease energy.
     */
    @Override
    protected void decreaseEnergy() {}

    /**
     * Prevents sidekick from getting too far from main avatar.
     * @param deltaTime - time passed since last call to update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // sidekick is too far to the right
        if (getCenter().x() - parentAvatar.getCenter().x() > LENIENT_MAX_DIST) {
            transform().setVelocityX(Math.min(0, getVelocity().x()));
        }
        // sidekick is too far to the left
        else if (parentAvatar.getCenter().x() - getCenter().x() > LENIENT_MAX_DIST) {
            transform().setVelocityX(Math.max(0, getVelocity().x()));
        }

        // Main avatar has actively tried to distance itself too much from sidekick
        if (Math.abs(getCenter().x() - parentAvatar.getCenter().x()) > STRICT_MAX_DIST) {
            parentAvatar.removeMockAvatar();
        }
    }
}
