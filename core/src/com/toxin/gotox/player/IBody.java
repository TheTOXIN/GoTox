package com.toxin.gotox.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public interface IBody {
    Body createBody(World world);
}