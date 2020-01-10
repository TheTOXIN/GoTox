package com.toxin.gotox.player;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.boontaran.douglasPeucker.DouglasPeucker;
import com.boontaran.games.ActorClip;
import com.boontaran.marchingSquare.MarchingSquare;
import com.toxin.gotox.GoTox;
import com.toxin.gotox.Setting;
import com.toxin.gotox.levels.Level;

import java.util.ArrayList;

public class Player extends ActorClip implements IBody {

    private Image roverImg, astronautImg, astronautFallImg, frontWheelImage, rearWheelImg;

    private Group frontWheelCont, rearWheelCont, astronautFallCont;

    public Body rover, frontWheel, rearWheel, astronaut;

    private Joint frontWheelJoint, rearWheelJoint, astroJoint;

    private World world;

    private boolean hasDestroyed = false;
    private boolean destroyOnNextUpdate = false;

    private boolean isTouchGround = true;

    private float jumpImpulse = Setting.JUMP_IMPULSE;
    private float jumpWait = 0;

    private Level level;

    public Player(Level level) {
        this.level = level;

        roverImg = new Image(GoTox.atlas.findRegion("rover"));
        childs.addActor(roverImg);
        roverImg.setX(-roverImg.getWidth() / 2);

        roverImg.setY(-15);

        astronautImg = new Image(GoTox.atlas.findRegion("astronaut"));
        childs.addActor(astronautImg);

        astronautImg.setX(-35);
        astronautImg.setY(20);

        astronautFallCont = new Group();

        astronautFallImg = new Image(GoTox.atlas.findRegion("astronaut_fall"));
        astronautFallCont.addActor(astronautFallImg);

        astronautFallImg.setX(-astronautFallImg.getWidth() / 2);
        astronautFallImg.setY(-astronautFallImg.getHeight() / 2);

    }

    public void touchGround() {
        isTouchGround = true;
    }

    public boolean isTouchedGround() {
        if (jumpWait > 0) return false;
        return isTouchGround;
    }

    @Override
    public Body createBody(World world) {
        this.world = world;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;

        float[] vertices = traceOutline("rover_model");
        Vector2 centroid = Level.calculateCentroid(vertices);

        int i = 0;
        while (i < vertices.length) {
            vertices[i] -= centroid.x;
            vertices[i + 1] -= centroid.y;
            i += 2;
        }

        vertices = DouglasPeucker.simplify(vertices, 4);
        Level.scaleToWorld(vertices);
        Array<Polygon> triangles = Level.getTriangles(new Polygon(vertices));
        rover = createBodyFromTriangles(world, triangles);
        rover.setTransform((getX()) / Level.WORLD_SCALE, (getY()) / Level.WORLD_SCALE, 0);

        frontWheel = createWheel(world, 20 / Level.WORLD_SCALE);
        frontWheel.setTransform(rover.getPosition().x + 60 / Level.WORLD_SCALE, rover.getPosition().y - 8 / Level.WORLD_SCALE, 0);

        frontWheelCont = new Group();
        frontWheelImage = new Image(GoTox.atlas.findRegion("front_wheel"));

        frontWheelCont.addActor(frontWheelImage);
        frontWheelImage.setX(-frontWheelImage.getWidth() / 2);
        frontWheelImage.setY(-frontWheelImage.getHeight() / 2);

        getParent().addActor(frontWheelCont);

        UserData data = new UserData();
        data.actor = frontWheelCont;
        frontWheel.setUserData(data);

        RevoluteJointDef rDef = new RevoluteJointDef();
        rDef.initialize(rover, frontWheel, new Vector2(frontWheel.getPosition()));
        frontWheelJoint = world.createJoint(rDef);

        rearWheel = createWheel(world, 20 / Level.WORLD_SCALE);
        rearWheel.setTransform(rover.getPosition().x - 62 / Level.WORLD_SCALE, rover.getPosition().y - 8 / Level.WORLD_SCALE, 0);
        rDef = new RevoluteJointDef();

        rearWheelCont = new Group();
        rearWheelImg = new Image(GoTox.atlas.findRegion("rear_wheel"));
        rearWheelCont.addActor(rearWheelImg);
        rearWheelImg.setX(-rearWheelImg.getWidth() / 2);
        rearWheelImg.setY(-rearWheelImg.getHeight() / 2);

        getParent().addActor(rearWheelCont);
        data = new UserData();
        data.actor = rearWheelCont;
        rearWheel.setUserData(data);

        rDef.initialize(rover, rearWheel, new Vector2(rearWheel.getPosition()));
        rearWheelJoint = world.createJoint(rDef);

        vertices = traceOutline("astronaut_model");
        centroid = Level.calculateCentroid(vertices);

        i = 0;
        while (i < vertices.length) {
            vertices[i] -= centroid.x;
            vertices[i + 1] -= centroid.y;

            i += 2;
        }
        vertices = DouglasPeucker.simplify(vertices, 6);
        Level.scaleToWorld(vertices);
        triangles = Level.getTriangles(new Polygon(vertices));
        astronaut = createBodyFromTriangles(world, triangles);
        astronaut.setTransform(rover.getPosition().x - 0 / Level.WORLD_SCALE, rover.getPosition().y + 30 / Level.WORLD_SCALE, 0);

        WeldJointDef actronautDef = new WeldJointDef();
        actronautDef.initialize(rover, astronaut, new Vector2(astronaut.getPosition()));
        astroJoint = world.createJoint(actronautDef);

        return rover;
    }

