package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.OvalRenderable;
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

    private final GameObjectCollection gameObjects;
    private int groundLayer;
    private final float groundHeightAtX0;
    private int seed;
    private final Vector2 windowDimensions;
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
        float xInRange0150  = x/ windowDimensions.x() * 150;
        gameObjects.addGameObject(new GameObject(new Vector2(x,noise.apply(x) ),
                new Vector2(3.0f,3.0f),
                new OvalRenderable(Color.BLACK)),Layer.FOREGROUND);
        return noise.apply(x);
    }
    public void createInRange(int minX, int maxX){
        RectangleRenderable[] rend = {new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)),
                new RectangleRenderable(ColorSupplier.approximateColor(SECOND_GROUND_COLOR)),
                new RectangleRenderable(ColorSupplier.approximateColor(THIRD_GROUND_COLOR))};
        for(float x = minX, i = 0 ; x <= maxX; x+= Block.SIZE, i++){
            float height = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
            for (float y = windowDimensions.y(), j = 0; y >= height; y -= Block.SIZE, j++){
                Block block = new Block(new Vector2(x,y), rend[(int) (i + j) % 3]);
                gameObjects.addGameObject(block, Layer.DEFAULT);
                block.setTag("ground");
            }
        }

    }
    private void perlinNoise(){
        Random rand = new Random();
        float[] p = {-1.5f,-1.4f,-1.3f,-1.2f,-1.2f,-1.1f,-1.0f,-0.9f,-0.8f,-0.7f,-0.6f,
                1.5f,1.4f,1.3f,1.2f,1.2f,1.1f,1.0f,0.9f,0.8f,0.7f,0.6f};
        float[] pFactorTotal = { -0.1f, 0.1f};

        float rangeFactor = groundHeightAtX0;
        float normalizeRangeX  = 3f / windowDimensions.x();
        float factorE = p[rand.nextInt(p.length)];
        float scalaE = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factorPi = p[rand.nextInt(p.length)];
        float scalaPi = p[rand.nextInt(p.length)] * normalizeRangeX;

        float factor1 = p[rand.nextInt(p.length)];
        float scala1 = p[rand.nextInt(p.length)] * normalizeRangeX;

        float totalFactor = pFactorTotal[rand.nextInt(pFactorTotal.length)] * rangeFactor;


        System.out.println(totalFactor/rangeFactor + "(" + factor1 + "sin(" + scala1/normalizeRangeX + "x) + "+
                factorE + "sin("+scalaE/normalizeRangeX+"e x) + " + factorPi + "sin("+ scalaPi/normalizeRangeX+"pi x))");
        //System.out.println(scala1 + " " + scalaPi + " " + scalaE);
        noise = x -> groundHeightAtX0 +  totalFactor * (float)(factor1 * Math.sin(scala1 * x) +
                factorE * Math.sin(scalaE * Math.E * x) + factorPi * Math.sin(scalaPi * Math.PI * x));
    }
}
