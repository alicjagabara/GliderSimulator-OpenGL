package Engine.Entitys;

import Engine.Textures.TextureModel;
import Engine.models.ModelWithTexture;
import Engine.renderEngine.DisplayManager;
import Engine.renderEngine.OBJLoader;
import Engine.renderEngine.VAOsLoader;
import Engine.terrains.Terrain;
import GilterSimulator.TerrainManager;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

@Getter
@Setter
public class Plane extends Entity {

    public static final int STARTING_ALTITUDE = 100;
    //used
    private static final float MAX_ROLL = 20f;
    private static final float ROLL_SPEED = 0.5f;
    private static final float MAX_TURN_SPEED = 20f;
    private static final float GRAVITY_DROP_PER_SECOND = 0.0f;
    private static final float MAX_PITCH = 10f;
    private static final float PITCH_SPEED = 0.2f;
    private static final float PITCH_COEFF = 0.55f;
    //not used
    private static final float WIND_SUPPRESS = 0.9f;
    private static final float NORMAL_SUPPRESS = 2f;
    private static final float FAST_SUPPRESS = 4f;
    private static final float MAX_SPEED = 40f;
    //altitude of the lowest vertex of the model with initializing plane y on 0: lowest_y_coordinate*scale
    //TODO real-physic plane movement
    private float current_speed = 10f;
    private float current_turn_speed = 0;
    private float current_vertical_wind_speed = 0;


    public Plane() throws IOException {

        super(new ModelWithTexture(OBJLoader.loadObjModel("plane",
                VAOsLoader.getInstance()), new TextureModel(10, 2, VAOsLoader
                .getInstance()
                .loadTextureFromJPG("plane_textures"))), new Vector3f(0, Plane.STARTING_ALTITUDE, 0), 0, 0, 0, 0.3f);
    }

    public void move() {
        checkInputs();
        super.increaseRotation(0, current_turn_speed * DisplayManager.getFrameTimeSec(), 0);
        float distance = current_speed * DisplayManager.getFrameTimeSec();
        float dx =
                (float) (distance * Math.sin(Math.toRadians(super.getRotY()) * Math.cos(Math.toRadians(super.getRotX()))));
        float dz =
                (float) (distance * Math.cos(Math.toRadians(super.getRotY()) * Math.cos(Math.toRadians(super.getRotX()))));
        float dy =
                (float) (GRAVITY_DROP_PER_SECOND + current_vertical_wind_speed + Math.sin(super.getRotX()) * PITCH_COEFF) * DisplayManager
                .getFrameTimeSec();
        super.increasePosition(dx, dy, dz);
        Terrain terrain = super.getCurrentTerrain(TerrainManager.getInstance().getTerrains());
        if ((terrain == null)) {
            System.out.println("Out of the Map");
        }
        assert terrain != null;
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x , super.getPosition().z);
        if(super.getPosition().y < terrainHeight){
            super.getPosition().y = terrainHeight;
        }
    }


    public void checkInputs() {

        if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            super.setRotX(Math.min(super.getRotX() + PITCH_SPEED, MAX_PITCH));

        } else if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            super.setRotX(Math.max(super.getRotX() - PITCH_SPEED, -MAX_PITCH));

        } else {
            if (super.getRotX() > 0) super.setRotX(Math.max(super.getRotX() - PITCH_SPEED, 0));
            else super.setRotX(Math.min(super.getRotX() + PITCH_SPEED, 0));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            super.setRotZ(Math.min(super.getRotZ() + ROLL_SPEED, MAX_ROLL));


        } else if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            super.setRotZ(Math.max(super.getRotZ() - ROLL_SPEED, -MAX_ROLL));
        } else {
            if (super.getRotZ() > 0) super.setRotZ(Math.max(super.getRotZ() - ROLL_SPEED, 0));
            else super.setRotZ(Math.min(super.getRotZ() + ROLL_SPEED, 0));
        }
        current_turn_speed = -MAX_TURN_SPEED * super.getRotZ() / MAX_ROLL;

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            current_speed = Math.min(current_speed + 0.5f, MAX_SPEED);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            current_speed = Math.max(-5, current_speed - 0.2f);
        } else {
            if (current_speed > 0) {
                current_speed = Math.max(0, current_speed - 0.1f);
            } else {
                current_speed = Math.min(0, current_speed + 0.1f);
            }
        }
    }

}
