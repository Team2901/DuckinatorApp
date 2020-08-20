package main.java;

public class Point {

    private final double x;
    private final double y;
    private final String name;

    public Point(WayPoint wayPoint){
        this.name = wayPoint.getName();
        this.x = wayPoint.getXInches();
        this.y = wayPoint.getYInches();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }


    public String getName() {
        return name;
    }
}
