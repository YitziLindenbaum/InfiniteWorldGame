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
    public static final String ENERGY_COUNTER_STR = "ENERGY: %g";
    private float energyCounter = 100;
    private final UserInputListener inputListener;
    private GameObject energyCounterNumeric;
    private ImageReader imageReader;

    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
        super(pos, Vector2.ONES.mult(50), new OvalRenderable(AVATAR_COLOR));
//        super(pos, Vector2.ONES.mult(100),
//                imageReader.readImage("asset/spongbob.png", true));
        this.imageReader = imageReader;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //move left
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
//            renderer().setRenderable(imageReader.readImage("asset/spongbobWalkLeft.gif",true));
        }

        //move right
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            xVel += VELOCITY_X;
        transform().setVelocityX(xVel);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);

        //fly
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energyCounter > 0) {
            //set energy counter - I remove this code to debug
            energyCounter -= 0.5f;
            energyCounterNumeric.renderer().setRenderable(new TextRenderable(String.format(
                    ENERGY_COUNTER_STR, energyCounter)));

            transform().setVelocityY(VELOCITY_Y);
            this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
            //Eliminates gravity for 0.001 seconds, it's not working well - the avatar passes through the ground
            //Basically they provided us with this code, but I found that it make the Avatar to fall through the ground.

            //physics().preventIntersectionsFromDirection(null);
            //physics().setMass(-GameObjectPhysics.IMMOVABLE_MASS);
//           new ScheduledTask(this, .0001f, false,
//                    ()->{
//               this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
//               //physics().setMass(-GameObjectPhysics.IMMOVABLE_MASS);
//           });
            return;
        }
        //jump
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
            transform().setVelocityY(VELOCITY_Y);
            return;
        }
        if (getVelocity().y() != 0){
            setVelocity(getVelocity().multY(0.99f));
        }

        if (transform().getVelocity().x() == 0 && transform().getVelocity().y() == 0){
            energyCounter += 0.5f;
            energyCounterNumeric.renderer().setRenderable(new TextRenderable(String.format(
                    ENERGY_COUNTER_STR, energyCounter)));
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
        energyCounterNumeric = new GameObject(Vector2.ZERO, new Vector2(10, 20),
                new TextRenderable(String.format(ENERGY_COUNTER_STR, energyCounter)));
        gameObjects.addGameObject(energyCounterNumeric, Layer.FOREGROUND * 2);
        energyCounterNumeric.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        energyCounterNumeric.setTopLeftCorner(Vector2.ONES.mult(5));
        //set the counter placed in the top left corner of the camera, it's not looking good :(
//        new ScheduledTask(energyCounterNumeric, 0.5f, true, () -> {
//           energyCounterNumeric.setCenter(camera.getTopLeftCorner().add(Vector2.ONES.mult(5)));
//        });
    }
}
