package kzclient.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathHelper {

    public float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public double lerp(double a, double b, double t) {
        return (1 - t) * a + t * b;
    }


}
