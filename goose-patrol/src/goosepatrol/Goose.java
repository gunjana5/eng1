package goosepatrol;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public class Goose extends Entity {
    float hungerTimer;
    float freezeLeft;
    boolean fed;
    float sitX;
    float sitY;
    Position position;
    Velocity velocity;
    Item item;
    PlayerTracker tracker;
    float followSpeed = 70;
    public Goose(float x, float y, PlayerTracker tracker) {
        // fat enough that a 64 path is a pinch
        super(x, y, 40, 58);
        hungerTimer = 0;
        freezeLeft = 0;
        fed = false;
        sitX = x;
        sitY = y;
        position = new Position(x, y);
        velocity = new Velocity(0, 0);
        sprite = new Sprite();
        item = null;
        this.tracker = tracker;
    }
    public void followPlayer(float px, float py) {
        // chase me using the tracker  skip if they ate or i went afk
        if (fed) {
            return;
        }
        if (tracker != null) {
            if (tracker.timedOut() || tracker.afk) {
                return;
            }
            tracker.followPlayer();
            px = tracker.getdX();
            py = tracker.getDy();
        }
        if (freezeLeft > 0) {
            return;
        }
        float cx = x + w / 2;
        float cy = y + h / 2;
        float dx = px - cx;
        float dy = py - cy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) {
            // already on me
            return;
        }
        // hungrier they get a bit quicker  cap +40
        float extra = hungerTimer;
        if (extra > 40) {
            extra = 40;
        }
        float step = followSpeed + extra;
        velocity.x = dx / dist * step;
        velocity.y = dy / dist * step;
    }
    public void eat() {
        // long boi path  no food item
        eat(null);
    }
    public void eat(Item food) {
        // walk onto the grass not teleport  999 so they stay off you
        item = food;
        fed = true;
        hungerTimer = 0;
        freezeLeft = 999;
        if (tracker != null) {
            tracker.setTimeOut(999);
        }
        sitX = x - 55;
        sitY = y - 70;
    }
    public void update(float dt) {
        if (!fed) {
            hungerTimer += dt;
        }
        if (freezeLeft > 0) {
            freezeLeft -= dt;
        }
        if (fed) {
            // 70 a second towards the sit spot
            float step = 70 * dt;
            if (x > sitX) {
                x -= step;
                if (x < sitX) {
                    x = sitX;
                }
            }
            if (y > sitY) {
                y -= step;
                if (y < sitY) {
                    y = sitY;
                }
            }
            sprite.faceLeft = true;
            if (x != sitX || y != sitY) {
                sprite.useWalk();
            } else {
                sprite.useIdle();
            }
        } else {
            if (velocity.x < -1) {
                sprite.faceLeft = true;
            } else if (velocity.x > 1) {
                sprite.faceLeft = false;
            }
            boolean moving = Math.abs(velocity.x) > 1 || Math.abs(velocity.y) > 1;
            if (moving) {
                sprite.useRun();
            } else {
                sprite.useIdle();
            }
            x += velocity.x * dt;
            y += velocity.y * dt;
            velocity.x = 0;
            velocity.y = 0;
        }
        sprite.tick(dt);
        position.x = x;
        position.y = y;
        syncBox();
    }
    public void draw(ShapeRenderer shapes) {
        // blob under the feet
        shapes.setColor(0.16f, 0.16f, 0.14f, 1);
        shapes.ellipse(x, y - 4, w, 12);
    }
    public void draw(SpriteBatch batch) {
        // sheet is 64x64 with padding so i scale it up on the path
        float drawW = 80;
        float drawH = 80;
        float dx = x + w / 2 - drawW / 2;
        // 6 down so the feet sit on the dirt
        float dy = y - 6;
        sprite.draw(batch, dx, dy, drawW, drawH);
    }
}
