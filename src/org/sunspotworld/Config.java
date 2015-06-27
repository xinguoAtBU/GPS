/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 *
 * @author samueltango
 */
public class Config {
    HashMap<String, Point> sensorPoints;
    int numofsensors;
    int maxScale, xMax, yMax;

    public Config(){
        Properties prop = new Properties();
        sensorPoints = new HashMap<String, Point>();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");

            maxScale = xMax = yMax = 0;
            // load the settings.properties file
            prop.load(input);
            numofsensors = Integer.parseInt(prop.getProperty("num_of_sensors"));
            Point pt = null;
            for(int i=1; i<=numofsensors; ++i){
                pt = new Point( Integer.parseInt(prop.getProperty("x" + i)),  Integer.parseInt(prop.getProperty("y" + i)));
                sensorPoints.put(prop.getProperty("addr" + i), pt);
                if(abs(pt.x)>xMax) xMax = abs(pt.x);
                if(abs(pt.y)>yMax) yMax = abs(pt.y);

                int tmpMax = max(abs(pt.x), abs(pt.y));
                if(tmpMax > maxScale) maxScale = tmpMax;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public HashMap<String, Point> getSensorPoints() {
        return sensorPoints;
    }

    public int getNumofsensors() {
        return numofsensors;
    }

    public int getMaxscale() {
        return maxScale;
    }

    public int getxMax() {
        return xMax;
    }

    public int getyMax() {
        return yMax;
    }
}
