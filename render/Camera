package render;

import math.Vector3;

public class Camera {
    public Vector3 position;
    public Vector3 forward;
    public Vector3 up;
    public Vector3 right;
    public double fov;        // vertical field of view, in degrees
    public double nearPlane;

    public Camera(Vector3 position, Vector3 target, double fov) {
        this.position = position;
        this.fov = fov;
        this.nearPlane = 0.1;

        Vector3 worldUp = new Vector3(0, 1, 0);
        forward = target.subtract(position).normalize();
        right = forward.cross(worldUp).normalize();
        up = right.cross(forward).normalize();
    }

    // Projects a world-space point into camera space (x = right, y = up, z = depth)
    public Vector3 worldToCamera(Vector3 worldPoint) {
        Vector3 relative = worldPoint.subtract(position);
        return new Vector3(
                relative.dot(right),
                relative.dot(up),
                relative.dot(forward)
        );
    }
}
