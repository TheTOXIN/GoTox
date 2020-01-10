package com.toxin.gotox.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.boontaran.games.StageGame;
import com.toxin.gotox.GoTox;

import java.lang.String;

public class Intro extends StageGame {

    public static final int ON_PLAY = 1;
    public static final int ON_BACK = 2;

    private Image title, logo;
    private ImageButton playBtn;

    public Intro() {
        Image bg = new Image(GoTox.atlas.findRegion("intro_bg"));
        addBackground(bg, true, false);

        logo = new Image(GoTox.atlas.findRegion("logo"));
        addChild(logo);

        logo.moveBy(693, 357);

        title = new Image(GoTox.atlas.findRegion("title"));
        addChild(title);

        title.setPosition(20, 525);

        MoveByAction move = new MoveByAction();
        move.setAmount(0, -title.getHeight() * 1.5f);
        move.setDuration(0.4f);
        move.setInterpolation(Interpolation.swingOut);
        move.setActor(title);

        title.addAction(Actions.delay(0.5f, move));

        playBtn = new ImageButton(
            new TextureRegionDrawable(GoTox.atlas.findRegion("play_btn")),
            new TextureRegionDrawable(GoTox.atlas.findRegion("play_btn_down"))
        );

        addChild(playBtn);
        //centerActorXY(playBtn);
        playBtn.moveBy(210, 240);

        AlphaAction alphaAction = new AlphaAction();
        alphaAction.setActor(playBtn);
        alphaAction.setDuration(0.8f);
        alphaAction.setAlpha(1f);

        playBtn.setColor(1, 1, 1, 0);
        playBtn.addAction(Actions.delay(0.8f, alphaAction));
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playBtn.clearActions();
                onClickPlay();
                GoTox.media.playSound("click.ogg");
            }
        });
    }

    private void onClickPlay() {
        playBtn.setTouchable(Touchable.disabled);
        playBtn.addAction(Actions.alpha(0, 1f));
        title.addAction(Actions.moveTo(title.getX(), getHeight(), 0.5f, Interpolation.swingIn));

        delayCall("delay1", 0.7f);
    }

    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            return true;
        }

        return super.keyUp(keycode);
    }

    protected void onDelayCall(String code) {
        if (code.equals("delay1")) {
            call(ON_PLAY);
        }
    }


}
