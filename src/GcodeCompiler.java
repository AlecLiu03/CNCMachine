import java.util.ArrayList;
import java.util.Arrays;

public class GcodeCompiler {

    // max constants for drawing surface
    static final double xHome = 0; // also xMin
    static final double yHome = 0; // also yMin
    static final double zUp = 19; // want home z pos to be "UP"
    static final double xMax = 350; // gets stuck on something
    static final double yMax = 300;
    static final double zPaper = 0;

    // user defined drawing space, by default includes the entire space
    public static double xLow = xHome;
    public static double xHigh = xMax;
    public static double yLow = yHome;
    public static double yHigh = yMax;

    // used to store information about circle locations - may be expanded to be "polygon" in the future
    private static class CircleCoords{
        double x;
        double y;
        double r;
        public CircleCoords(double x, double y, double r){
            this.x = x;
            this.y = y;
            this.r = r;
        }
    }

    // set drawing area coordinates
    public static void setDrawingSpace(double xL, double xH, double yL, double yH){
        if(xL < xHome || xH > xMax || yL < yHome || yH > yMax){
            System.out.println("User defined drawing space is out of bounds, proceeding with default values");
            return;
        }
        xLow = xL;
        xHigh = xH;
        yLow = yL;
        yHigh = yH;
    }

    // move the drawing instrument to the specified location. Usually used like "move up, move to x-y coord, move down"
    public static void move(double x, double y, double z){ // if any coord is negative, omit
        if(x > xHigh || y > yHigh || z > zUp){
            System.out.printf("ERROR: Coords out of bounds %f %f %f\n", x ,y ,z);
        }
        StringBuilder sb = new StringBuilder();
        if(x < 0 && y < 0){
            System.out.printf("G1 Z%d\n", (int) z);
            return;
        } else {
            System.out.printf("G1 X%.4f Y%.4f\n", x, y);
        }
    }

    // move back to machine starting state
    public static void moveToHome(){
        System.out.println("G1 Z19");
        System.out.println("G1 X0 Y0");
        System.out.println("G1 Z0");
    }

    // draw a line from (xStart, yStart) tp (xEnd, yEnd)
    public static void drawLine(double xStart, double yStart, double xEnd, double yEnd){
        move(-1, -1, zUp);
        move(xStart, yStart, -1);
        move(-1, -1, zPaper);
        move(xEnd, yEnd, -1);
        move(-1, -1, zUp);
    }

    // draw a rectangle with the top left corner at (x,y) with length lenX and width lenY
    public static void drawCornerRectangle(double x, double y, double lenX, double lenY){
        // move to correct position
        move(-1, -1, zUp);
        move(x, y, -1);
        move(-1, -1, zPaper);

        //draw rectangle
        move(x + lenX, y, -1);
        move(x + lenX, y + lenY, -1);
        move(x, y + lenY, -1);
        move(x, y, -1);

        //return to original position
        move(-1, -1, zUp);
    }

    // draw a rectangle centered (x,y) with length lenX and width lenY
    public static void drawCenterRectangle(double x, double y, double lenX, double lenY){
        // move to correct position
        move(-1, -1, zUp);
        double startX = x - lenX / 2;
        double startY = y - lenY / 2;
        move(startX, startY, -1);
        move(-1, -1, zPaper);

        // draw rectangle
        move(startX + lenX, startY, -1);
        move(startX + lenX, startY + lenY, -1);
        move(startX, startY + lenY, -1);
        move(startX, startY, -1);
        move(-1, -1, zUp);
        move(x, y, -1);

        // return to original position
    }

    // draw a set number of lines randomly on the surface
    public static void drawRandomLines(int numberOfLines, double minDist, double maxDist){
        for(int i = 0; i < numberOfLines; i++){
            //generate random start
            double xStart = Math.random() * (xHigh - xLow) + xLow;
            double yStart = Math.random() * (yHigh - yLow) + yLow;

            //generate random distance
            double dist = Math.random() * (maxDist - minDist) + minDist;

            //generate random degree
            double radians = (Math.random() * 2 * Math.PI);

            //generate random end from distance and degree
            double xEnd = xStart + (dist * Math.cos(radians));
            double yEnd = yStart + (dist * Math.sin(radians));
            while(xEnd < xLow || xEnd > xHigh || yEnd < yLow || yEnd > yHigh){
                radians = (Math.random() * 2 * Math.PI);
                xEnd = xStart + (dist * Math.cos(radians));
                yEnd = yStart + (dist * Math.sin(radians));
            }
            //draw the line
            drawLine(xStart, yStart, xEnd, yEnd);
        }
    }

