package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.awt.*;

public class PepseGameManager extends GameManager {

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sky.create(gameObjects(),windowController.getWindowDimensions(), Layer.BACKGROUND);
        Terrain terrain = new Terrain(gameObjects(),Layer.DEFAULT, windowController.getWindowDimensions(), 1);
        terrain.createInRange(0, (int)windowController.getWindowDimensions().x() );
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), 30);
        GameObject sun = Sun.create(gameObjects(),Layer.BACKGROUND + 1,
                windowController.getWindowDimensions(), 1000);
        GameObject sunHalo  = SunHalo.create(gameObjects(), Layer.BACKGROUND + 10, sun,
                new Color(255,255,0,20));

    }

    public static void main(String[] args) {

        new PepseGameManager().run();
    }
}
