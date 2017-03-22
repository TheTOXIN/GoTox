package com.toxin.gotox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.toxin.gotox.GameCallBack;
import com.toxin.gotox.GoTox;

public class DesktopLauncher {
	public DesktopLauncher () {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 800;
		config.height = 480;

		new LwjglApplication(new GoTox(gameCallBack), config);
	}

	private GameCallBack gameCallBack = new GameCallBack() {
		@Override
		public void sendMessage(int message) {
			System.out.println("DesktopLauncher sendMessage: " + message);
		}
	};

	public static void main(String[] args) {
		new DesktopLauncher();
	}
}
