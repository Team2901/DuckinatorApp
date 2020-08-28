package main.java;

public class Point {

    private final double x;
    private final double y;
    private final double angle;
    private final String name;

    public Point(WayPoint wayPoint){
        this.name = wayPoint.getName();
        this.x = wayPoint.getXInches();
        this.y = wayPoint.getYInches();
        this.angle = wayPoint.getZAngle();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getAngle() {
        return angle;
    }

    public String getName() {
        return name;
    }
}
