package com.toxin.gotox.player;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.sun.org.apache.xpath.internal.operations.String;

public class UserData {

    public Actor actor;
    public String name;

    public UserData(Actor actor, String name) {
        this.actor = actor;
        this.name = name;
    }

    public UserData() {

    }
}