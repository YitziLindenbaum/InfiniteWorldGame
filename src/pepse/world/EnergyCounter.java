package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

public class EnergyCounter extends GameObject{

    private static final Vector2 DIMENSIONS_ENERGY_COUNTER = new Vector2(20, 40);
    private static final Vector2 TOP_LEFT_CORNER_ENERGY_COUNTER = Vector2.ONES.mult(5);
    private static final String ENERGY_COUNTER_STR = "ENERGY: %d";
    private static float energyCounter = 100;
    private static final float ENERGY_PARAMETER = 0.5f;
    public static final int MAX_ENERGY_VALUE = 100;
    public static final int MIN_ENERGY_VALUE = 0;

    /**
     * constructor
     * @param topLeftCorner - top left corner
     * @param dimensions - size
     * @param renderable - text render
     */
    public EnergyCounter(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * create energy counter
     * @param gameObjects - game objects
     * @return energy counter
     */
    public static EnergyCounter create(GameObjectCollection gameObjects){
        EnergyCounter energy = new EnergyCounter(TOP_LEFT_CORNER_ENERGY_COUNTER, DIMENSIONS_ENERGY_COUNTER,
                new TextRenderable(String.format(ENERGY_COUNTER_STR, (int)energyCounter)));
        energy.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(energy, Layer.FOREGROUND);
        return energy;
    }

    /**
     * get energy
     * @return energy
     */
    public float getEnergy(){
        return energyCounter;
    }

    /**
     * increase
     */
    public void increase(){
        energyCounter = Float.min(energyCounter + ENERGY_PARAMETER, MAX_ENERGY_VALUE);
        changeRender();
    }

    /**
     * decrease
     */
    public void decrease(){
        energyCounter = Float.max(energyCounter - ENERGY_PARAMETER, MIN_ENERGY_VALUE);
        changeRender();
    }

    /**
     * set render
     */
    private void changeRender(){
        renderer().setRenderable(new TextRenderable(String.format(
                ENERGY_COUNTER_STR, (int)energyCounter)));
    }

}
