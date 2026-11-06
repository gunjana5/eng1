package goosepatrol;
// x y w h  so overlap is one array
public class CollisionBox {
    float[] collisionBox;
    public CollisionBox(float x, float y, float w, float h) {
        // pack the hitbox
        collisionBox = new float[] {x, y, w, h};
    }
    public void set(float x, float y, float w, float h) {
        // keep the box on the sprite when it moves
        collisionBox[0] = x;
        collisionBox[1] = y;
        collisionBox[2] = w;
        collisionBox[3] = h;
    }
}
