/**
 * Created by Samuel on 2014/11/5.
 */
package org.sunspotworld;

public class Bilateration extends Trilateration{
    public Bilateration(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2, x2, y2);
    }

    public Location getLocationFromDistance(double r1, double r2){
        return getLocationFromDistance(r1, r2, r2);
    }
}
