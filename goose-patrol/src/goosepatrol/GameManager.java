package goosepatrol;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
public class GameManager extends ApplicationAdapter {
    Map map;
    GUI gui;
    InputManager input;
    Renderer renderer;
    String screen;
    boolean paused;
    float timeElapsed;
    int itemsUsed;
    int eventsHit;
    String lastEvent;
    boolean escaped;
    boolean caught;
    boolean afk;
    public void create() {
        // boot  load pictures then sit on the menu
        renderer = new Renderer();
        renderer.create();
        gui = new GUI();
        if (!loadAssets()) {
            screen = "fail";
            return;
        }
        screen = "menu";
    }
    boolean loadAssets() {
        // load can flake so try 3 times then show fail
        for (int i = 0; i < 3; i++) {
            if (Sprite.tryLoad() && Item.loadTextures()) {
                return true;
            }
        }
        return false;
    }
    public void start() {
        // r calls this so i can replay without quitting
        map = new Map();
        PlayerListener listener = new PlayerListener(map.player);
        map.tracker.listener = listener;
        input = new InputManager(listener);
        paused = false;
        timeElapsed = 0;
        itemsUsed = 0;
        eventsHit = 0;
        lastEvent = null;
        escaped = false;
        caught = false;
        afk = false;
        screen = "play";
    }
    public void render() {
        // every frame  pause skips update  still draws
        float dt = Gdx.graphics.getDeltaTime();
        handleScreenInput();
        if (screen.equals("play") && map != null && input != null) {
            if (input.rTapped()) {
                start();
            } else if (input.spaceTapped()) {
                // pause  not on the menu  timer 300s
                if (!escaped && !caught && timeElapsed < 300) {
                    paused = !paused;
                }
            }
            if (!paused) {
                update(dt);
            }
        }
        renderer.render(this);
    }
    void handleScreenInput() {
        // menu and settings clicks  play uses wasd instead
        if (screen.equals("menu")) {
            // start button  300 300 200 50
            boolean startBtn = renderer.tapIn(300, 300, 200, 50)
                || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
            if (startBtn) {
                start();
                return;
            }
            if (renderer.tapIn(300, 235, 200, 50)) {
                screen = "settings";
            }
            return;
        }
        if (screen.equals("settings")) {
            if (renderer.tapIn(300, 80, 200, 44) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                screen = "menu";
            }
            return;
        }
        if (screen.equals("fail")) {
            if (renderer.tapIn(300, 160, 200, 50) || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                if (loadAssets()) {
                    screen = "menu";
                }
            }
        }
    }
    public void update(float dt) {
        // timer 300s
        if (timeElapsed >= 300) {
            paused = true;
            return;
        }
        if (caught) {
            return;
        }
        float oldX = map.player.x;
        float oldY = map.player.y;
        map.player.beginFrame(dt);
        // 1/2 select  E is in keyPressed
        if (input.slotTapped(0)) {
            map.player.selectSlot(0);
        }
        if (input.slotTapped(1)) {
            map.player.selectSlot(1);
        }
        input.keyPressed();
        input.keyReleased();
        if (map.player.justUsed) {
            itemsUsed++;
            lastEvent = "used " + map.player.lastUsedId;
            map.player.justUsed = false;
        }
        map.update(dt);
        if (map.tracker != null) {
            afk = map.tracker.afk;
        }
        Item got = map.touchingItem();
        if (got != null) {
            map.player.pickUp();
            lastEvent = "picked up " + got.translation + "  1/2 then E";
        }
        Goose bump = map.overlappingGoose();
        if (bump != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // keyPressed ran E before you moved so food needs this second go
            map.player.useItem();
            if (map.player.justUsed) {
                itemsUsed++;
                lastEvent = "used " + map.player.lastUsedId;
                map.player.justUsed = false;
            }
            bump = map.overlappingGoose();
        }
        if (bump != null) {
            // colliding takes health
            if (map.player.hitByGoose()) {
                lastEvent = "goose got you  health " + map.player.health;
                if (map.player.health <= 0) {
                    caught = true;
                    paused = true;
                    lastEvent = "the geese got you";
                }
            }
            map.undoPlayer(oldX, oldY);
        } else if (map.checkCollisions()) {
            map.undoPlayer(oldX, oldY);
        }
        for (int i = 0; i < map.events.size(); i++) {
            ProximityEvent e = map.events.get(i);
            if (!e.triggered && e.inRange(map.player)) {
                e.triggered = true;
                eventsHit++;
                if (e.id.equals("longboi")) {
                    lastEvent = "long boi showed up  the geese wandered off";
                    for (int g = 0; g < map.enemies.size(); g++) {
                        if (!map.enemies.get(g).fed) {
                            map.enemies.get(g).eat();
                        }
                    }
                } else if (e.id.equals("leftover")) {
                    lastEvent = "leftover food on the side path";
                    // 8 left of the trigger so it sits on the spur
                    map.worldItems.add(new Item("greggs", "food", 3, 1, e.x - 8, e.y));
                }
            }
        }
        if (map.reachedExit()) {
            escaped = true;
            paused = true;
        }
        timeElapsed += dt;
        if (timeElapsed > 300) {
            timeElapsed = 300;
            paused = true;
        }
    }
    public void resize(int width, int height) {
        // window changed  keep 800x480 inside
        if (renderer != null) {
            renderer.resize(width, height);
        }
    }
    public void pause() {
        // os paused us  freeze the round if we are in it
        if ("play".equals(screen)) {
            paused = true;
        }
    }
    public void resume() {
        // window got focus back  do not unpause the round
    }
    public void dispose() {
        // closing the window
        if (renderer != null) {
            renderer.dispose();
        }
        if (gui != null) {
            gui.dispose();
        }
        Item.unloadTextures();
        Sprite.unload();
    }
}
