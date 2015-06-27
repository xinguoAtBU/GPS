/**
 * RSSIReceiver.java
 *
 * Example host application for gathering information transmitted by
 * RSSIBeacons, used for indoor localization.
 *
 * @author Aaron Heuckroth <a.heuckroth@gmail.com>
 */
package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.resources.Resources;
import java.io.*;
import javax.microedition.io.*;

public class RSSIReceiver {

    private static final String RECEIVE_PORT = "59";

    //MUST be the same as the CONFIRM_BYTE from RSSIBeacon.java
    private static final byte RX_CONFIRM_BYTE = 43;
    private static final int BROADCAST_CHANNEL = 15;
    //static radio connection objects
    private static RadiogramConnection rxConnection = null;
    private static Radiogram rxg = null;

    private static final int RSSI_MIN = 0;
    private static final int RSSI_MAX = 40;
    private static final String SENSOR_1_ADDR = "79B0";
    private static final String SENSOR_2_ADDR = "78FB";
    private static final String SENSOR_3_ADDR = "80F5";
    private static final String SENSOR_4_ADDR = "45BB";
    private static final String SENSOR_5_ADDR = "7DF3";
    private static final String SENSOR_6_ADDR = "358F";
    private static final double K_1 = 33.58;
    private static final double B_1 = -289.66;
    private static final double K_2 = 55.14;
    private static final double B_2 = -924.26;
    private static final double K_3 = 22.69;
    private static final double B_3 = -235.32;
    private static final double K_4 = 30.6;
    private static final double B_4 = -708.58;
    private static final double K_5 = 34.35;
    private static final double B_5 = -251.66;
    private static final double K_6 = 25.67;
    private static final double B_6 = -579.14;
    SensorInfo D_first = new SensorInfo(null, 0);
    SensorInfo D_second = new SensorInfo(null, 0);
    SensorInfo D_third = new SensorInfo(null, 0);

    //static radio connection objects
    private static RadiogramConnection rgConnection = null;
    private static Radiogram rg = null;

    /* Setup RadiogramConnection on the RECEIVE_PORT. */
    private static void setupConnection() {
        try {
            IRadioPolicyManager rpm = RadioFactory.getRadioPolicyManager();
            long ourAddr = rpm.getIEEEAddress();
            rpm.setChannelNumber(BROADCAST_CHANNEL);
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));

            rgConnection = (RadiogramConnection) Connector.open("radiogram://:" + RECEIVE_PORT);
            System.out.println("Maxleng for Packet is : " + rgConnection.getMaximumLength());
            rg = (Radiogram) rgConnection.newDatagram(rgConnection.getMaximumLength());

        } catch (IOException ex) {
            System.err.println("Could not open radiogram broadcast connection!");
            System.err.println(ex);
        }
    }

    /* Wait for forwarded transmissions from the RSSIReceiverOnSPOT mote, then
     print out the RSSI and Address data from each transmission.
     */
    public void receiveLoop()throws IOException {
        while (true) {
            try {
                //reset radiograms to clear transmission data
                rxg.reset();              

                //waits for a new transmission on RECEIVE_PORT
                rxConnection.receive(rxg);
                
                //read confirmation byte data from the radiogram
                byte checkByte = rxg.readByte();
                
                //check to see if radiogram is the right type
                if (checkByte == RX_CONFIRM_BYTE) {
                    int rssiVal = - rxg.readInt();
                    String spotAddress = rxg.readUTF().substring(15);
                    
                    if(1 == ifValid(rssiVal)){
                        D_first.set(D_second.getAddr(), D_second.getDistance());
                        D_second.set(D_third.getAddr(), D_third.getDistance());
                        D_third.set(spotAddress, distance(spotAddress, rssiVal));
                        System.out.println(spotAddress + " :  " + rssiVal);
                    }else{
                        if(D_first.getAddr().equals(spotAddress))
                            D_first.set(null, 0);                        
                        else if(D_second.getAddr().equals(spotAddress)){
                            D_second.set(D_first.getAddr(), D_first.getDistance());
                            D_first.set(null, 0);
                        }else if(D_third.getAddr().equals(spotAddress)){
                            D_third.set(D_second.getAddr(), D_second.getDistance());
                            D_second.set(D_first.getAddr(), D_first.getDistance());
                            D_first.set(null, 0);
                        }                                                                             
                    }        
                    
                    DrawLocation drawLocation = new DrawLocation();
                    if(D_first.getAddr() != null && D_second.getAddr() != null){
                        drawLocation.addData(D_first, D_second, D_third);
                    }
                } else {                  
                    System.out.println("Unrecognized radiogram type! Expected: " + RX_CONFIRM_BYTE + ", Saw: " + checkByte);
                }

            } catch (Exception e) {
                System.err.println("No datagram received!");
            }
        }
        
        
    }

    private int ifValid(int rssi){
        if((rssi > RSSI_MIN) && (rssi < RSSI_MAX)){
            return 1;
        }else{
            return 0;
        }
    }
   
    private double distance(String addr, int rssi){
        if(addr.equals(SENSOR_1_ADDR)){
            return rssi * K_1 + B_1;
        }
        if(addr.equals(SENSOR_2_ADDR)){
            return rssi * K_2 + B_2;
        }
        if(addr.equals(SENSOR_3_ADDR)){
            return rssi * K_3 + B_3;
        }
        if(addr.equals(SENSOR_4_ADDR)){
            return rssi * K_4 + B_4;
        }
        if(addr.equals(SENSOR_5_ADDR)){
            return rssi * K_5 + B_5;
        }
        if(addr.equals(SENSOR_6_ADDR)){
            return rssi * K_6 + B_6;
        }
        return 100;
    }
    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws IOException {
        RSSIReceiver app = new RSSIReceiver();
        app.setupConnection();
        app.receiveLoop();
        System.exit(0);
    }
}
