package goosepatrol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
public class InputManager {
    PlayerListener listener;
    public InputManager(PlayerListener listener) {
        // keys go to the listener not straight to the player
        this.listener = listener;
    }
    public void keyPressed() {
        // held keys  check every frame not just the tap
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            listener.onInputAction("left");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            listener.onInputAction("right");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            listener.onInputAction("up");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            listener.onInputAction("down");
        }
        // E consume  justPressed so it doesnt fire every frame
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            listener.onInputAction("use");
        }
    }
    public void keyReleased() {
        // let go  stop that way
        boolean horiz = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean vert = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)
            || Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if (!horiz) {
            listener.onInputAction("idle-x");
        }
        if (!vert) {
            listener.onInputAction("idle-y");
        }
    }
    public boolean slotTapped(int slot) {
        // 1 and 2 pick the hotbar
        if (slot == 0) {
            return Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)
                || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1);
        }
        if (slot == 1) {
            return Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)
                || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2);
        }
        return false;
    }
    public boolean spaceTapped() {
        // pause tap  not held
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }
    public boolean rTapped() {
        // remake the map
        return Gdx.input.isKeyJustPressed(Input.Keys.R);
    }
}
