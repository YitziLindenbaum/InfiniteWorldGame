package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;
import pepse.util.EnergyCounter;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Represents the avatar in the game.
 */
public class Avatar extends GameObject{
    protected static final float VELOCITY_X = 300;
    protected static final float VELOCITY_Y = -300;
    protected static final float GRAVITY = 500;
    public static final int AVATAR_SIZE = 65;

    private static final float TIME_BETWEEN_CLIPS = 0.5F;
    private static final String SPONGEBOB_STAND_PATH = "assets/spongebobStand.png";
    private static final String SPONGEBOB_WALK_RIGHT_IMG_1_PATH = "assets/spongebobWalk/img1.png";
    private static final String SPONGEBOB_WALK_RIGHT_IMG_2_PATH = "assets/spongebobWalk/img2.png";
    private static final String SPONGEBOB_WALK_RIGHT_IMG_3_PATH = "assets/spongebobWalk/img3.png";
    private static final String SPONGEBOB_WALK_RIGHT_IMG_4_PATH = "assets/spongebobWalk/img4.png";
    private static final String SPONGEBOB_WALK_RIGHT_IMG_5_PATH = "assets/spongebobWalk/img5.png";
    protected static final float FACTOR_VELOCITY_DOWN = 0.7f;
    public static final String AVATAR_TAG = "avatar";

    private static final int AVATAR_ANGLE_FLY = -45;
    protected static final int ZERO_VEL = 0;
    protected static final int ZERO_ANGLE = 0;

    protected final UserInputListener inputListener;
    protected EnergyCounter energyCounterNumeric;
    protected GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final AnimationRenderable animation;
    private boolean isMockAvatarInGame = false;
    private MockAvatar mockAvatar;

    /**
     * Create a new Avatar object.
     * @param pos - top left corner of avatar
     * @param inputListener - input from user
     * @param imageReader - image reader
     * @param gameObject - game objects
     */
    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader,
                  GameObjectCollection gameObject) {
        super(pos, Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage(SPONGEBOB_STAND_PATH, true));
        this.imageReader = imageReader;
        //set gravity
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.gameObjects = gameObject;
        //create energy counter
        energyCounterNumeric = EnergyCounter.create(gameObject);
        //set animation of the avatar
        this.animation = new AnimationRenderable(new ImageRenderable[]{
                this.imageReader.readImage(SPONGEBOB_WALK_RIGHT_IMG_1_PATH, true),
                this.imageReader.readImage(SPONGEBOB_WALK_RIGHT_IMG_2_PATH, true),
                this.imageReader.readImage(SPONGEBOB_WALK_RIGHT_IMG_3_PATH, true),
                this.imageReader.readImage(SPONGEBOB_WALK_RIGHT_IMG_4_PATH, true),
                this.imageReader.readImage(SPONGEBOB_WALK_RIGHT_IMG_5_PATH, true)
        }, TIME_BETWEEN_CLIPS);
        renderer().setRenderable(animation);
    }


    /**
     * responsible for the render of the avatar
     * @param g -
     * @param camera -
     */
    @Override
    public void render(Graphics2D g, Camera camera) {
        super.render(g, camera);


        // right & left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            renderer().setRenderable(animation);
            renderer().setIsFlippedHorizontally(true);
        }
        else if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            renderer().setRenderable(animation);
            renderer().setIsFlippedHorizontally(false);
        }

        // jumping and flying
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
            energyCounterNumeric.getEnergy() > EnergyCounter.MIN_ENERGY_VALUE) {
            renderer().setRenderableAngle(AVATAR_ANGLE_FLY);
            return;
        }

        if (getVelocity().y() != ZERO_VEL){
            renderer().setRenderableAngle(ZERO_ANGLE);
        }

        if (transform().getVelocity().x() == ZERO_VEL && transform().getVelocity().y() == ZERO_VEL){
            renderer().setRenderableAngle(ZERO_ANGLE);
            renderer().setRenderable(imageReader.readImage(SPONGEBOB_STAND_PATH, true));
        }
    }

    /**
     * update avatar mood by gets input from users
     * @param deltaTime -
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = ZERO_VEL;
        //move left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
        }

        //move right
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
        }
        transform().setVelocityX(xVel);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);

        //fly
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energyCounterNumeric.getEnergy() > EnergyCounter.MIN_ENERGY_VALUE) {
            //set energy counter - I remove this code to debug
            decreaseEnergy();
            transform().setVelocityY(VELOCITY_Y);
            this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            return;
        }

        //jump and fly
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
            transform().setVelocityY(VELOCITY_Y);
            return;
        }
        if (getVelocity().y() != ZERO_VEL){
            setVelocity(getVelocity().multY(FACTOR_VELOCITY_DOWN));
        }

        if (transform().getVelocity().y() == ZERO_VEL){
            increaseEnergy();
        }

        handleMockAvatar();

    }

    /**
     * Increases energy by 0.5. (Note: these methods are extracted to allow override by MockAvatar.)
     */
    protected void increaseEnergy() {
        energyCounterNumeric.increase();
    }

    /**
     * Decreases energy by 0.5.
     */
    protected void decreaseEnergy() {
        energyCounterNumeric.decrease();
    }

    /**
     * Handles the creation and removal of the sidekick.
     */
    protected void handleMockAvatar() {
        if(inputListener.isKeyPressed(KeyEvent.VK_P) && !isMockAvatarInGame){
            Vector2 pos = getTopLeftCorner().add(Vector2.of(30f, 0));
            mockAvatar = new MockAvatar(pos, inputListener, imageReader, gameObjects, this);
            gameObjects.addGameObject(mockAvatar, Layer.DEFAULT);
            isMockAvatarInGame = true;
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_D) && isMockAvatarInGame){
            gameObjects.removeGameObject(mockAvatar, Layer.DEFAULT);
            isMockAvatarInGame = false;
        }
    }



    /**
     * create new avatar
     * @param gameObjects - game objects
     * @param layer - avatar layer
     * @param topLeftCorner - top left corner avatar
     * @param inputListener - input listener
     * @param imageReader - image reader
     * @return avatar
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader){
        Avatar avatar = new Avatar(topLeftCorner, inputListener, imageReader ,gameObjects);
        gameObjects.addGameObject(avatar, layer);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.physics().setMass(-GameObjectPhysics.IMMOVABLE_MASS);
        avatar.setTag(AVATAR_TAG);
        return avatar;

    }

}
