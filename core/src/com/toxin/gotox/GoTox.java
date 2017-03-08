package com.toxin.gotox;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.boontaran.games.StageGame;

public class GoTox extends Game {
	private boolean loadingsAssets = false;
	private AssetManager assetManager;

	public static TextureAtlas atlas;
	public static BitmapFont myFont;
	
	@Override
	public void create () {
		StageGame.setAppSize(800, 400);

		Gdx.input.setCatchBackKey(true);

		loadingsAssets = true;
		assetManager = new AssetManager();
		assetManager.load("images_ru/pack.atlas", TextureAtlas.class);
		assetManager.load("musics/music1.ogg", Music.class);
		assetManager.load("musics/level_failed.ogg", Music.class);
		assetManager.load("musics/level_win.ogg", Music.class);
		assetManager.load("sounds/level_completed.ogg", Sound.class);
		assetManager.load("sounds/fail.ogg", Sound.class);
		assetManager.load("sounds/click.ogg", Sound.class);
		assetManager.load("sounds/crash.ogg", Sound.class);

		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		FreetypeFontLoader.FreeTypeFontLoaderParameter sizeParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		sizeParams.fontFileName = "fonts/a_FuturicaExtraBlack.ttf";
		sizeParams.fontParameters.size = 40;

		assetManager.load("myFont.ttf", BitmapFont.class, sizeParams);
	}

	@Override
	public void render () {
		if (loadingsAssets) {
			if(assetManager.update()) {
				loadingsAssets = false;
				onAssetsLoaded();
			}
		}
		super.render();
	}
	
	@Override
	public void dispose () {
		assetManager.dispose();
		super.dispose();
	}

	public void onAssetsLoaded() {
		atlas = assetManager.get("images_ru/pack.atlas", TextureAtlas.class);
		myFont = assetManager.get("fonts/a_FuturicaExtraBlack.ttf", BitmapFont.class);
	}

	public void exitApp() {
		Gdx.app.exit();
	}
}
