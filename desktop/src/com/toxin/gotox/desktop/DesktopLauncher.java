package com.toxin.gotox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.toxin.gotox.GameCallBack;
import com.toxin.gotox.GoTox;
import com.toxin.gotox.Setting;

public class DesktopLauncher {

    private DesktopLauncher() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = Setting.WIDTH_SCREEN;
        config.height = Setting.HEIGHT_SCREEN;

        GameCallBack gameCallBack = message -> System.out.println("DesktopLauncher sendMessage: " + message);

        new LwjglApplication(
            new GoTox(gameCallBack),
            config
        );
    }

    public static void main(String[] args) {
        new DesktopLauncher();
    }
}
