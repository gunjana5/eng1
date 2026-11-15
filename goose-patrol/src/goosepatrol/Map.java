package goosepatrol;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
public class Map {
    Player player;
    ArrayList<Goose> enemies;
    ArrayList<ProximityEvent> events;
    ArrayList<Item> worldItems;
    ArrayList<float[]> bushes;
    ArrayList<float[]> paths;
    ArrayList<float[]> posts;
    PlayerTracker tracker;
    float[] exit;
    public Map() {
        // spawn on the first east stretch
        player = new Player(70, 318);
        player.world = this;
        Item startFood = new Item("greggs", "food", 3, 2, 0, 0);
        startFood.taken = true;
        player.inventory[0] = startFood;
        Item startDrink = new Item("speed", "drink", 5, 1, 0, 0);
        startDrink.taken = true;
        player.inventory[1] = startDrink;
        tracker = new PlayerTracker(player);
        paths = new ArrayList<float[]>();
        // east through the pinch then north
        paths.add(new float[] {40, 300, 720, 64});
        paths.add(new float[] {696, 300, 64, 280});
        // fork  left is the quiet bit  right keeps going
        paths.add(new float[] {180, 516, 580, 64});
        paths.add(new float[] {180, 516, 64, 280});
        paths.add(new float[] {40, 732, 204, 56});
        paths.add(new float[] {696, 516, 64, 280});
        // join then last stretch to yellow
        paths.add(new float[] {180, 732, 700, 64});
        paths.add(new float[] {816, 732, 64, 200});
        paths.add(new float[] {816, 868, 480, 64});
        exit = new float[] {1296, 868, 200, 64};
        bushes = new ArrayList<float[]>();
        // pinch  path is the gap
        bushes.add(new float[] {520, 80, 110, 220});
        bushes.add(new float[] {520, 364, 110, 140});
        // block in the fork so left and right are not the same walk
        bushes.add(new float[] {280, 580, 380, 140});
        // under the east bit
        bushes.add(new float[] {300, 140, 500, 150});
        bushes.add(new float[] {800, 948, 720, 80});
        bushes.add(new float[] {0, 500, 32, 320});
        posts = new ArrayList<float[]>();
        // yellow
        posts.add(new float[] {1296, 864});
        posts.add(new float[] {1488, 864});
        enemies = new ArrayList<Goose>();
        // pinch  fork  leftover  last bit
        enemies.add(new Goose(520, 308, tracker));
        enemies.add(new Goose(708, 540, tracker));
        enemies.add(new Goose(188, 600, tracker));
        enemies.add(new Goose(900, 872, tracker));
        events = new ArrayList<ProximityEvent>();
        // long boi on a plinth  south of the first path
        events.add(new ProximityEvent("longboi", 160, 210, 48));
        // leftover on the side spur
        events.add(new ProximityEvent("leftover", 90, 750, 70));
        worldItems = new ArrayList<Item>();
        worldItems.add(new Item("speed", "drink", 5, 1, 250, 278));
    }
    public void dropLeftover() {
        // both  spread on the spur not a pile
        worldItems.add(new Item("greggs", "food", 3, 1, 50, 740));
        worldItems.add(new Item("greggs", "food", 3, 1, 90, 748));
        worldItems.add(new Item("greggs", "food", 3, 1, 130, 742));
        worldItems.add(new Item("greggs", "food", 3, 1, 200, 620));
        worldItems.add(new Item("speed", "drink", 5, 1, 80, 760));
        worldItems.add(new Item("speed", "drink", 5, 1, 190, 560));
        worldItems.add(new Item("speed", "drink", 5, 1, 210, 700));
    }
    public boolean onDirt(float px, float py, float pw, float ph) {
        // dirt plus the lawn beside it  far grass is a wall
        float pad = 100;
        for (int i = 0; i < paths.size(); i++) {
            float[] p = paths.get(i);
            if (px < p[0] + p[2] + pad && px + pw > p[0] - pad && py < p[1] + p[3] + pad && py + ph > p[1] - pad) {
                return true;
            }
        }
        if (px < exit[0] + exit[2] + pad && px + pw > exit[0] - pad && py < exit[1] + exit[3] + pad && py + ph > exit[1] - pad) {
            return true;
        }
        return false;
    }
    public void update(float dt) {
        player.update(dt);
        // one tracker for both geese  do not tick it twice
        if (tracker != null) {
            tracker.update(dt);
        }
        for (int i = 0; i < enemies.size(); i++) {
            Goose g = enemies.get(i);
            float oldX = g.x;
            float oldY = g.y;
            if (!g.fed) {
                g.followPlayer(player.x + player.w / 2, player.y + player.h / 2);
            }
            g.update(dt);
            if (g.fed) {
                continue;
            }
            // hungry ones stay near the walk  same as me
            if (hitsBush(g.x, g.y, g.w, g.h) || !onDirt(g.x, g.y, g.w, g.h)) {
                g.x = oldX;
                g.y = oldY;
                g.position.x = oldX;
                g.position.y = oldY;
                g.syncBox();
            }
        }
    }
    public boolean hitsBush(float px, float py, float pw, float ph) {
        // cant walk through hedges
        for (int i = 0; i < bushes.size(); i++) {
            float[] b = bushes.get(i);
            if (px < b[0] + b[2] && px + pw > b[0] && py < b[1] + b[3] && py + ph > b[1]) {
                return true;
            }
        }
        return false;
    }
    public boolean checkCollisions() {
        // far grass is blocked  bush or a hungry goose
        float[] box = player.collisionBox.collisionBox;
        if (!onDirt(box[0], box[1], box[2], box[3])) {
            return true;
        }
        if (hitsBush(box[0], box[1], box[2], box[3])) {
            return true;
        }
        for (int i = 0; i < enemies.size(); i++) {
            Goose g = enemies.get(i);
            if (g.fed) {
                continue;
            }
            if (player.overlaps(g)) {
                return true;
            }
        }
        return false;
    }
    public void undoPlayer(float oldX, float oldY) {
        // bump  put me back
        player.x = oldX;
        player.y = oldY;
        player.position.x = oldX;
        player.position.y = oldY;
        player.syncBox();
    }
    public Item touchingItem() {
        // walk onto food or drink
        for (int i = 0; i < worldItems.size(); i++) {
            Item it = worldItems.get(i);
            if (it.touching(player)) {
                return it;
            }
        }
        return null;
    }
    public Goose overlappingGoose() {
        // hungry ones only
        for (int i = 0; i < enemies.size(); i++) {
            Goose g = enemies.get(i);
            if (g.fed) {
                continue;
            }
            if (player.overlaps(g)) {
                return g;
            }
        }
        return null;
    }
    public boolean reachedExit() {
        // yellow
        return player.x + player.w > exit[0] && player.x < exit[0] + exit[2]
            && player.y + player.h > exit[1] && player.y < exit[1] + exit[3];
    }
    public void drawHedge(ShapeRenderer shapes, float[] b) {
        // blobs not a flat rect  20 step  r 18
        shapes.setColor(0.12f, 0.38f, 0.16f, 1);
        for (float px = b[0]; px < b[0] + b[2]; px += 20) {
            for (float py = b[1]; py < b[1] + b[3]; py += 20) {
                shapes.circle(px + 16, py + 16, 18);
            }
        }
    }
    public void drawPost(ShapeRenderer shapes, float px, float py) {
        // 8 by 64 brown stick
        shapes.setColor(0.4f, 0.26f, 0.12f, 1);
        shapes.rect(px, py, 8, 64);
    }
    public void drawPodium(ShapeRenderer shapes, float x, float y) {
        // empty plinth until you walk close
        shapes.setColor(0.7f, 0.66f, 0.56f, 1);
        shapes.rect(x - 18, y, 36, 12);
        shapes.rect(x - 12, y + 12, 24, 10);
    }
    public void drawLongBoi(ShapeRenderer shapes, float x, float y) {
        // tall white one  sits on the plinth
        shapes.setColor(0.93f, 0.93f, 0.95f, 1);
        shapes.ellipse(x - 12, y, 24, 22);
        shapes.rect(x - 4, y + 16, 8, 46);
        shapes.circle(x, y + 68, 10);
        shapes.setColor(1f, 0.55f, 0.12f, 1);
        shapes.triangle(x - 6, y + 72, x - 6, y + 64, x - 20, y + 68);
        shapes.setColor(0, 0, 0, 1);
        shapes.circle(x - 3, y + 70, 2);
    }
    public void draw(ShapeRenderer shapes, float camX, float camY, float viewW, float viewH) {
        // grass under the camera so the window is never grey
        shapes.setColor(0.45f, 0.7f, 0.38f, 1);
        shapes.rect(camX - viewW / 2 - 80, camY - viewH / 2 - 80, viewW + 160, viewH + 160);
        shapes.setColor(0.62f, 0.5f, 0.32f, 1);
        for (int i = 0; i < paths.size(); i++) {
            float[] p = paths.get(i);
            shapes.rect(p[0], p[1], p[2], p[3]);
        }
        shapes.setColor(0.95f, 0.85f, 0.3f, 1);
        shapes.rect(exit[0], exit[1], exit[2], exit[3]);
        for (int i = 0; i < bushes.size(); i++) {
            drawHedge(shapes, bushes.get(i));
        }
        for (int i = 0; i < posts.size(); i++) {
            float[] p = posts.get(i);
            drawPost(shapes, p[0], p[1]);
        }
        for (int i = 0; i < events.size(); i++) {
            ProximityEvent e = events.get(i);
            if (e.id.equals("longboi")) {
                drawPodium(shapes, e.x, e.y);
                if (e.triggered) {
                    // pops up once you walk in range
                    drawLongBoi(shapes, e.x, e.y + 22);
                }
            }
        }
    }
    public void drawItems(SpriteBatch batch) {
        // sausage roll and bottle on the path
        for (int i = 0; i < worldItems.size(); i++) {
            worldItems.get(i).draw(batch);
        }
    }
    public void drawPlayer(SpriteBatch batch) {
        // me plus whatever is in the hand
        player.draw(batch);
    }
    public void drawGeese(SpriteBatch batch) {
        // duckhive sheets
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(batch);
        }
    }
}
