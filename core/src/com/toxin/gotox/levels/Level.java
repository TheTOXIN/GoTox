package com.toxin.gotox.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.boontaran.games.StageGame;

public class Level extends StageGame {

    private String directory;

    public static final float WORLD_SCALE = 30;

    public Level(String directory) {
        this.directory = directory;
    }

    public void addChild(Actor actor) {
        this.stage.addActor(actor);
    }

    public void addChild(Actor actor, float x, float y) {
        this.addChild(actor);
        actor.setX(x);
        actor.setY(y);
    }
}
