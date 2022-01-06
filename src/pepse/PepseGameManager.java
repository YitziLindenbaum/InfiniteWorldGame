package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final int SEED = 100 + new Random().nextInt(50);
    private static final int INIT_MAX_X = 5000;
    private static final int INIT_MIN_X = -INIT_MAX_X;
    private static int cur_minX = INIT_MIN_X, cur_maxX = INIT_MAX_X;
    private Terrain terrain;
    private  Avatar avatar;
    private  Tree tree;
    private Vector2 windowDimensions;

    private void removeBesidesRange(int minX, int maxX){
        for (GameObject object : gameObjects().objectsInLayer(Layer.STATIC_OBJECTS)){
            if(!object.getTag().equals("tree") && !object.getTag().equals("leaf") &&
                    !object.getTag().equals("ground")) continue;
            if (object.getTopLeftCorner().x() > maxX){
                gameObjects().removeGameObject(object, Layer.STATIC_OBJECTS);
            }
            else if (object.getTopLeftCorner().x() < minX){
                gameObjects().removeGameObject(object, Layer.STATIC_OBJECTS);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //Real world - adding more trees and ground
        //Makes the program slower, we'll probably have to deal with it in another way.
        if(avatar.getCenter().x() + windowDimensions.x() > cur_maxX){
            System.out.println("in1");
            terrain.createInRange(cur_maxX, cur_maxX + INIT_MAX_X);
            tree.createInRange(cur_maxX, cur_maxX + INIT_MAX_X);
            cur_maxX += INIT_MAX_X;
            cur_minX -= INIT_MIN_X;
            removeBesidesRange(cur_minX, cur_maxX);
        }
        if (avatar.getCenter().x() - windowDimensions.x() < cur_minX){
            System.out.println("in2");
            terrain.createInRange(cur_minX + INIT_MIN_X, cur_minX);
            tree.createInRange(cur_minX + INIT_MIN_X, cur_minX);
            cur_maxX -= INIT_MAX_X;
            cur_minX += INIT_MIN_X;
            removeBesidesRange(cur_minX, cur_maxX);
        }
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        System.out.println(SEED);
        windowDimensions = windowController.getWindowDimensions();

        //create sky
        Sky.create(gameObjects(),windowDimensions, Layer.BACKGROUND);

        //create terrain
        terrain = new Terrain(gameObjects(),Layer.STATIC_OBJECTS, windowDimensions, SEED);
        terrain.createInRange(INIT_MIN_X, INIT_MAX_X);

        //create night
        Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions, 30);

        //create sun
        GameObject sun = Sun.create(gameObjects(),Layer.BACKGROUND + 1,
                windowDimensions, 30);

        //create sun halo
        SunHalo.create(gameObjects(), Layer.BACKGROUND + 10, sun,
                new Color(255,255,0,20));

        //create trees
        tree = new Tree(gameObjects(), terrain::groundHeightAt);
        tree.createInRange(INIT_MIN_X, INIT_MAX_X);

        //set avatar collide with the ground and tree with STATIC_OBJECTS
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,Layer.STATIC_OBJECTS,true);

        //create avatar
        float midX = windowDimensions.x()/2;
        float y = (float)Math.floor(terrain.groundHeightAt(midX)/Block.SIZE)*Block.SIZE - Block.SIZE -75;
        Vector2 initialAvatarLocation = new Vector2(midX, y);
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT, initialAvatarLocation,
                inputListener, imageReader );



        //set camera following after the avatar
        Camera camera = new Camera(avatar, windowDimensions.mult(0.5f).add(initialAvatarLocation.mult(-1)),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());

        //create energyCounter
        avatar.createEnergyCounter(gameObjects(), camera);
        setCamera(camera);

    }

    public static void main(String[] args) {

        new PepseGameManager().run();

    }
}
