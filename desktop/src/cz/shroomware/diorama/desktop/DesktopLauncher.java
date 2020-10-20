package cz.shroomware.diorama.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;
import java.util.Locale;

import cz.shroomware.diorama.editor.EditorEngineGame;

public class DesktopLauncher {
    private static final boolean release = false;
    private static final boolean forceUpdate = false;

    public static void main(String[] arg) {
        if (!release) {
            packAtlas();
            packShadowAtlas();
            packUiAtlas();
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1100, 620);
//        config.setWindowedMode(1920, 1080);
        config.setBackBufferConfig(8, 8, 8, 8, 8, 8, 3);
        config.setWindowSizeLimits(640, 480, 16000, 9000);
        config.setTitle("Editor");
        new Lwjgl3Application(new EditorEngineGame(), config);
    }

    private static void packShadowAtlas() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.paddingX = 16;
        settings.paddingY = 16;
        settings.duplicatePadding = false;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.filterMin = Texture.TextureFilter.Nearest;

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                File atlasFile = new File("atlas\\shadows.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("..\\..\\raw_shadows\\").lastModified()) {
                    TexturePacker.process(settings, "..\\..\\raw_shadows\\", "atlas", "shadows");
                    System.out.println("Shadow atlas: packed");
                } else {
                    System.out.println("Shadow atlas: nothing to pack");
                }
                break;
            default: // workaround, not reporting Linux
                atlasFile = new File("atlas/shadows.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("../../raw_shadows").lastModified()) {
                    TexturePacker.process(settings, "../../raw_shadows", "atlas", "shadows");
                    System.out.println("Shadow atlas: packed");
                } else {
                    System.out.println("Shadow atlas: nothing to pack");
                }
                break;
        }
    }
    private static void packAtlas() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.paddingX = 4;
        settings.paddingY = 4;
        settings.duplicatePadding = true;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.filterMin = Texture.TextureFilter.Nearest;

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                File atlasFile = new File("atlas\\auto.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("..\\..\\raw_assets\\").lastModified()) {
                    TexturePacker.process(settings, "..\\..\\raw_assets\\", "atlas", "auto");
                    System.out.println("Atlas: packed");
                } else {
                    System.out.println("Atlas: nothing to pack");
                }
                break;
            default: // workaround, not reporting Linux
                atlasFile = new File("atlas/auto.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("../../raw_assets").lastModified()) {
                    TexturePacker.process(settings, "../../raw_assets", "atlas", "auto");
                    System.out.println("Atlas: packed");
                } else {
                    System.out.println("Atlas: nothing to pack");
                }
                break;
        }
    }

    private static void packUiAtlas() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.paddingX = 4;
        settings.paddingY = 4;
        settings.duplicatePadding = true;
        settings.filterMag = Texture.TextureFilter.MipMapLinearLinear;
        settings.filterMin = Texture.TextureFilter.MipMapLinearLinear;

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                File atlasFile = new File("atlas\\ui.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("..\\..\\ui_raw_assets\\").lastModified()) {
                    TexturePacker.process(settings, "..\\..\\ui_raw_assets\\", "atlas", "ui");
                    System.out.println("UI Atlas: packed");
                } else {
                    System.out.println("UI Atlas: nothing to pack");
                }
                break;
            default: // workaround, not reporting Linux
                atlasFile = new File("atlas/ui.atlas");
                if (forceUpdate || atlasFile.lastModified() < getLatestFileFromDir("../../ui_raw_assets").lastModified()) {
                    TexturePacker.process(settings, "../../ui_raw_assets", "atlas", "ui");
                    System.out.println("UI Atlas: packed");
                } else {
                    System.out.println("UI Atlas: nothing to pack");
                }
                break;
        }
    }

    private static File getLatestFileFromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }


    public static final class OsCheck {
        // cached result of OS detection
        protected static OSType detectedOS;

        /**
         * detect the operating system from the os.filename System property and cache
         * the result
         *
         * @returns - the operating system detected
         */
        public static OSType getOperatingSystemType() {
            if (detectedOS == null) {
                String OS = System.getProperty("os.filename", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                    detectedOS = OSType.MacOS;
                } else if (OS.indexOf("win") >= 0) {
                    detectedOS = OSType.Windows;
                } else if (OS.indexOf("nux") >= 0) {
                    detectedOS = OSType.Linux;
                } else {
                    detectedOS = OSType.Other;
                }
            }
            return detectedOS;
        }

        /**
         * types of Operating Systems
         */
        public enum OSType {
            Windows, MacOS, Linux, Other
        }
    }
}
