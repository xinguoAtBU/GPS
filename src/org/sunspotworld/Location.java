/**
 * Created by Samuel on 2014/11/3.
 */
package org.sunspotworld;

public class Location{
    double x, y;
    public Location(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point toPoint(){
        return new Point((int) x, (int)y);
    }
}