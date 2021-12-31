package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
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

public class PepseGameManager extends GameManager {

    public int MIN_X = -3000;
    public int MAX_X = 3000;
    private Terrain terrain;
    private  Avatar avatar;
    private  Tree tree;
    private Vector2 windowDim;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //Real world - adding more trees and ground
        //Makes the program slower, we'll probably have to deal with it in another way.
        if(avatar.getCenter().x() + windowDim.x() > MAX_X){
            terrain.createInRange(MAX_X, 2 * MAX_X);
            tree.createInRange(MAX_X, 2 * MAX_X);
            MAX_X += MAX_X;
        }
        if (avatar.getCenter().x() - windowDim.x() < MIN_X){
            terrain.createInRange(2 * MIN_X, MIN_X);
            tree.createInRange(2 * MIN_X, MIN_X);
            MIN_X += MIN_X;
        }
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDim = windowController.getWindowDimensions();

        //create sky
        Sky.create(gameObjects(),windowController.getWindowDimensions(), Layer.BACKGROUND);

        //create terrain
        terrain = new Terrain(gameObjects(),Layer.DEFAULT, windowController.getWindowDimensions(), 1);
        terrain.createInRange(MIN_X, MAX_X);

        //create night
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), 30);

        //create sun
        GameObject sun = Sun.create(gameObjects(),Layer.BACKGROUND + 1,
                windowController.getWindowDimensions(), 30);

        //create sun halo
        SunHalo.create(gameObjects(), Layer.BACKGROUND + 10, sun,
                new Color(255,255,0,20));

        //create trees
        tree = new Tree(gameObjects(), terrain);
        tree.createInRange(MIN_X, MAX_X);

        //create avatar
        float midX = windowController.getWindowDimensions().x()/2;
        float y = (float)Math.floor(terrain.groundHeightAt(midX)/Block.SIZE)*Block.SIZE - Block.SIZE;
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT,new Vector2(midX, y ),
                inputListener, imageReader );

        //set camera following after the avatar
        Camera camera = new Camera(avatar, Vector2.ZERO,
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
