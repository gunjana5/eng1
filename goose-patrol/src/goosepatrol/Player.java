package goosepatrol;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public class Player extends Entity implements PlayerController {
    Item[] inventory;
    int selected;
    Position position;
    Velocity velocity;
    float speed;
    float baseSpeed = 230;
    float drinkBonus = 105;
    float speedBoostLeft;
    int health;
    float hurtCool;
    float stamina;
    float staminaMax = 100;
    boolean movedThisFrame;
    Map world;
    boolean justUsed;
    String lastUsedId;
    public Player(float x, float y) {
        // little person hitbox  sprite is the same size
        super(x, y, 16, 28);
        position = new Position(x, y);
        velocity = new Velocity(0, 0);
        sprite = Sprite.playerSprite();
        speed = baseSpeed;
        speedBoostLeft = 0;
        inventory = new Item[2];
        selected = 0;
        health = 3;
        hurtCool = 0;
        stamina = staminaMax;
        movedThisFrame = false;
        justUsed = false;
        lastUsedId = null;
    }
    public void beginFrame(float dt) {
        // speed for this frame  keys set the vector  release clears it
        if (speedBoostLeft > 0) {
            // drink adds the same extra it always did  walk went up 80 so drink did too
            speed = baseSpeed + drinkBonus;
        } else {
            speed = baseSpeed;
        }
        if (stamina <= 0) {
            // 0.4 when you are out of breath
            speed = speed * 0.4f;
        }
        movedThisFrame = false;
    }
    public void update(float dt) {
        // move then drain stamina
        if (hurtCool > 0) {
            hurtCool -= dt;
        }
        if (speedBoostLeft > 0) {
            speedBoostLeft -= dt;
        }
        x += velocity.x * dt;
        y += velocity.y * dt;
        position.x = x;
        position.y = y;
        if (velocity.x < -1) {
            sprite.faceLeft = true;
        } else if (velocity.x > 1) {
            sprite.faceLeft = false;
        }
        sprite.usePlayer(speedBoostLeft > 0);
        // stamina goes down while you run
        if (movedThisFrame) {
            // 18 a second
            stamina -= 18 * dt;
            if (stamina < 0) {
                stamina = 0;
            }
        } else {
            // 12 a second back
            stamina += 12 * dt;
            if (stamina > staminaMax) {
                stamina = staminaMax;
            }
        }
        syncBox();
    }
    public void draw(ShapeRenderer shapes) {
        // blob under the feet so you read as on the path
        shapes.setColor(0.16f, 0.16f, 0.14f, 1);
        shapes.ellipse(x - 2, y - 3, w + 4, 8);
    }
    public void draw(SpriteBatch batch) {
        sprite.draw(batch, x, y, w, h);
        // 1/2 puts the selected item in the hand  last use clears the slot so this goes
        Item held = inventory[selected];
        if (held == null || held.texture == null) {
            return;
        }
        float hw = 20;
        float hh = 14;
        if (held.id.equals("speed")) {
            // bottle is taller
            hw = 11;
            hh = 18;
        }
        float hy = y + 8;
        float hx;
        if (sprite.faceLeft) {
            hx = x - hw - 1;
        } else {
            hx = x + w + 1;
        }
        batch.draw(held.texture, hx, hy, hw, hh);
    }
    public void moveLeft() {
        // a
        velocity.x = -speed;
        movedThisFrame = true;
    }
    public void moveRight() {
        // d
        velocity.x = speed;
        movedThisFrame = true;
    }
    public void moveUp() {
        // w
        velocity.y = speed;
        movedThisFrame = true;
    }
    public void moveDown() {
        // s
        velocity.y = -speed;
        movedThisFrame = true;
    }
    public void stopX() {
        // let go of a/d
        velocity.x = 0;
    }
    public void stopY() {
        // let go of w/s
        velocity.y = 0;
    }
    public void pickUp() {
        // walk onto it  fills a free slot
        if (world == null) {
            return;
        }
        pickUp(world.touchingItem());
    }
    public void pickUp(Item item) {
        // pocket until 1/2 then E
        if (item == null || item.taken) {
            return;
        }
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                item.taken = true;
                inventory[i] = item;
                return;
            }
        }
    }
    public void selectSlot(int slot) {
        // 1 is 0  2 is 1
        if (slot < 0 || slot >= inventory.length) {
            return;
        }
        selected = slot;
    }
    public void useItem() {
        // 1/2 pick the slot  then E consumes that one
        justUsed = false;
        lastUsedId = null;
        Item it = inventory[selected];
        // empty or not consumable  player stays the same
        if (it == null) {
            return;
        }
        if (!it.consumable) {
            return;
        }
        if (it.id.equals("greggs")) {
            if (world != null) {
                Goose g = world.overlappingGoose();
                if (g != null) {
                    it.uses -= 1;
                    g.eat(it);
                    if (it.uses <= 0) {
                        inventory[selected] = null;
                    }
                    justUsed = true;
                    lastUsedId = "food";
                }
            }
            return;
        }
        if (it.id.equals("speed")) {
            speedBoostLeft = it.duration;
            it.uses -= 1;
            if (it.uses <= 0) {
                inventory[selected] = null;
            }
            justUsed = true;
            lastUsedId = "drink";
            return;
        }
    }
    public boolean hitByGoose() {
        // 1s before they can bite again
        if (hurtCool > 0) {
            return false;
        }
        health -= 1;
        hurtCool = 1;
        return true;
    }
}
