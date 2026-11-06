package goosepatrol;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
public class GUI {
    BitmapFont font;
    BitmapFont big;
    public GUI() {
        // default font  no .fnt file
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.15f);
        big = new BitmapFont();
        big.setColor(Color.WHITE);
        // 1.15 body  1.6 titles
        big.getData().setScale(1.6f);
    }
    public void renderHotbar(ShapeRenderer shapes, GameManager game) {
        // two boxes  yellow ring on the selected one
        if (game.map == null) {
            return;
        }
        float hx = 20;
        for (int i = 0; i < game.map.player.inventory.length; i++) {
            if (i == game.map.player.selected) {
                shapes.setColor(0.95f, 0.85f, 0.25f, 1);
                shapes.rect(hx - 4, 6, 40, 40);
            }
            shapes.setColor(0.18f, 0.18f, 0.22f, 1);
            // 32 box  48 apart
            shapes.rect(hx, 10, 32, 32);
            hx += 48;
        }
    }
    public void renderHotbarIcons(SpriteBatch batch, GameManager game) {
        // 1 and 2 pick the slot  number on the picture is uses left
        if (game.map == null) {
            return;
        }
        float hx = 20;
        font.setColor(Color.WHITE);
        for (int i = 0; i < game.map.player.inventory.length; i++) {
            Item it = game.map.player.inventory[i];
            if (it != null && it.texture != null) {
                batch.draw(it.texture, hx + 2, 14, 28, 22);
                font.draw(batch, String.valueOf(it.uses), hx + 20, 24);
            }
            font.draw(batch, String.valueOf(i + 1), hx + 12, 54);
            hx += 48;
        }
    }
    public void renderUI(SpriteBatch batch, GameManager game) {
        // timer  counters  pause text  stays on the screen
        if (game.map == null) {
            return;
        }
        int secs = (int) game.timeElapsed;
        font.draw(batch, "time " + secs + " / 300", 10, 468);
        font.draw(batch, "items used " + game.itemsUsed + "   events " + game.eventsHit, 160, 468);
        font.draw(batch, "health " + game.map.player.health + "   stamina " + (int) game.map.player.stamina, 420, 468);
        font.draw(batch, "path bends  yellow is the way out", 10, 448);
        font.draw(batch, "1/2 select   E consume", 50, 32);
        if (game.lastEvent != null) {
            font.draw(batch, game.lastEvent, 280, 32);
        }
        if (game.afk && !game.escaped && !game.caught && !game.paused) {
            font.draw(batch, "Are you afk?", 340, 280);
        }
        if (game.caught) {
            big.draw(batch, "the geese got you", 250, 260);
            font.draw(batch, "R to play again", 330, 230);
        } else if (game.escaped) {
            big.draw(batch, "you got out", 300, 260);
            font.draw(batch, "R to play again", 330, 230);
        } else if (game.timeElapsed >= 300) {
            big.draw(batch, "time up", 330, 260);
            font.draw(batch, "R to play again", 330, 230);
        } else if (game.paused) {
            big.draw(batch, "PAUSED", 330, 300);
            font.draw(batch, "SPACE to resume     1/2 then E     R reset     WASD", 150, 262);
        }
    }
    public void renderWorld(SpriteBatch batch, GameManager game) {
        // these sit on the map so they move with the camera
        if (game.map == null) {
            return;
        }
        font.setColor(Color.BLACK);
        font.draw(batch, "EXIT", game.map.exit[0] + 40, game.map.exit[1] + 40);
        for (int i = 0; i < game.map.worldItems.size(); i++) {
            Item it = game.map.worldItems.get(i);
            if (!it.taken) {
                font.draw(batch, it.translation, it.x - 2, it.y + 34);
            }
        }
        font.setColor(Color.WHITE);
    }
    public void renderMenu(SpriteBatch batch) {
        // super basic so start is not just two buttons
        big.draw(batch, "GOOSE PATROL", 270, 430);
        font.draw(batch, "START", 370, 332);
        font.draw(batch, "SETTINGS", 352, 267);
        font.draw(batch, "WASD  walk", 300, 200);
        font.draw(batch, "follow the brown path  yellow EXIT is the way out", 160, 172);
        font.draw(batch, "geese chase you and take health if they touch you", 155, 144);
        font.draw(batch, "press 1 for food  walk into the goose  press E to feed it", 130, 116);
        font.draw(batch, "click START or press enter", 270, 78);
    }
    public void renderSettings(SpriteBatch batch) {
        // keys only  no volume slider
        big.draw(batch, "SETTINGS", 320, 400);
        font.draw(batch, "WASD  move", 300, 340);
        font.draw(batch, "1 and 2  select hotbar slot", 300, 310);
        font.draw(batch, "E  consume selected item", 300, 280);
        font.draw(batch, "SPACE  pause once you are in the game", 300, 250);
        font.draw(batch, "R  reset the round", 300, 220);
        font.draw(batch, "click START on the menu to play", 300, 190);
        font.draw(batch, "no extra volume or graphics options for A1", 220, 150);
    }
    public void dispose() {
        // closing the window
        font.dispose();
        big.dispose();
    }
}
