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
    ArrayList<float[]> ponds;
    ArrayList<float[]> flowers;
    ArrayList<float[]> posts;
    PlayerTracker tracker;
    float[] exit;
    public Map() {
        // spawn on the first east stretch  pond is under you
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
        // east through pinch 1  keep going until you are past the posts then north
        paths.add(new float[] {40, 300, 560, 64});
        // north after pinch 1  x 536 is past the hedge at x 508 so the turn is open
        paths.add(new float[] {536, 300, 64, 260});
        // fork  left is long  right has goose 2
        paths.add(new float[] {160, 496, 440, 64});
        paths.add(new float[] {160, 496, 64, 320});
        // leftover food on this side bit
        paths.add(new float[] {40, 700, 180, 56});
        paths.add(new float[] {536, 496, 400, 64});
        // north after pinch 2  x 856 is past those hedges
        paths.add(new float[] {856, 496, 64, 320});
        // join then last bend to yellow
        paths.add(new float[] {160, 752, 760, 64});
        paths.add(new float[] {856, 752, 64, 200});
        paths.add(new float[] {856, 888, 500, 64});
        exit = new float[] {1356, 888, 200, 64};
        bushes = new ArrayList<float[]>();
        // pinch 1  top hedge stops before the fork at y 496
        bushes.add(new float[] {400, 80, 108, 220});
        bushes.add(new float[] {400, 364, 108, 116});
        // pinch 2  top hedge stops before the join at y 752
        bushes.add(new float[] {720, 240, 108, 256});
        bushes.add(new float[] {720, 560, 108, 176});
        // island in the fork so left and right are actually different
        bushes.add(new float[] {280, 580, 450, 150});
        // ponds first  scatter uses nearWater so this list cannot be empty yet
        ponds = new ArrayList<float[]>();
        ponds.add(new float[] {70, 150, 180, 120});
        ponds.add(new float[] {300, 640, 80, 60});
        // 16 by 11 grid  skip if near a path or the water
        for (int gx = 0; gx < 16; gx++) {
            for (int gy = 0; gy < 11; gy++) {
                float x = 30 + gx * 110;
                float y = 20 + gy * 105;
                if (nearPath(x, y, 70) || nearWater(x, y, 40)) {
                    continue;
                }
                bushes.add(new float[] {x, y, 50, 50});
            }
        }
        posts = new ArrayList<float[]>();
        posts.add(new float[] {400, 296});
        posts.add(new float[] {508, 296});
        posts.add(new float[] {720, 492});
        posts.add(new float[] {828, 492});
        posts.add(new float[] {1356, 884});
        posts.add(new float[] {1548, 884});
        flowers = new ArrayList<float[]>();
        for (int i = 0; i < bushes.size(); i++) {
            float[] b = bushes.get(i);
            flowers.add(new float[] {b[0] + 62, b[1] + 14});
            flowers.add(new float[] {b[0] + 18, b[1] + 68});
        }
        enemies = new ArrayList<Goose>();
        // pinch 1 then pinch 2  not the same gate
        enemies.add(new Goose(430, 302, tracker));
        enemies.add(new Goose(760, 498, tracker));
        events = new ArrayList<ProximityEvent>();
        // long boi in the grass south of the first stretch
        events.add(new ProximityEvent("longboi", 320, 190, 48));
        // leftover food on the west spur
        events.add(new ProximityEvent("leftover", 90, 718, 50));
        worldItems = new ArrayList<Item>();
        worldItems.add(new Item("speed", "drink", 5, 1, 260, 278));
    }
    public boolean nearPath(float x, float y, float pad) {
        // used so scatter bushes do not sit on the dirt
        for (int i = 0; i < paths.size(); i++) {
            float[] p = paths.get(i);
            if (x + pad > p[0] && x < p[0] + p[2] + pad && y + pad > p[1] && y < p[1] + p[3] + pad) {
                return true;
            }
        }
        if (x + pad > exit[0] && x < exit[0] + exit[2] + pad && y + pad > exit[1] && y < exit[1] + exit[3] + pad) {
            return true;
        }
        return false;
    }
    public boolean nearWater(float x, float y, float pad) {
        // same skip for ponds
        for (int i = 0; i < ponds.size(); i++) {
            float[] p = ponds.get(i);
            if (x + pad > p[0] && x < p[0] + p[2] + pad && y + pad > p[1] && y < p[1] + p[3] + pad) {
                return true;
            }
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
            if (!g.fed) {
                g.followPlayer(player.x + player.w / 2, player.y + player.h / 2);
            }
            g.update(dt);
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
    public boolean hitsPond(float px, float py, float pw, float ph) {
        // dont walk on the water
        for (int i = 0; i < ponds.size(); i++) {
            float[] p = ponds.get(i);
            if (px < p[0] + p[2] && px + pw > p[0] && py < p[1] + p[3] && py + ph > p[1]) {
                return true;
            }
        }
        return false;
    }
    public boolean checkCollisions() {
        // bush pond or a hungry goose
        float[] box = player.collisionBox.collisionBox;
        if (hitsBush(box[0], box[1], box[2], box[3])) {
            return true;
        }
        if (hitsPond(box[0], box[1], box[2], box[3])) {
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
    public void draw(ShapeRenderer shapes, float camX, float camY, float viewW, float viewH) {
        // grass under the camera so the window is never grey
        shapes.setColor(0.45f, 0.7f, 0.38f, 1);
        shapes.rect(camX - viewW / 2 - 80, camY - viewH / 2 - 80, viewW + 160, viewH + 160);
        shapes.setColor(0.85f, 0.82f, 0.25f, 1);
        for (int i = 0; i < flowers.size(); i++) {
            float[] f = flowers.get(i);
            shapes.circle(f[0], f[1], 5);
        }
        shapes.setColor(0.62f, 0.5f, 0.32f, 1);
        for (int i = 0; i < paths.size(); i++) {
            float[] p = paths.get(i);
            shapes.rect(p[0], p[1], p[2], p[3]);
        }
        shapes.setColor(0.95f, 0.85f, 0.3f, 1);
        shapes.rect(exit[0], exit[1], exit[2], exit[3]);
        for (int i = 0; i < ponds.size(); i++) {
            float[] p = ponds.get(i);
            shapes.setColor(0.28f, 0.52f, 0.78f, 1);
            shapes.ellipse(p[0], p[1], p[2], p[3]);
            shapes.setColor(0.4f, 0.65f, 0.85f, 1);
            shapes.ellipse(p[0] + 24, p[1] + 16, p[2] * 0.35f, p[3] * 0.3f);
        }
        for (int i = 0; i < bushes.size(); i++) {
            drawHedge(shapes, bushes.get(i));
        }
        for (int i = 0; i < posts.size(); i++) {
            float[] p = posts.get(i);
            drawPost(shapes, p[0], p[1]);
        }
        for (int i = 0; i < events.size(); i++) {
            ProximityEvent e = events.get(i);
            if (!e.triggered || !e.id.equals("longboi")) {
                continue;
            }
            // long boi is the tall one so it is not the same as the path geese
            shapes.setColor(0.93f, 0.93f, 0.95f, 1);
            shapes.ellipse(e.x - 12, e.y, 24, 22);
            shapes.rect(e.x - 4, e.y + 16, 8, 46);
            shapes.circle(e.x, e.y + 68, 10);
            shapes.setColor(1f, 0.55f, 0.12f, 1);
            shapes.triangle(e.x - 6, e.y + 72, e.x - 6, e.y + 64, e.x - 20, e.y + 68);
            shapes.setColor(0, 0, 0, 1);
            shapes.circle(e.x - 3, e.y + 70, 2);
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
