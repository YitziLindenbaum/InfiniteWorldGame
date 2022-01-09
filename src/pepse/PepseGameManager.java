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
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

/**
 * Manages PEPSE game.
 */
public class PepseGameManager extends GameManager {

    private static final int SEED = 100 + new Random().nextInt(50);
    private static final int INIT_MAX_X = 5000;
    private static final int INIT_MIN_X = -INIT_MAX_X;
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int TREE_LAYER = Layer.STATIC_OBJECTS + 10;
    private static final int LEAVES_LAYER = Layer.STATIC_OBJECTS + 50;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final int CYCLE_LENGTH = 30;
    private static final float HALF = 0.5f;
    private static final int MINUS = -1;
    private static int cur_minX = INIT_MIN_X, cur_maxX = INIT_MAX_X;
    private Terrain terrain;
    private Avatar avatar;
    private Tree tree;
    private Vector2 windowDimensions;

    /**
     * Responsible for remove all the objects that beside the range of the game
     * @param minX of range of the game
     * @param maxX of range of the game
     */
    private void removeBesidesRange(int minX, int maxX) {
        for (GameObject object : gameObjects().objectsInLayer(Layer.STATIC_OBJECTS)) {
            if(object.getTopLeftCorner().x()  <= maxX && object.getTopLeftCorner().x() >= minX) continue;
            switch (object.getTag()){
                case Tree.TREE_TAG:
                    gameObjects().removeGameObject(object,TREE_LAYER);
                    break;
                case Leaf.LEAF_TAG:
                    gameObjects().removeGameObject(object,LEAVES_LAYER);
                    break;
                case Terrain.GROUND_TAG:
                    gameObjects().removeGameObject(object,Terrain.GROUND_LAYER);
                    break;
                case Terrain.GROUND_FIRST_LAYER_TAG:
                    gameObjects().removeGameObject(object,GROUND_LAYER);
                    break;
                default:

            }
            if (!object.getTag().equals(Tree.TREE_TAG) && !object.getTag().equals(Leaf.LEAF_TAG) &&
                !object.getTag().equals(Terrain.GROUND_TAG)) continue;
            if (object.getTopLeftCorner().x() > maxX) {
                gameObjects().removeGameObject(object, Layer.STATIC_OBJECTS);
            } else if (object.getTopLeftCorner().x() < minX) {
                gameObjects().removeGameObject(object, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * Run once per frame.
     * @param deltaTime -
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //Real world - adding more trees and ground
        //check if avatar get closer to the end of the ground
        if (avatar.getCenter().x() + windowDimensions.x() > cur_maxX) {
            terrain.createInRange(cur_maxX, cur_maxX + INIT_MAX_X);
            tree.createInRange(cur_maxX, cur_maxX + INIT_MAX_X);
            cur_maxX += INIT_MAX_X;
            cur_minX -= INIT_MIN_X;
            removeBesidesRange(cur_minX, cur_maxX); //remove the unnecessary objects
        }
        if (avatar.getCenter().x() - windowDimensions.x() < cur_minX) {
            terrain.createInRange(cur_minX + INIT_MIN_X, cur_minX);
            tree.createInRange(cur_minX + INIT_MIN_X, cur_minX);
            cur_maxX -= INIT_MAX_X;
            cur_minX += INIT_MIN_X;
            removeBesidesRange(cur_minX, cur_maxX); //remove the unnecessary objects
        }
    }

    /**
     * initializeGame - initialize all the objects in the game
     * @param imageReader - image reader
     * @param soundReader - sound reader
     * @param inputListener - parse keys
     * @param windowController -
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowDimensions = windowController.getWindowDimensions();

        Sky.create(gameObjects(),windowDimensions, SKY_LAYER);

        terrain = new Terrain(gameObjects(), GROUND_LAYER, windowDimensions, SEED);
        terrain.createInRange(INIT_MIN_X, INIT_MAX_X);

        Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, CYCLE_LENGTH);

        GameObject sun = Sun.create(gameObjects(), SUN_LAYER,
                windowDimensions, CYCLE_LENGTH);

        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun,
                SUN_HALO_COLOR);

        tree = new Tree(gameObjects(), terrain::groundHeightAt, TREE_LAYER, LEAVES_LAYER, SEED);
        tree.createInRange(INIT_MIN_X, INIT_MAX_X);

        initializeLayers();
        initializeAvatar(imageReader, inputListener, windowController);

    }

    private void initializeAvatar(ImageReader imageReader, UserInputListener inputListener,
                           WindowController windowController) {
        //create avatar
        float midX = windowDimensions.x() / 2;
        float y =
            (float) Math.floor(terrain.groundHeightAt(midX) / Block.SIZE) * Block.SIZE - Block.SIZE - Avatar.AVATAR_SIZE;
        Vector2 initialAvatarLocation = new Vector2(midX, y);
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, initialAvatarLocation,
            inputListener, imageReader);


        //set camera following after the avatar
        Camera camera = new Camera(avatar, windowDimensions.mult(HALF).add(initialAvatarLocation.mult(MINUS)),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());

        setCamera(camera);
    }

    private void initializeLayers() {
        //set avatar collide with the ground and tree
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, GROUND_LAYER,true); //first layer
        //gameObjects().layers().shouldLayersCollide(AVATAR_LAYER,Terrain.GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER,true);
        //set leaves collide with rhe first layer of the ground
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
    }

    /**
     * Main funtion to run program.
     * @param args
     */
    public static void main(String[] args) {

        new PepseGameManager().run();

    }
}
