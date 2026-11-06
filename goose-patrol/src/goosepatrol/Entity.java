package goosepatrol;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public abstract class Entity {
    float x;
    float y;
    float w;
    float h;
    CollisionBox collisionBox;
    // pictures sit on the thing that moves
    Sprite sprite;
    public Entity(float x, float y, float w, float h) {
        // hitbox same size as the draw
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        collisionBox = new CollisionBox(x, y, w, h);
    }
    public void syncBox() {
        // box follows x y after a move
        collisionBox.set(x, y, w, h);
    }
    public boolean overlaps(Entity other) {
        // aabb from the collision box  not a second copy of x y
        float[] a = collisionBox.collisionBox;
        float[] b = other.collisionBox.collisionBox;
        return a[0] < b[0] + b[2] && a[0] + a[2] > b[0] && a[1] < b[1] + b[3] && a[1] + a[3] > b[1];
    }
    // player and goose tick themselves
    public abstract void update(float dt);
    // shapes path  pictures go through the batch draw
    public abstract void draw(ShapeRenderer shapes);
    public void draw(SpriteBatch batch) {
        // default  sprite at x y
        if (sprite != null) {
            sprite.draw(batch, x, y, w, h);
        }
    }
}
