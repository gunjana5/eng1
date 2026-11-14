package goosepatrol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
// geese = duckhive sheets  player = the little person i paint
public class Sprite {
    static Texture idleSheet;
    static Texture walkSheet;
    static Texture runSheet;
    static Texture playerSheet;
    static Texture playerBoostSheet;
    static Object[] idleFrames;
    static Object[] walkFrames;
    static Object[] runFrames;
    static Object[] playerFrames;
    static Object[] playerBoostFrames;
    static boolean loaded;
    Object[] images;
    float[] times;
    float clock;
    int frame;
    boolean faceLeft;
    public static boolean tryLoad() {
        // load  once  cut the sheets into frames
        if (loaded) {
            return true;
        }
        try {
            idleSheet = sheet("goose/Idle.png");
            walkSheet = sheet("goose/Walk.png");
            runSheet = sheet("goose/Run.png");
            // idle is 2 frames  walk and run are 4
            idleFrames = cut(idleSheet, 2);
            walkFrames = cut(walkSheet, 4);
            runFrames = cut(runSheet, 4);
            playerSheet = makePerson(false);
            playerBoostSheet = makePerson(true);
            playerFrames = new Object[] { new TextureRegion(playerSheet) };
            playerBoostFrames = new Object[] { new TextureRegion(playerBoostSheet) };
            loaded = true;
            return true;
        } catch (Exception e) {
            unload();
            return false;
        }
    }
    static Texture sheet(String name) {
        // nearest so the pixels stay chunky
        Texture t = new Texture(Gdx.files.internal(name));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }
    static Object[] cut(Texture sheet, int n) {
        // duckhive frames are 64x64
        TextureRegion[][] grid = TextureRegion.split(sheet, 64, 64);
        Object[] out = new Object[n];
        for (int i = 0; i < n; i++) {
            out[i] = grid[0][i];
        }
        return out;
    }
    static Texture makePerson(boolean boost) {
        // same little person i used to draw with rectangles
        Pixmap p = new Pixmap(16, 28, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0);
        p.fill();
        p.setColor(0.15f, 0.15f, 0.4f, 1);
        p.fillRectangle(2, 18, 4, 10);
        p.fillRectangle(10, 18, 4, 10);
        if (boost) {
            // cyan when the drink is on
            p.setColor(0.2f, 0.75f, 0.9f, 1);
        } else {
            p.setColor(0.2f, 0.4f, 0.9f, 1);
        }
        p.fillRectangle(2, 8, 12, 12);
        p.setColor(0.95f, 0.8f, 0.65f, 1);
        p.fillCircle(8, 6, 5);
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
        return t;
    }
    public static void unload() {
        // game closing
        if (idleSheet != null) {
            idleSheet.dispose();
        }
        if (walkSheet != null) {
            walkSheet.dispose();
        }
        if (runSheet != null) {
            runSheet.dispose();
        }
        if (playerSheet != null) {
            playerSheet.dispose();
        }
        if (playerBoostSheet != null) {
            playerBoostSheet.dispose();
        }
        idleSheet = null;
        walkSheet = null;
        runSheet = null;
        playerSheet = null;
        playerBoostSheet = null;
        loaded = false;
    }
    public Sprite() {
        // geese start idle  facing left like the sheet
        images = idleFrames;
        times = new float[] {0.28f, 0.28f};
        clock = 0;
        frame = 0;
        faceLeft = true;
    }
    public static Sprite playerSprite() {
        // one frame  not a sheet
        Sprite s = new Sprite();
        s.images = playerFrames;
        s.times = new float[] {1f};
        s.faceLeft = false;
        return s;
    }
    public void useIdle() {
        // 0.28s a frame
        setAnim(idleFrames, new float[] {0.28f, 0.28f});
    }
    public void useWalk() {
        // 0.12s a frame
        setAnim(walkFrames, new float[] {0.12f, 0.12f, 0.12f, 0.12f});
    }
    public void useRun() {
        // 0.08s a frame
        setAnim(runFrames, new float[] {0.08f, 0.08f, 0.08f, 0.08f});
    }
    public void usePlayer(boolean boost) {
        // drink on  cyan person
        if (boost) {
            setAnim(playerBoostFrames, new float[] {1f});
        } else {
            setAnim(playerFrames, new float[] {1f});
        }
    }
    void setAnim(Object[] next, float[] nextTimes) {
        // dont reset if it is already this anim
        if (next == null || images == next) {
            return;
        }
        images = next;
        times = nextTimes;
        frame = 0;
        clock = 0;
    }
    public void tick(float dt) {
        // advance the sheet
        if (images == null || images.length == 0) {
            return;
        }
        clock += dt;
        float hold = times[frame];
        if (clock >= hold) {
            clock = 0;
            frame = (frame + 1) % images.length;
        }
    }
    public void draw(SpriteBatch batch, float x, float y, float drawW, float drawH) {
        // current frame  flipped if they look left
        if (images == null || images.length == 0) {
            return;
        }
        TextureRegion r = (TextureRegion) images[frame];
        if (faceLeft) {
            // flip in x
            batch.draw(r, x + drawW, y, -drawW, drawH);
        } else {
            batch.draw(r, x, y, drawW, drawH);
        }
    }
}
