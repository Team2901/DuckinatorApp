public class FieldUtils {

    public final static int FIELD_MEASUREMENT_PIXELS = 510;
    public final static int FIELD_MEASUREMENT_INCHES = 144;

    public static double getTargetAngle(final WayPoint wayPoint1, final WayPoint wayPoint2) {
        double dx = wayPoint2.getXPoint() - wayPoint1.getXPoint();
        double dy = wayPoint1.getYPoint() - wayPoint2.getYPoint();
        double targetAnglePi = Math.atan2(dy, dx);
        double targetAngle = ((targetAnglePi*180)/Math.PI);
        return normalizeAngle(targetAngle);
    }

    public static double getLineLength(final WayPoint wayPoint1, final WayPoint wayPoint2) {
        double dx = wayPoint2.getXPoint() - wayPoint1.getXPoint();
        double dy = wayPoint2.getYPoint() - wayPoint1.getYPoint();
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public static double convertToInches(final double pixelValue) {
        double conversionFactorPixelInch = ((double) FIELD_MEASUREMENT_INCHES / (double) FIELD_MEASUREMENT_PIXELS);
        return pixelValue * conversionFactorPixelInch;
    }

    public static double convertToPixels(final double inchesValue) {
        double conversionFactorInchPixel = ((double) FIELD_MEASUREMENT_PIXELS / (double) FIELD_MEASUREMENT_INCHES);
        return inchesValue * conversionFactorInchPixel;
    }

    public static double normalizeAngle(final double angle) {
        return ((angle + 180) % 360) - 180;
    }
}
