package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject{
    /**
     *
     */

    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 300;
    private static final Color AVATAR_COLOR = Color.DARK_GRAY;
    public static final String ENERGY_COUNTER_STR = "ENERGY: %d";
    private float energyCounter = 100;
    private final UserInputListener inputListener;
    private GameObject energyCounterNumeric;
    private ImageReader imageReader;
    private AnimationRenderable animationLeft;
    private AnimationRenderable animationRight;
    private AnimationRenderable animationStand;

    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
//        super(pos, Vector2.ONES.mult(50), new OvalRenderable(AVATAR_COLOR));
        super(pos, Vector2.ONES.mult(50),
                imageReader.readImage("asset/spongebobStand.png", true));
        this.imageReader = imageReader;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        /*this.animationLeft = new AnimationRenderable(new ImageRenderable[]{
                this.imageReader.readImage("asset/spongebobWalkLeft/img1.png", true),
                this.imageReader.readImage("asset/spongebobWalkLeft/img2.png", true),
                this.imageReader.readImage("asset/spongebobWalkLeft/img3.png", true),
                this.imageReader.readImage("asset/spongebobWalkLeft/img4.png", true),
                this.imageReader.readImage("asset/spongebobWalkLeft/img5.png", true)
        }, 0.5F);*/
        this.animationRight = new AnimationRenderable(new ImageRenderable[]{
                this.imageReader.readImage("asset/spongebobWalkRight/img1.png", true),
                this.imageReader.readImage("asset/spongebobWalkRight/img2.png", true),
                this.imageReader.readImage("asset/spongebobWalkRight/img3.png", true),
                this.imageReader.readImage("asset/spongebobWalkRight/img4.png", true),
                this.imageReader.readImage("asset/spongebobWalkRight/img5.png", true)
        }, 0.5F);
        /*this.animationStand = new AnimationRenderable(new ImageRenderable[]{
                this.imageReader.readImage("asset/spongebobStand/img1.png", true),
                this.imageReader.readImage("asset/spongebobStand/img2.png", true),
                this.imageReader.readImage("asset/spongebobStand/img3.png", true),
                this.imageReader.readImage("asset/spongebobStand/img4.png", true),
                this.imageReader.readImage("asset/spongebobStand/img5.png", true)
        }, 0.5F);*/
        renderer().setRenderable(animationRight);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //move left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
        }

        //move right
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
        }
        transform().setVelocityX(xVel);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);

        //fly
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energyCounter > 0) {
            //set energy counter - I remove this code to debug
            energyCounter -= 0.5f;
            energyCounterNumeric.renderer().setRenderable(new TextRenderable(String.format(
                    ENERGY_COUNTER_STR, (int)energyCounter)));
            renderer().setRenderableAngle(45);
            transform().setVelocityY(VELOCITY_Y);
            this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            //Eliminates gravity for 0.001 seconds, it's not working well - the avatar passes through the ground
            //Basically they provided us with this code, but I found that it make the Avatar to fall through the ground.

//            physics().preventIntersectionsFromDirection(null);
//           new ScheduledTask(this, .0001f, false, ()->{
//               this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
////               //physics().setMass(-GameObjectPhysics.IMMOVABLE_MASS);
//           });
            return;
        }
        //jump
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
            transform().setVelocityY(VELOCITY_Y);
            return;
        }
        if (getVelocity().y() != 0){
            setVelocity(getVelocity().multY(0.97f));
            renderer().setRenderableAngle(renderer().getRenderableAngle() * 0.6f);
        }

        if (transform().getVelocity().x() == 0 && transform().getVelocity().y() == 0){
            renderer().setRenderableAngle(0);
            //renderer().setRenderable(imageReader.readImage("asset/spongebobWalkRight/img1.png", true));
            if (energyCounter >= 100) return;
            energyCounter += 0.5f;
            energyCounterNumeric.renderer().setRenderable(new TextRenderable(String.format(
                    ENERGY_COUNTER_STR, (int)energyCounter)));
        }

    }


    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader){
        Avatar avatar = new Avatar(topLeftCorner, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.physics().setMass(-GameObjectPhysics.IMMOVABLE_MASS);
        return avatar;

    }
    public void createEnergyCounter(GameObjectCollection gameObjects, Camera camera)
    {
        //add new object of energyCounterNumeric
        energyCounterNumeric = new GameObject(Vector2.ZERO, new Vector2(20, 40),
                new TextRenderable(String.format(ENERGY_COUNTER_STR, (int)energyCounter)));
        gameObjects.addGameObject(energyCounterNumeric, Layer.UI);
        energyCounterNumeric.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        energyCounterNumeric.setTopLeftCorner(Vector2.ONES.mult(5));
    }
}