    // draw a set number of lines randomly on the surface, this time angle bounded
    public static void drawRandomLinesWithAngleBounds(int numberOfLines, double minDist, double maxDist, double minAngle, double maxAngle){
        for(int i = 0; i < numberOfLines; i++){
            //generate random start
            double xStart = Math.random() * (xHigh - xLow) + xLow;
            double yStart = Math.random() * (yHigh - yLow) + yLow;

            //generate random distance
            double dist = Math.random() * (maxDist - minDist) + minDist;

            //generate random degree
            double degrees = Math.random() * (maxAngle - minAngle) + minAngle;
            double radians = (degrees / 360 * 2 * Math.PI);

            //generate random end from distance and degree
            double xEnd = xStart + (dist * Math.cos(radians));
            double yEnd = yStart + (dist * Math.sin(radians));
            while(xEnd < xLow || xEnd > xHigh || yEnd < yLow || yEnd > yHigh){
                degrees = Math.random() * (maxAngle - minAngle) + minAngle;
                radians = (degrees / 360 * 2 * Math.PI);
                xStart = Math.random() * (xHigh - xLow) + xLow;
                yStart = Math.random() * (yHigh - yLow) + yLow;
                xEnd = xStart + (dist * Math.cos(radians));
                yEnd = yStart + (dist * Math.sin(radians));
            }
            //draw the line
            System.out.println("Generateed angle " + degrees);

            drawLine(xStart, yStart, xEnd, yEnd);
        }
    }

    // draw a set number of lines back to back on the surface
    public static void drawSnake(int numberOfLines, double minDist, double maxDist){
        double xStart = Math.random() * (xHigh - xLow) + xLow;
        double yStart = Math.random() * (yHigh - yLow) + yLow;
        for(int i = 0; i < numberOfLines; i++){
            //generate random distance
            double dist = Math.random() * (maxDist - minDist) + minDist;

            //generate random degree
            double radians = (Math.random() * 2 * Math.PI);

            //generate random end from distance and degree
            double xEnd = xStart + (dist * Math.cos(radians));
            while(xEnd < xLow || xEnd > xHigh){
                radians = (Math.random() * 2 * Math.PI);
                xEnd = xStart + (dist * Math.cos(radians));
            }
            double yEnd = yStart + (dist * Math.sin(radians));
            while(yEnd < xLow || yEnd > yHigh){
                radians = (Math.random() * 2 * Math.PI);
                yEnd = yStart + (dist * Math.sin(radians));
            }
            //draw the line
            drawLine(xStart, yStart, xEnd, yEnd);
            xStart = xEnd;
            yStart = yEnd;
        }
    }

    // draw a circle based on a center point and radius
    public static void drawCenterCircle(double x, double y, double radius){
        if (x < (radius + xLow) || x > xHigh - radius || y < (radius + yLow) || y > yHigh - radius){
            System.out.println("ERROR: Center circle out of bounds");
        }
        move(-1, -1, zUp);
        move(x + radius, y, -1);
        move(-1, -1, zPaper);
        System.out.printf("G02 X%.4f Y%.4f I%.4f J%.4f\n", x - radius, y, -1 * radius, 0.0);
        System.out.printf("G02 X%.4f Y%.4f I%.4f J%.4f\n", x + radius, y, radius, 0.0);
        move(-1, -1, zUp);
    }

    // draw a set number of circles randomly on the paper
    public static void drawRandomCircles(int numCircles, double minRad, double maxRad){
        for(int i = 0; i < numCircles; i++){
            //generate random start
            double xStart = Math.random() * (xHigh - xLow) + xLow;
            double yStart = Math.random() * (yHigh - yLow) + yLow;

            //generate random distance
            double dist = Math.random() * (maxRad - minRad) + minRad;

            // while this x,y, dist combo is invalid, retry
            while(xStart < (dist + xLow) || xStart > xHigh - dist || yStart < (dist + yLow) || yStart > yHigh - dist) {
                xStart = Math.random() * (xHigh - xLow) + xLow;
                yStart = Math.random() * (yHigh - yLow) + yLow;
                dist = Math.random() * (maxRad - minRad) + minRad;
            }
            drawCenterCircle(xStart, yStart, dist);
        }
    }

    // draw an even distribution of tangent circles on the paper based on radius
    public static void drawGridCircles(double radius){
        for(double i = xLow + radius; i < xHigh; i += 2 * radius){
            for(double j = yLow + radius; j < yHigh; j += 2 * radius){
                drawCenterCircle(i, j, radius);
            }
        }
    }

