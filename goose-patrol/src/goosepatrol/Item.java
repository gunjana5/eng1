package goosepatrol;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class Item {
    String id;
    String translation;
    // sausage roll or bottle
    Texture texture;
    float duration;
    int uses;
    boolean consumable;
    float x;
    float y;
    float w;
    float h;
    boolean taken;
    static Texture greggsTex;
    static Texture drinkTex;
    public static boolean loadTextures() {
        // paint them once  reuse
        if (greggsTex != null && drinkTex != null) {
            return true;
        }
        try {
            greggsTex = makeGreggs();
            drinkTex = makeDrink();
            return true;
        } catch (Exception e) {
            unloadTextures();
            return false;
        }
    }
    static Texture makeGreggs() {
        // sausage roll  30 by 16
        Pixmap p = new Pixmap(30, 16, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0);
        p.fill();
        p.setColor(0.9f, 0.74f, 0.48f, 1);
        p.fillRectangle(0, 2, 30, 12);
        p.setColor(0.62f, 0.38f, 0.18f, 1);
        p.fillRectangle(7, 5, 16, 6);
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
        return t;
    }
    static Texture makeDrink() {
        // bottle  16 by 22
        Pixmap p = new Pixmap(16, 22, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0);
        p.fill();
        p.setColor(0.15f, 0.7f, 0.85f, 1);
        p.fillRectangle(2, 4, 12, 18);
        p.setColor(0.1f, 0.35f, 0.55f, 1);
        p.fillRectangle(4, 0, 8, 6);
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
        return t;
    }
    public static void unloadTextures() {
        // game closing
        if (greggsTex != null) {
            greggsTex.dispose();
        }
        if (drinkTex != null) {
            drinkTex.dispose();
        }
        greggsTex = null;
        drinkTex = null;
    }
    public Item(String id, String translation, float duration, int uses, float x, float y) {
        // duration is drink seconds  uses is how many E
        this.id = id;
        this.translation = translation;
        this.duration = duration;
        this.uses = uses;
        this.consumable = true;
        this.x = x;
        this.y = y;
        // world size  hotbar draws smaller
        this.w = 30;
        this.h = 22;
        this.taken = false;
        if (id.equals("speed")) {
            this.texture = drinkTex;
        } else {
            this.texture = greggsTex;
        }
    }
    public boolean touching(Player p) {
        // walk onto it
        return !taken && p.x < x + w && p.x + p.w > x && p.y < y + h && p.y + p.h > y;
    }
    public void draw(SpriteBatch batch) {
        // gone from the path once it is in a pocket
        if (taken || texture == null) {
            return;
        }
        batch.draw(texture, x, y, w, h);
    }
}
