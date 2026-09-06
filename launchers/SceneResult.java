package launcher;

import physics.PhysicsWorld;
import render.Camera;
import scene.GameObject;

import java.util.List;

public class SceneResult {
    public PhysicsWorld world;
    public List<GameObject> objects;
    public Camera camera;
    public GameObject tracked;
    public GameObject relativeTo;// nullable — fine if a scenario has nothing to compare against
    public physics.PeriodTracker periodTracker; // nullable -- most scenarios don't track a period
    public physics.SpringConstraint trackedSpring; // nullable -- most scenarios have no spring
    public SceneResult(PhysicsWorld world, List<GameObject> objects, Camera camera, GameObject tracked, GameObject relativeTo) {
        this.world = world;
        this.objects = objects;
        this.camera = camera;
        this.tracked = tracked;
        this.relativeTo = relativeTo;
    }
}