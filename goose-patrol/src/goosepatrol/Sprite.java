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
    static Texture playerWalkSheet;
    static Texture playerBoostSheet;
    static Object[] idleFrames;
    static Object[] walkFrames;
    static Object[] runFrames;
    static Object[] playerFrames;
    static Object[] playerWalkFrames;
    static Object[] playerBoostFrames;
    static boolean loaded;
    Object[] images;
    float[] times;
    float clock;
    int frame;
    boolean faceLeft;

    public static boolean tryLoad() {
        if (loaded) {
            return true;
        }
        try {
            idleSheet = sheet("goose/Idle.png");
            walkSheet = sheet("goose/Walk.png");
            runSheet = sheet("goose/Run.png");
            idleFrames = cut(idleSheet, 2, 64, 64);
            walkFrames = cut(walkSheet, 4, 64, 64);
            runFrames = cut(runSheet, 4, 64, 64);

            //refactored for player spritesheet
            playerSheet = sheet("player/idle.png");
            playerWalkSheet = sheet("player/walk.png");
            playerBoostSheet = sheet("player/run.png");
            playerFrames = cutEven(playerSheet, 2);
            playerWalkFrames = cutEven(playerWalkSheet, 4);
            playerBoostFrames = cutEven(playerBoostSheet, 4);

            loaded = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace(); //log error
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

    static Object[] cut(Texture sheet, int n, int w, int h) {
        TextureRegion[][] grid = TextureRegion.split(sheet, w, h);
        Object[] out = new Object[n];
        for (int i = 0; i < n; i++) {
            out[i] = grid[0][i];
        }
        return out;
    }

    // n frames side by side, one row, evenly spaced
    static Object[] cutEven(Texture sheet, int n) {
        return cut(sheet, n, sheet.getWidth() / n, sheet.getHeight());
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
        if (playerWalkSheet != null) {
            playerWalkSheet.dispose();
        }
        playerWalkSheet = null;
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
        s.times = new float[] {0.4f, 0.4f};
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

    public void usePlayer(boolean boost, boolean moving) {
        if (boost) {
            setAnim(playerBoostFrames, new float[] {0.1f, 0.1f, 0.1f, 0.1f});  // 4 run frames
        } else if (moving) {
            setAnim(playerWalkFrames, new float[] {0.12f, 0.12f, 0.12f, 0.12f});
        } else {
            setAnim(playerFrames, new float[] {0.4f, 0.4f});
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
