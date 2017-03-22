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
import com.badlogic.gdx.utils.I18NBundle;
import com.boontaran.games.StageGame;
import com.toxin.gotox.media.Media;
import com.toxin.gotox.screens.Intro;
import com.toxin.gotox.screens.LevelList;
import com.toxin.gotox.utils.Data;

import java.lang.String;

import java.util.Locale;

public class GoTox extends Game {

	public static final int SHOW_BANNER = 1;
	public static final int HIDE_BANNER = 2;
	public static final int LOAD_INTERSITIAL = 3;
	public static final int SHOW_INTERSITIAL = 4;
	public static final int OPEN_MARKET = 5;
	public static final int SHARE = 6;

	private boolean loadingsAssets = false;

	private AssetManager assetManager;
	private GameCallBack gameCallBack;
	private I18NBundle bundle;
	private String path_to_atlas;
	private Intro intro;
	private LevelList levelList;

	public static TextureAtlas atlas;
	public static BitmapFont font40;
	public static Media media;
	public static Data data;

	public GoTox(GameCallBack gameCallBack) {
		this.gameCallBack = gameCallBack;
	}
	
	@Override
	public void create () {
		StageGame.setAppSize(800, 400);

		Gdx.input.setCatchBackKey(true);

		Locale locale = Locale.getDefault();
		bundle = I18NBundle.createBundle(Gdx.files.internal("MyBundle"), locale);
		path_to_atlas = bundle.get("path");

		loadingsAssets = true;
		assetManager = new AssetManager();
		assetManager.load(path_to_atlas, TextureAtlas.class);
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

		assetManager.load("font40.ttf", BitmapFont.class, sizeParams);

		media = new Media(assetManager);
		data = new Data();
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
		font40 = assetManager.get("font40.ttf", BitmapFont.class);

		showIntro();
	}

	private void exitApp() {
		Gdx.app.exit();
	}

	private void showIntro() {
		intro = new Intro();
		setScreen(intro);

		intro.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				if (code == Intro.ON_PLAY) {
					showLevelList();
					hideIntro();
				} else if (code == Intro.ON_BACK) {
					exitApp();
				}
			}
		});

		media.playMusic("music1.ogg", true);
	}

	private void hideIntro() {
		intro = null;
	}

	private void showLevelList() {
		levelList = new LevelList();
		setScreen(levelList);

		levelList.setCallback(new StageGame.Callback() {
			@Override
			public void call(int code) {
				if (code == LevelList.ON_BACK) {
					showIntro();
					hideLevelList();
				} else if (code == LevelList.ON_LEVEL_SELECTED) {
					//showLevel();
					hideLevelList();
				} else if (code == LevelList.ON_OPEN_MARKET) {
					gameCallBack.sendMessage(OPEN_MARKET);
				} else if (code == LevelList.ON_SHARE) {
					gameCallBack.sendMessage(SHARE);
				}
			}
		});

		gameCallBack.sendMessage(SHOW_BANNER);
		media.playMusic("music1.ogg", true);
	}

	private void hideLevelList() {
		levelList = null;
		gameCallBack.sendMessage(HIDE_BANNER);
	}
}
