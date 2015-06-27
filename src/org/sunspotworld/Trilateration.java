package org.sunspotworld;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Created by Samuel on 2014/10/27.
 * A, B, C represent 3 sensors that are put in fixed location
 */
public class Trilateration {
    // The 3 sides of the triangle aligned by location-fixed sensors (A, B, C)
    // BC = a, AC = b, AB = c;
//    private double a, b, c;

    // The coordinates of the 3 points of the triangle
    // For simplicity purpose, it is assumed that the 3 points' coordinates will be taken as below:
    // Point A (x1, y1)
    // Point B (x2, y2)
    // Point C (x3, y3)
    private double x1, y1, x2, y2, x3, y3;

    // The distance between the to-be-test sensor and each other location-fixed sensors
    private double r1, r2, r3;

    // Generic situation (based on location)
    public Trilateration(double x1, double y1, double x2, double y2, double x3, double y3){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
    }

    // Get the location of the unkonwn position based on the distance to each fixed sensor (A, B, C)
    public Location getLocationFromDistance(double r1, double r2, double r3){
        double x, y;
//        x = (r1 * r1 - r2 * r2 + x2 * x2) / (2*x2);
////        y = (r1 * r1 - r3 * r3 + b*b - 2 * x * x3) / (2 * y3);
//        y = (r1 * r1 - r3 * r3 - x * x + (x - x3)*(x - x3) + y3 * y3) / (2 * y3);

        double k = (x1 - x2) / (y1 - y2);
        double m = y1 - (x1*x1 - x2*x2 + y1*y1 - y2*y2 - r1*r1 + r2*r2)/(2 *(y1 -y2));
        double aa = k*k + 1;
        double bb = 2*(m*k - x1);
        double cc = x1*x1 + m*m - r1*r1;
        double sq = sqrt(bb*bb - 4*aa*cc);
        double _x1 = (-bb + sq)/(2*aa);
        double _x2 = (-bb - sq)/(2*aa);
        double _y1 = -k*_x1 + y1 - m;
        double _y2 = -k*_x2 + y1 - m;
        double errRange1 = abs((x3 - _x1) * (x3 - _x1) + (y3 - _y1) * (y3 - _y1) - r3 * r3);
        double errRange2 = abs((x3 - _x2) * (x3 - _x2) + (y3 - _y2) * (y3 - _y2) - r3 * r3);

//        System.out.println("("+x1+","+y1+")" + "("+x2+","+y2+")" + "("+x3+","+y3+")");
        if(errRange1 < errRange2){
            x = _x1;
            y = _y1;
        }else{
            x = _x2;
            y = _y2;
        }

//        System.out.println("*** _x1, _y1 = " + _x1 + ", " + _y1);
//        System.out.println("*** _x2, _y2 = " + _x2 + ", " + _y2);
//        System.out.println("*** err1 = " + errRange1);
//        System.out.println("*** err2 = " + errRange2);
        return new Location(x, y);
    }
}
