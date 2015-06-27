/**
 * Created by Samuel on 2014/10/27.
 */
package org.sunspotworld;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import org.sunspotworld.SensorInfo;

public class DrawLocation extends JFrame {
    Point start, end;
    HashMap<String, Point> sensorPoints;
    ArrayList<String> sensorAddrs;
    int scaleUnit;
    int xMax, yMax;
    double zoomFactor;
    Container p;
    int numOfSensors;
    SensorInfo si1, si2, si3;

    int windowWidth = Constants.CANVAS_WIDTH + 2 * Constants.CANVAS_MARGIN_WIDTH;
    int windowHeight = Constants.CANVAS_HEIGHT + 2 * Constants.CANVAS_MARGIN_HEIGHT;
    public DrawLocation() {
        p = getContentPane();
        int window_X, window_Y;

        window_X = (Constants.SCREEN_WIDTH - windowWidth) / 2;
        window_Y = (Constants.SCREEN_HEIGHT - windowHeight) / 2;
        setBounds(window_X, window_Y, windowWidth, windowHeight);
        setTitle("EC544 Challenge 5 - Group #2");
        setVisible(true);
        setLayout(null);

        // Get the location of 4 sensors
        Config config = new Config();
        numOfSensors = config.getNumofsensors();
        sensorPoints = config.getSensorPoints();
        sensorAddrs = new ArrayList<String>(sensorPoints.keySet());

        scaleUnit = getScaleUnit(config.getMaxscale());
        xMax = config.getxMax();
        yMax = config.getyMax();
        zoomFactor = 10.0 * scaleUnit / Constants.CANVAS_HEIGHT;

        paintComponents(this.getGraphics());
        setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addData(SensorInfo si1, SensorInfo si2, SensorInfo si3){
        this.si1 = si1;
        this.si2 = si2;
        this.si3 = si3;
    }
    
    public void paintComponents(Graphics gg) {
//        gg.DrawLocation(10, 100, 200, 400);
        final Graphics g = gg;

        // Repainting area that needs to be cleared to display new location value
        final int clear_block_width = 200;
        final int clear_block_height = 12;
        start = new Point(Constants.CANVAS_MARGIN_WIDTH, Constants.CANVAS_MARGIN_HEIGHT);
        end = new Point(Constants.CANVAS_MARGIN_WIDTH, Constants.CANVAS_MARGIN_HEIGHT);
        g.setColor(Color.blue);
        Runnable run = new Runnable() {
            Point temp = null;
            int x = Constants.CANVAS_MARGIN_WIDTH;
            int y;
            String strLocation;
            boolean outOfScope = false;

            public void run() {
                int d = 1;
                int i = 0;
                Location xy;

                Trilateration tri = null;
                while(true) {
                    try {
                        long time = System.currentTimeMillis();
//                        float r1 = 740;
//                        float r2 = 710 + i;
//                        float r3 = 505 - i;
//                        si1 = new SensorInfo("80F5", r1);
//                        si2 = new SensorInfo("45BB", r2);
//                        si3 = new SensorInfo("79B0", r3);

                        tri = getTrilateration(si1.getAddr(), si2.getAddr(), si3.getAddr());
                        // Get the sensor location
                        xy = tri.getLocationFromDistance(si1.getDistance(), si2.getDistance(), si3.getDistance());

                        // Draw the sensor track in real-time mode
                        x = (int)xy.getX();
                        y = (int)xy.getY();
//                        y = Constants.CANVAS_MARGIN_HEIGHT + (int)(40*Math.sin(Math.PI*(x-Constants.CANVAS_MARGIN_WIDTH)/30));
//                        y = Constants.CANVAS_MARGIN_HEIGHT + 3*(x-Constants.CANVAS_MARGIN_WIDTH)/4;
//                        System.out.println("********" + x + ", " + y);
                        temp = new Point(x, y);
                        cleanCanvas(g);

                        // Update location label
                        if(isInScope(x,y)) {
                            drawCenteredCircle(g, Color.RED, x, y, 8);
                            g.setColor(Color.BLUE);
                            strLocation = "Sensor Location: " + x + ", " + y;
                        }else{
                            g.setColor(Color.RED);
                            strLocation = "Sensor Location: Out of scope!";
                        }
                        g.clearRect(windowWidth / 2 - 8, windowHeight - 20, clear_block_width, clear_block_height);
                        g.drawString(strLocation, windowWidth / 2 - 100, windowHeight - 10);

                        // Move to next location
                        start = end;
                        end = temp;
                        outOfScope = false;

                        // Draw the x, y axis
                        g.setColor(Color.GRAY);
                        drawXYAxis(g);

                        // Show the fixed sensors' locations
                        /**
                         *          C               D
                         *
                         *
                         *          A               B
                         */
                        drawFixedSensors(g);

                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    x += d;
                    ++i;
                }
            }
        };
        new Thread(run).start();
    }

    public Bilateration getBilateration(String addr1, String addr2){
        Point pA, pB;
        pA = sensorPoints.get(addr1);
        pB = sensorPoints.get(addr2);
        return new Bilateration(pA.x, pA.y, pB.x, pB.y);
    }

    public Trilateration getTrilateration(String addr1, String addr2, String addr3){
        Point pA, pB, pC;
        pA = sensorPoints.get(addr1);
        pB = sensorPoints.get(addr2);
        pC = sensorPoints.get(addr3);
        return new Trilateration(pA.x, pA.y, pB.x, pB.y, pC.x, pC.y);
    }

    public int getScaleUnit(int length){
        int u1 = length / 10;
        int u2 = length / 9;
        for(int ii=u1; ii<=u2; ++ii){
            if(ii%100 == 0) return ii;
        }
        for(int ii=u1; ii<=u2; ++ii){
            if(ii%10 == 0) return ii;
        }
        for(int ii=u1; ii<=u2; ++ii){
            if(ii%5 == 0) return ii;
        }
        for(int ii=u1; ii<=u2; ++ii){
            if(ii%2 == 0) return ii;
        }
        return 1;
    }

    public void drawXYAxis(Graphics g){

        // x axis: (x0, yn) ~ (xn, yn)
        // y axis: (x0, y0) ~ (x0, yn)
        int x0 = Constants.CANVAS_MARGIN_WIDTH;
        int y0 = Constants.CANVAS_MARGIN_HEIGHT;
        int xn = x0 + Constants.CANVAS_WIDTH;
        int yn = y0 + Constants.CANVAS_HEIGHT;
        g.drawLine(x0, yn, xn, yn);     // draw x axis
        g.drawLine(x0, y0, x0, yn);     // draw y axis
        int tickInt = Constants.CANVAS_HEIGHT / 10;
        int min = 0;
        for (int xt = x0 + tickInt; xt < xn; xt += tickInt) {
            g.drawLine(xt, yn + 5, xt, yn - 5);
//            int min = (xt - x0) * (xLen / Constants.CANVAS_MARGIN_WIDTH);
            min += scaleUnit;
            g.drawString(Integer.toString(min), xt - (min < 10 ? 3 : 7) , yn + 20);
        }

//        tickInt = Constants.CANVAS_HEIGHT / 10;
        min = 0;
        for (int yt = yn-tickInt; yt > Constants.CANVAS_MARGIN_HEIGHT; yt -= tickInt) {
            g.drawLine(x0 - 5, yt, x0 + 5, yt);
//            int min = (yt - y0) * (yLen / Constants.CANVAS_MARGIN_HEIGHT);
            min += scaleUnit;
            g.drawString(Integer.toString(min), x0 - 32 , yt + 5);
        }
    }

    public void drawFixedSensors(Graphics g) {

        Color color = Color.BLUE;

        for(String key:sensorPoints.keySet()){
            Point pt = sensorPoints.get(key);
            drawCenteredCircle(g, color, pt.x, pt.y, 10);
            String label = key + "(" + pt.x + ", " + pt.y + ")";
            drawLabel(g, label, pt.x - 30, pt.y + 30);
        }
    }

    public void drawLabel(Graphics gg, String txt, int x, int y){
        gg.drawString(txt, (int)(x/zoomFactor) + Constants.CANVAS_MARGIN_WIDTH,
                Constants.CANVAS_MARGIN_HEIGHT + Constants.CANVAS_HEIGHT - (int)(y/zoomFactor));
    }

    public void drawCenteredCircle(Graphics gg, Color color, int x, int y, int r) {
        gg.setColor(color);
        x = (int)(x/zoomFactor) + Constants.CANVAS_MARGIN_WIDTH;
        y = Constants.CANVAS_MARGIN_HEIGHT + Constants.CANVAS_HEIGHT - (int)(y/zoomFactor);
        x = x-(r/2);
        y = y-(r/2) ;
        gg.fillOval(x,y,r,r);
    }

    public void cleanCanvas(Graphics gg){
        int cleanX = Constants.CANVAS_MARGIN_WIDTH + 5;
        int cleanY = Constants.CANVAS_MARGIN_HEIGHT + 5;
        gg.clearRect(cleanX, cleanY, Constants.CANVAS_WIDTH + Constants.CANVAS_MARGIN_WIDTH, Constants.CANVAS_HEIGHT - 10);
    }

    public boolean isInScope(int x, int y){
        return !(x<30 || x>xMax || y<30 || y>yMax);
    }
}