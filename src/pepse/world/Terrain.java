package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;


public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final Color SECOND_GROUND_COLOR = new Color(202, 113, 64);
    private static final Color THIRD_GROUND_COLOR = new Color(222, 133, 84);

    private GameObjectCollection gameObjects;
    private int groundLayer;
    private float groundHeightAtX0;
    private int seed;
    private Vector2 windowDimensions;
    private  static final float  BLOCK_SIZE = 20;
    private Function<Float,Float> noise;


    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * (2f/3f);
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        perlinNoise();
    }
    public float groundHeightAt(float x){
        return  noise.apply(x);
        //return groundHeightAtX0;
    }
    public void createInRange(int minX, int maxX){
        RectangleRenderable[] rend = {new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)),
                new RectangleRenderable(ColorSupplier.approximateColor(SECOND_GROUND_COLOR)),
                new RectangleRenderable(ColorSupplier.approximateColor(THIRD_GROUND_COLOR))};
        for(float x = minX, i = 0 ; x <= maxX; x+= Block.SIZE, i++){
            float height = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            System.out.println(height);
            for (float y = windowDimensions.y(), j = 0; y >= height; y -= Block.SIZE, j++){
                Block block = new Block(new Vector2(x,y), rend[(int) (i + j) % 3]);
                gameObjects.addGameObject(block, Layer.DEFAULT);
                block.setTag("ground");
            }
        }

    }
    private void perlinNoise(){
        Random rand = new Random();

        float rangeFactor = groundHeightAtX0/12;
        float factorE = rangeFactor * rand.nextFloat();
        float scalaE = 10 * rand.nextFloat();

        float factorPi = rangeFactor * rand.nextFloat();
        float scalaPi = 10 * rand.nextFloat();

        float factor1 = rangeFactor  * rand.nextFloat();
        float scala1 =  10 * rand.nextFloat();

        float totalFactor = 2f * rand.nextFloat();

        while (totalFactor == 0){
            totalFactor = 2f * rand.nextFloat();
        }
        float  finalTotalFactor = totalFactor;

        System.out.println(factor1 + " " + factorPi + " " + factorE + " total; "+ finalTotalFactor);
        System.out.println(scala1 + " " + scalaPi + " " + scalaE);
        noise = x -> groundHeightAtX0 +  finalTotalFactor * (float)(factor1 * Math.sin(scala1 * x) +
                factorE * Math.sin(scalaE * Math.E * x) + factorPi * Math.sin(scalaPi * Math.PI * x));
    }
}
