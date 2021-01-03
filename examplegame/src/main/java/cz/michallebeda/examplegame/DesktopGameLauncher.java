package cz.michallebeda.examplegame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import cz.michallebeda.diorama.engine.EngineGame;

public class DesktopGameLauncher {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1100, 620);
//        config.setWindowedMode(1920, 1080);
        config.setBackBufferConfig(8, 8, 8, 8, 8, 8, 3);
        config.setWindowSizeLimits(640, 480, 16000, 9000);
        config.setTitle("Editor");
        config.useOpenGL3(true, 3, 3);
        new Lwjgl3Application(new EngineGame(), config);
    }
}
