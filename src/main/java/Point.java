package main.java;

public class Point {

    private final double x;
    private final double y;
    private final Double overrideAngle;
    private final String name;

    public Point(WayPoint wayPoint){
        this.name = wayPoint.getName();
        this.x = wayPoint.getXInches();
        this.y = wayPoint.getYInches();
        this.overrideAngle = wayPoint.getOverrideAngle();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Double getOverrideAngle() {
        return overrideAngle;
    }

    public String getName() {
        return name;
    }
}
