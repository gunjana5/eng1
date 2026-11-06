package goosepatrol;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
// draws the map and the hud  game manager calls this
public class Renderer {
    ShapeRenderer shapes;
    SpriteBatch batch;
    OrthographicCamera worldCam;
    OrthographicCamera uiCam;
    public void create() {
        // two cameras  world follows me  ui stays put
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false, 800, 480);
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 480);
        uiCam.position.set(400, 240, 0);
        uiCam.update();
    }
    public void resize(int width, int height) {
        // game stays 800x480  window can stretch
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        worldCam.viewportWidth = 800;
        worldCam.viewportHeight = 480;
        uiCam.setToOrtho(false, 800, 480);
        uiCam.position.set(400, 240, 0);
        uiCam.update();
    }
    public void render(GameManager game) {
        // menu / settings / fail first  then the park
        Gdx.gl.glClearColor(0.12f, 0.16f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (game.screen.equals("fail")) {
            drawFail(game);
            return;
        }
        if (game.screen.equals("menu")) {
            drawMenu(game);
            return;
        }
        if (game.screen.equals("settings")) {
            drawSettings(game);
            return;
        }
        if (game.map == null) {
            return;
        }
        // pov follows me  window stays 800x480  world is bigger
        worldCam.position.set(game.map.player.x + game.map.player.w / 2, game.map.player.y + game.map.player.h / 2, 0);
        worldCam.update();
        shapes.setProjectionMatrix(worldCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        game.map.draw(shapes, worldCam.position.x, worldCam.position.y, 800, 480);
        game.map.player.draw(shapes);
        for (int i = 0; i < game.map.enemies.size(); i++) {
            game.map.enemies.get(i).draw(shapes);
        }
        shapes.end();
        batch.setProjectionMatrix(worldCam.combined);
        batch.begin();
        game.map.drawItems(batch);
        game.map.drawGeese(batch);
        // me last so the hand item sits in front of a goose when you feed
        game.map.drawPlayer(batch);
        game.gui.renderWorld(batch, game);
        batch.end();
        shapes.setProjectionMatrix(uiCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.12f, 0.18f, 0.12f, 1);
        // hud strip  y 430
        shapes.rect(0, 430, 800, 50);
        game.gui.renderHotbar(shapes, game);
        shapes.end();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        game.gui.renderHotbarIcons(batch, game);
        game.gui.renderUI(batch, game);
        batch.end();
    }
    void drawMenu(GameManager game) {
        // start + settings + how to play
        shapes.setProjectionMatrix(uiCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.18f, 0.28f, 0.18f, 1);
        shapes.rect(0, 0, 800, 480);
        // start button  higher so the how to play lines fit under it
        shapes.setColor(0.2f, 0.55f, 0.28f, 1);
        shapes.rect(300, 300, 200, 50);
        shapes.setColor(0.22f, 0.22f, 0.28f, 1);
        shapes.rect(300, 235, 200, 50);
        shapes.end();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        game.gui.renderMenu(batch);
        batch.end();
    }
    void drawSettings(GameManager game) {
        // dark screen  back at 300 80
        shapes.setProjectionMatrix(uiCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.16f, 0.16f, 0.2f, 1);
        shapes.rect(0, 0, 800, 480);
        shapes.setColor(0.22f, 0.22f, 0.28f, 1);
        shapes.rect(300, 80, 200, 44);
        shapes.end();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        game.gui.renderSettings(batch);
        game.gui.font.draw(batch, "BACK", 375, 108);
        batch.end();
    }
    void drawFail(GameManager game) {
        // load died  retry
        shapes.setProjectionMatrix(uiCam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.2f, 0.1f, 0.1f, 1);
        shapes.rect(0, 0, 800, 480);
        shapes.setColor(0.45f, 0.18f, 0.18f, 1);
        shapes.rect(300, 160, 200, 50);
        shapes.end();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        game.gui.big.draw(batch, "game could not load up", 210, 300);
        game.gui.font.draw(batch, "tried 3 times", 340, 250);
        game.gui.font.draw(batch, "RETRY", 370, 192);
        batch.end();
    }
    public boolean tapIn(float x, float y, float w, float h) {
        // mouse in game coords  y is flipped
        if (!Gdx.input.justTouched()) {
            return false;
        }
        int gw = Math.max(1, Gdx.graphics.getWidth());
        int gh = Math.max(1, Gdx.graphics.getHeight());
        float mx = Gdx.input.getX() * 800f / gw;
        float my = 480f - Gdx.input.getY() * 480f / gh;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
    public void dispose() {
        // closing the window
        shapes.dispose();
        batch.dispose();
    }
}
