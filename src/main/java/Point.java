package main.java;

public class Point {

    public double x;
    public double y;

    public Point() {
        this(0, 0);
    }

    public Point(Point p) {
        this(p.x, p.y);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return (double)this.x;
    }

    public double getY() {
        return (double)this.y;
    }

}
