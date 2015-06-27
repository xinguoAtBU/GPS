/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

/**
 *
 * @author Administrator
 */
public class SensorInfo {
    String addr;
    double distance;
 
    public SensorInfo(String addr, double distance) {
        this.addr = addr;
        this.distance = distance;
    }
 
    public String getAddr() {
        return addr;
    }
 
    public double getDistance() {
        return distance;
    }
     
    public void set(String addr, double distance) {
        this.addr = addr;
        this.distance = distance;
    }
}