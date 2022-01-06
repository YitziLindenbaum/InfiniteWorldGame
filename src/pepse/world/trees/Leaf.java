package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

public class Leaf extends GameObject {

    private final GameObjectCollection gameObjects;
    private final int layer;
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                GameObjectCollection gameObjects, int layer) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
//          System.out.println(other.getTag());
        if(other instanceof Block){
//            System.out.println("whhhy");
            gameObjects.removeGameObject(this, layer);
        }
    }

}