    // draw an even distribution of circles, but each border passes through the center of neighboring circles
    public static void drawGridOverlapCircles(double radius){
        for(double i = xLow + radius; i < xHigh; i += radius){
            for(double j = yLow + radius; j < yHigh; j += radius){
                drawCenterCircle(i, j, radius);
            }
        }
    }

    // HELPER FUNCTIONS - CIRCLE DRAWING

    // produce an approximate border of a circle
    public static double[][] produceEdgeCoords(double x, double y, double r){
        double[][] ans = new double[360][2];
        for(int i = 0; i < 360; i++){ //for every degree
            double rad = i * Math.PI / 180;
            ans[i][0] = x + Math.cos(rad) * r;
            ans[i][1] = y + Math.sin(rad) * r;
        }
        return ans;
    }

    // determine if a given circle overlaps with a list of existing circles
    public static boolean inCircle(ArrayList<CircleCoords> existing, double x, double y, double r){
        double[][] circleEdges = produceEdgeCoords(x, y, r);
        for(CircleCoords c : existing){ // for every existing circle
            double distBetweenRad = Math.sqrt((x - c.x) * (x - c.x) + (y - c.y) * (y - c.y));
            if(distBetweenRad + c.r <= r){
                return true;
            }
            for(int i = 0; i < 360; i++){ // see if any circle coord falls inside it
                // (x - x1)^2 + (y - y1)^2 < r^2
                double left = (c.x - circleEdges[i][0]) * (c.x - circleEdges[i][0]) + (c.y - circleEdges[i][1]) * (c.y - circleEdges[i][1]);
                double right = c.r * c.r;
                if(left < right){
                    return true;
                }
            }
        }
        return false;
    }

    // END HELPER FUNCTIONS

    // attempt to fill an area with a set number of circles
    public static void fillAreaWithNonOverlappingCircles(int numberOfCircles, int minRad, int maxRad){
        ArrayList<CircleCoords> existingCircles = new ArrayList<>();
        for(int i = 0; i < numberOfCircles; i++){
            //generate random start
            double xStart = Math.random() * (xHigh - xLow) + xLow;
            double yStart = Math.random() * (yHigh - yLow) + yLow;

            //generate random distance
            double dist = Math.random() * (maxRad - minRad) + minRad;

            // while this x,y, dist combo is invalid, retry
            while(xStart < (dist + xLow) || xStart > xHigh - dist || yStart < (dist + yLow) || yStart > yHigh - dist || inCircle(existingCircles, xStart, yStart, dist)){
                xStart = Math.random() * (xHigh - xLow) + xLow;
                yStart = Math.random() * (yHigh - yLow) + yLow;
                dist = Math.random() * (maxRad - minRad) + minRad;
            }
            drawCenterCircle(xStart, yStart, dist);
            existingCircles.add(new CircleCoords(xStart, yStart, dist));
        }
    }

    // attempt to draw a set number of circles that are tangent and non-overlapping
    public static int drawChainingCircles(int numberOfCircles, boolean debugOn, double minRad, double maxRad){
        ArrayList<CircleCoords> existingCircles = new ArrayList<>();
        // FIRST ITERATION
        double xStart = Math.random() * (xHigh - xLow) + xLow;
        double yStart = Math.random() * (yHigh - yLow) + yLow;
        //generate random distance
        double dist = Math.random() * (maxRad - minRad) + minRad;

        // while this x,y, dist combo is invalid, retry
        while(xStart < (dist + xLow) || xStart > xHigh - dist || yStart < (dist + yLow) || yStart > yHigh - dist || inCircle(existingCircles, xStart, yStart, dist)){
            xStart = Math.random() * (xHigh - xLow) + xLow;
            yStart = Math.random() * (yHigh - yLow) + yLow;
            dist = Math.random() * (maxRad - minRad) + minRad;
        }
        drawCenterCircle(xStart, yStart, dist);
        existingCircles.add(new CircleCoords(xStart, yStart, dist));
        // END FIRST ITERATION
        for(int i = 1; i < numberOfCircles; i++){
            // going from xStart, decide on a dist away and an angle
            double oldX = existingCircles.get(existingCircles.size() - 1).x;
            double oldY = existingCircles.get(existingCircles.size() - 1).y;
            double oldR = existingCircles.get(existingCircles.size() - 1).r;
            double randAngle = Math.random() * 2 * Math.PI;
            double randDist = Math.random() * (maxRad - minRad) + minRad + oldR;
            double newX = oldX + randDist * Math.cos(randAngle);
            double newY = oldY + randDist * Math.sin(randAngle);
            double newRad = randDist - oldR;
            int tryCounter = 0;

            // while this x,y, dist combo is invalid, retry - might get stuck and have to bail out
            while(newX < newRad + xLow || newX > xHigh - newRad || newY < newRad + yLow || newY > yHigh - newRad || inCircle(existingCircles, newX, newY, newRad)){
                if(tryCounter > 10000){
                    System.out.println("Couldn't fit all circles in bounds!");
                    return -1 * i;
                }
                randAngle = Math.random() * 2 * Math.PI;
                randDist = Math.random() * (maxRad - minRad) + minRad + oldR;;
                newX = oldX + randDist * Math.cos(randAngle);
                newY = oldY + randDist * Math.sin(randAngle);
                newRad = randDist - oldR;
                tryCounter++;
            }
            drawCenterCircle(newX, newY, newRad);

            //for debugging purposes
            if(debugOn){
                drawLine(oldX, oldY, newX, newY);
            }
            existingCircles.add(new CircleCoords(newX, newY, newRad));
        }
        return numberOfCircles;
    }

