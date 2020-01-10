package com.toxin.gotox.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.boontaran.games.StageGame;
import com.toxin.gotox.GoTox;
import com.toxin.gotox.Setting;
import com.toxin.gotox.media.LevelIcon;

public class LevelList extends StageGame {

    public static final int ON_BACK = 1;
    public static final int ON_LEVEL_SELECTED = 2;
    public static final int ON_OPEN_MARKET = 3;
    public static final int ON_SHARE = 4;

    private int selectedLevelId = 0;

    private ClickListener iconListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            selectedLevelId = ((LevelIcon)event.getTarget()).getId();
            GoTox.media.playSound("click.ogg");
            call(ON_LEVEL_SELECTED);
        }
    };

    public LevelList() {
        Image bg = new Image(GoTox.atlas.findRegion("intro_bg"));
        addBackground(bg, true, false);

        Group container = new Group();
        addChild(container);

        int row = 4, col = 4;
        float space = 20;

        float iconWidth = 0, iconHeight = 0;
        int id = 1;
        int x, y;

        int progress = GoTox.data.getProgress();

        for (y = 0; y < row; y++) {
            for (x = 0; x < col; x++) {
                LevelIcon icon = new LevelIcon(id);
                container.addActor(icon);

                if (iconWidth == 0) {
                    iconWidth = icon.getWidth();
                    iconHeight = icon.getHeight();
                }

                icon.setX(x * (iconWidth + space));
                icon.setY(((row - 1) -y) * (iconHeight+ space));

                if (id <= progress) {
                    icon.setLock(false);
                }

                if (id == progress) {
                    icon.setHilited();
                }

                if (Setting.DEBUG_GAME) {
                    icon.setLock(false);
                }

                icon.addListener(iconListener);
                id++;
            }
        }

        container.setWidth(col * iconWidth + (col - 1) * space);
        container.setHeight(row * iconHeight + (row - 1) * space);

        container.setX(30);
        container.setY(getHeight() - container.getHeight() - 30);
        container.setColor(1, 1, 1, 0);
        container.addAction(Actions.alpha(1, 0.4f));

        ImageButton rateBtn = new ImageButton(
            new TextureRegionDrawable(GoTox.atlas.findRegion("rate")),
            new TextureRegionDrawable(GoTox.atlas.findRegion("rate_down"))
        );

        addChild(rateBtn);
        rateBtn.setX(getWidth() - rateBtn.getWidth() - 20);
        rateBtn.setY(getHeight() - rateBtn.getHeight() - 20);

        rateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                call(ON_OPEN_MARKET);
            }
        });

        ImageButton shareBtn = new ImageButton(
            new TextureRegionDrawable(GoTox.atlas.findRegion("share")),
            new TextureRegionDrawable(GoTox.atlas.findRegion("share_down"))
        );

        addChild(shareBtn);
        shareBtn.setX(getWidth() - rateBtn.getWidth() - 20);
        shareBtn.setY(20);

        shareBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                call(ON_SHARE);
            }
        });
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            GoTox.media.playSound("click.ogg");
            call(ON_BACK);
            return true;
        }

        return super.keyUp(keycode);
    }

    public int getSelectedLevelId() {
        return selectedLevelId;
    }
}
