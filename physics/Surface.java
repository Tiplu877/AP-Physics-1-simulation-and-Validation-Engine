package physics;

import math.Vector3;

public class Surface {
    public Vector3 point;   // any point on the surface
    public Vector3 normal;  // unit vector pointing away from the surface (into open space)
    public Double minX, maxX; // optional horizontal bounds (null = infinite plane)

    // Rendering-only extras — null for surfaces not meant to be drawn as a finite quad (e.g. default flat ground)
    public Vector3 tangent;
    public Double length;
    public Double width;

    public Surface(Vector3 point, Vector3 normal) {
        this.point = point;
        this.normal = normal.normalize();
    }

    public Surface(Vector3 point, Vector3 normal, double minX, double maxX) {
        this(point, normal);
        this.minX = minX;
        this.maxX = maxX;
    }

    // Perpendicular signed distance from a world point to this surface's plane.
    // Positive = in front of the surface (open space side), negative = penetrating.
    public double distanceTo(Vector3 worldPoint) {
        return worldPoint.subtract(point).dot(normal);
    }

    public boolean isWithinBounds(Vector3 worldPoint) {
        if (minX == null) return true;
        return worldPoint.x >= minX && worldPoint.x <= maxX;
    }

    // Builds an inclined ramp starting at basePoint, rising at angleDegrees above horizontal,
    // extending `length` along the slope and `width` along Z.
    public static Surface incline(Vector3 basePoint, double angleDegrees, double length, double width) {
        double rad = Math.toRadians(angleDegrees);
        Vector3 tangent = new Vector3(Math.cos(rad), Math.sin(rad), 0);
        Vector3 normal = new Vector3(-Math.sin(rad), Math.cos(rad), 0);
        double maxX = basePoint.x + length * Math.cos(rad);

        Surface surface = new Surface(basePoint, normal, basePoint.x, maxX);
        surface.tangent = tangent;
        surface.length = length;
        surface.width = width;
        return surface;
    }
}