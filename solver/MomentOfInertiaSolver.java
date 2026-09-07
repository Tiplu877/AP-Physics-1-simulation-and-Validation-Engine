package solver;

public class MomentOfInertiaSolver {

    // Solid sphere rotating about an axis through its center
    public static double solidSphere(double mass, double radius) {
        return 0.4 * mass * radius * radius; // (2/5) m r^2
    }

    // Thin-walled hollow sphere (shell)
    public static double hollowSphere(double mass, double radius) {
        return (2.0 / 3.0) * mass * radius * radius;
    }

    // Solid disk or cylinder, rotating about its central axis
    public static double solidDisk(double mass, double radius) {
        return 0.5 * mass * radius * radius;
    }

    // Thin hoop or hollow cylinder, rotating about its central axis
    // (all mass concentrated at the radius, hence no factor -- this is the largest
    // I for a given mass/radius, since the mass is as far from the axis as possible)
    public static double hoop(double mass, double radius) {
        return mass * radius * radius;
    }

    // Thin rod, rotating about its center
    public static double rodAboutCenter(double mass, double length) {
        return (1.0 / 12.0) * mass * length * length;
    }

    // Thin rod, rotating about one end
    public static double rodAboutEnd(double mass, double length) {
        return (1.0 / 3.0) * mass * length * length;
    }

    // Point mass (or approximated as one) at a given distance from the axis --
    // this is also what "I" reduces to for any small object treated as a point,
    // e.g. a ball on a string swinging around a pivot.
    public static double pointMass(double mass, double distanceFromAxis) {
        return mass * distanceFromAxis * distanceFromAxis;
    }
}