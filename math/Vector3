package math;

import java.text.DecimalFormat;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(
                x + other.x,
                y + other.y,
                z + other.z
        );
    }
    public Vector3 subtract(Vector3 other){
        return new Vector3(
                x - other.x,
                y - other.y,
                z - other.z
        );
    }
    public Vector3 multiply(double scalar) {
        return new Vector3(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }
    public Vector3 divide(double scaler){
        return new Vector3(
                x / scaler,
                y / scaler,
                z / scaler
        );
    }
    public double magnitude(){
        return Math.sqrt(x*x + y*y + z*z);
    }
    public double magnitude_square(){
        return x*x + y*y + z*z;
    }
    public Vector3 normalize(){
       double mag = magnitude();
       if(mag == 0){
           return new Vector3 (0,0,0);
       }
       return divide(mag);
    }
    public Vector3 cross(Vector3 other) {
        return new Vector3(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }
    public double dot(Vector3 other){
        return x*other.x+y*other.y+z*other.z;
    }
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        return "(" + df.format(x) + ", "
                + df.format(y) + ", "
                + df.format(z) + ")";
    }
}