    private Body createWheel(World world, float rad) {
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;
        def.angularDamping = 1f;

        Body body = world.createBody(def);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(rad);

        fDef.shape = shape;
        fDef.restitution = 0.5f;
        fDef.friction = 0.4f;
        fDef.density = 1;

        body.createFixture(fDef);
        shape.dispose();

        return body;
    }

    private float[] traceOutline(String regionName) {
        Texture bodyOutLine = GoTox.atlas.findRegion(regionName).getTexture();
        TextureAtlas.AtlasRegion reg = GoTox.atlas.findRegion(regionName);

        int w = reg.getRegionWidth();
        int h = reg.getRegionHeight();
        int x = reg.getRegionX();
        int y = reg.getRegionY();

        bodyOutLine.getTextureData().prepare();
        Pixmap allPixmap = bodyOutLine.getTextureData().consumePixmap();

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(allPixmap, 0, 0, x, y, w, h);

        allPixmap.dispose();

        int pixel;

        w = pixmap.getWidth();
        h = pixmap.getHeight();

        int[][] map;
        map = new int[w][h];
        for (x = 0; x < w; x++) {
            for (y = 0; y < h; y++) {
                pixel = pixmap.getPixel(x, y);
                if ((pixel & 0x000000ff) == 0) {
                    map[x][y] = 0;
                } else {
                    map[x][y] = 1;
                }
            }
        }

        pixmap.dispose();

        MarchingSquare ms = new MarchingSquare(map);
        ms.invertY();
        ArrayList<float[]> traces = ms.traceMap();

        return traces.get(0);
    }

    private Body createBodyFromTriangles(World world, Array<Polygon> triangles) {
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;

        Body body = world.createBody(def);

        for (Polygon triangle : triangles) {
            FixtureDef fDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.set(triangle.getTransformedVertices());

            fDef.shape = shape;
            fDef.restitution = 0.3f;
            fDef.density = 1;

            body.createFixture(fDef);
            shape.dispose();
        }

        return body;
    }

    public void onKey(boolean moveFrontKey, boolean moveBackKey) {
        float torque = Setting.WHEEL_TORQUE;
        float maxAV = 18;

        if (moveFrontKey) {
            if (-rearWheel.getAngularVelocity() < maxAV) {
                rearWheel.applyTorque(-torque, true);
            }
            if (-frontWheel.getAngularVelocity() < maxAV) {
                frontWheel.applyTorque(-torque, true);
            }
        }
        if (moveBackKey) {
            if (rearWheel.getAngularVelocity() < maxAV) {
                rearWheel.applyTorque(torque, true);
            }
            if (frontWheel.getAngularVelocity() < maxAV) {
                frontWheel.applyTorque(torque, true);
            }
        }
    }

    public void jumpBack(float value) {
        if (value < 0.2f) value = 0.2f;

        rover.applyLinearImpulse(0, jumpImpulse * value,
            rover.getWorldCenter().x + 5 / Level.WORLD_SCALE,
            rover.getWorldCenter().y, true
        );

        isTouchGround = false;
        jumpWait = 0.3f;
    }

    public void jumpForward(float value) {
        if (value < 0.2f) value = 0.2f;

        rover.applyLinearImpulse(0, jumpImpulse * value,
            rover.getWorldCenter().x - 4 / Level.WORLD_SCALE,
            rover.getWorldCenter().y, true
        );

        isTouchGround = false;
        jumpWait = 0.3f;
    }

    @Override
    public void act(float delta) {
        if (jumpWait > 0) {
            jumpWait -= delta;
        }

        if (destroyOnNextUpdate) {
            destroyOnNextUpdate = false;

            world.destroyJoint(frontWheelJoint);
            world.destroyJoint(rearWheelJoint);
            world.destroyJoint(astroJoint);
            world.destroyBody(astronaut);

            astronautImg.remove();

            astronautFall();
        }

        super.act(delta);
    }

    private void astronautFall() {
        BodyDef def = new BodyDef();

        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;
        def.angularDamping = 0;

        def.position.x = astronaut.getPosition().x;
        def.position.y = astronaut.getPosition().y;
        def.angle = getRotation() * 3.1416f / 180;
        def.angularVelocity = astronaut.getAngularVelocity();

        Body body = world.createBody(def);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / Level.WORLD_SCALE);

        fDef.shape = shape;
        fDef.restitution = 0.5f;
        fDef.friction = 0.4f;
        fDef.density = 1;
        fDef.isSensor = true;

        body.createFixture(fDef);

        body.setLinearVelocity(astronaut.getLinearVelocity());

        shape.dispose();

        level.addChild(astronautFallCont);
        astronautFallCont.setPosition(getX(), getY());

        UserData data = new UserData();
        data.actor = astronautFallCont;
        body.setUserData(data);
    }

    public void destroy() {
        if (hasDestroyed) return;
        hasDestroyed = true;

        destroyOnNextUpdate = true;
    }

    public boolean isHasDestroyed() {
        return hasDestroyed;
    }
}