    // draw a honeycomb distribution across the page - not area bounded
    public static void drawHoneycomb(double sideLength){
        int numRows = (int) (xMax / (Math.sqrt(3) * sideLength));
        int numCols = (int) (yMax / (3 * sideLength));
        for(int i = 0; i < numRows; i++){
            for(int j = 0; j < numCols; j++){
                double baseX = i * sideLength * Math.sqrt(3);
                double baseY = j * 3 * sideLength;
                double vertHelper = sideLength * Math.sqrt(3) / 2;
                if(j > 0){
                    drawLine(baseX + vertHelper, baseY, baseX + vertHelper, baseY + sideLength);
                }
                drawLine(baseX + vertHelper, baseY + sideLength, baseX, baseY + 1.5 * sideLength);
                drawLine(baseX, baseY + 1.5 * sideLength, baseX, baseY + 2.5 * sideLength);
                drawLine(baseX, baseY + 2.5 * sideLength, baseX + vertHelper, baseY + 3 * sideLength);
                drawLine(baseX + vertHelper, baseY + 3 * sideLength, baseX + 2 * vertHelper, baseY + 2.5 * sideLength);
                drawLine(baseX + vertHelper, baseY + sideLength, baseX + 2 * vertHelper, baseY + 1.5 * sideLength);
                if(i == numRows - 1){
                    drawLine(baseX + 2 * vertHelper, baseY + 1.5 * sideLength, baseX + 2 * vertHelper, baseY + 2.5 * sideLength);
                }
            }
        }
    }

    // draw a sin wave - not area bounded
    public static void drawSinWave(double x, double y){
        move(x, y, -1);
        double[][] offsets = new double[101][2];
        for(int i = 0; i < offsets.length; i++){
            double delX = i * Math.PI / 50;
            double delY = Math.sin(delX);
            offsets[i][0] = delX;
            offsets[i][1] = delY;
        }
        for(int i = 0; i < offsets.length - 1; i++){
            drawLine(x + offsets[i][0], y + offsets[i][1],x + offsets[i + 1][0], y + offsets[i + 1][1]);
        }
    }

    // HELPER FUNCTIONS - PERIPHERAL FUNCTIONS

    // draw marks along the y-axis
    public static void drawYHash(){
        for(int i = 0; i <= yMax; i += 20){
            move(-1, -1, zUp);
            move(10, i, -1);
            move(-1, -1, zPaper);
            move(0, i, -1);
        }
    }

    // draw marks along the x-axis
    public static void drawXHash(){
        for(int i = 20; i < xMax; i += 20){
            move(-1, -1, zUp);
            move(i, 5, -1);
            move(-1, -1, zPaper);
            move(i, 0, -1);
        }
    }

    // print out G-code initialization
    public static void setup(){
        System.out.println();
        System.out.println("G90"); // absolute positioning
        System.out.println("M03 S1000");
        System.out.println("G1 Z19 F1000");
    }

    // print out clean up G-code
    public static void tearDown(){
        moveToHome();
        System.out.println("M05");
        System.out.println("M02");
    }

    // END HELPER FUNCTIONS

    public static void main(String[] args){
        setDrawingSpace(80, 300, 40, 220);
        setup();
        drawRandomLinesWithAngleBounds(30, 25.4, 27.8, 90, 180);
        tearDown();
    }
}
