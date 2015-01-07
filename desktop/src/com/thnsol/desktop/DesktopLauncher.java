package com.thnsol.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thnsol.ThNsOL;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.depth = 32;
		config.title = "Touhou Nyasama Online";
		config.addIcon("icon.png", FileType.Internal);
		new LwjglApplication(new ThNsOL(), config);
	}
}
