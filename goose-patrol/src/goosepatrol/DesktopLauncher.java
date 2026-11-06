package goosepatrol;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.lwjgl.system.Configuration;
public class DesktopLauncher {
    public static void main(String[] args) {
        // mac glfw has to run on the first thread
        // 1.13.1 has no useGlfwAsync so mac only sets glfw_async
        // windows/linux skip it  do not also put -XstartOnFirstThread in gradle
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Goose Patrol");
        // window 800x480  camera follows inside it
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        new Lwjgl3Application(new GameManager(), config);
    }
}
