import java.util.ArrayList;
import java.util.Arrays;

public class GcodeCompiler {
    static final double xHome = 0;
    static final double yHome = 0;
    static final double zUp = 19; // want home z pos to be "UP"
    static final double xMax = 350; // gets stuck on something
    static final double yMax = 300; // find out later
    static final double zPaper = 0; // find out later

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

    public static void move(double x, double y, double z){ // if any coord is negative, omit
        if(x > xMax || y > yMax || z > zUp){
            System.out.printf("ERROR: Coords out of bounds %f %f %f\n", x ,y ,z);
        }
        StringBuilder sb = new StringBuilder();
        if(x < 0 && y < 0){
            System.out.printf("G1 Z%d\n", (int) z);
            return;
        } else {
            System.out.printf("G1 X%.4f Y%.4f\n", x, y);
        }
//        sb.append("G1");
//        if(x >= 0){
//            sb.append(" X" + x);
//        }
//        if(y >= 0){
//            sb.append(" Y" + y);
//        }
//        if(z >= 0){
//            sb.append(" Z" + z);
//        }
//        System.out.println(sb.toString());
    }
    public static void moveToHome(){
        System.out.println("G1 Z19");
        System.out.println("G1 X0 Y0");
        System.out.println("G1 Z0");
    }

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

    public static void drawRandomLines(int numberOfLines){
        for(int i = 0; i < numberOfLines; i++){
            //generate random start
            double xStart = Math.random() * xMax;
            double yStart = Math.random() * yMax;
            //generate random distance

            double dist = (Math.random() * 5) + 5; // lies between 100 and 200
            //generate random degree
            double radians = (Math.random() * 2 * Math.PI);
            //generate random end from distance and degree

            double xEnd = xStart + (dist * Math.cos(radians));
            while(xEnd < 0 || xEnd > xMax){
                radians = (Math.random() * 2 * Math.PI);
                xEnd = xStart + (dist * Math.cos(radians));
            }
            double yEnd = yStart + (dist * Math.sin(radians));
            while(yEnd < 0 || yEnd > yMax){
                radians = (Math.random() * 2 * Math.PI);
                yEnd = yStart + (dist * Math.sin(radians));
            }
            //draw the line
            drawLine(xStart, yStart, xEnd, yEnd);
        }
    }
    public static void drawSnake(int numberOfLines){
        double xStart = Math.random() * xMax;
        double yStart = Math.random() * yMax;
        for(int i = 0; i < numberOfLines; i++){
            //generate random distance
            double dist = (Math.random() * 5) + 5; // lies between 100 and 200
            //generate random degree
            double radians = (Math.random() * 2 * Math.PI);
            //generate random end from distance and degree

            double xEnd = xStart + (dist * Math.cos(radians));
            while(xEnd < 0 || xEnd > xMax){
                radians = (Math.random() * 2 * Math.PI);
                xEnd = xStart + (dist * Math.cos(radians));
            }
            double yEnd = yStart + (dist * Math.sin(radians));
            while(yEnd < 0 || yEnd > yMax){
                radians = (Math.random() * 2 * Math.PI);
                yEnd = yStart + (dist * Math.sin(radians));
            }
            //draw the line
            drawLine(xStart, yStart, xEnd, yEnd);
            xStart = xEnd;
            yStart = yEnd;
        }
    }
    public static void drawCenterCircle(double x, double y, double radius){
        move(-1, -1, zUp);
        move(x + radius, y, -1);
        move(-1, -1, zPaper);
        System.out.printf("G02 X%.4f Y%.4f I%.4f J%.4f\n", x - radius, y, -1 * radius, 0.0);
        System.out.printf("G02 X%.4f Y%.4f I%.4f J%.4f\n", x + radius, y, radius, 0.0);
        move(-1, -1, zUp);
    }
    public static void drawRandomCircles(int numCircles){
        for(int i = 0; i < numCircles; i++){
            //generate random start
            double xStart = Math.random() * xMax;
            double yStart = Math.random() * yMax;
            //generate random distance
            double dist = (Math.random() * 1.2) + .1; // lies between 100 and 20;
            while(xStart < dist || xStart > xMax - dist){
                xStart = Math.random() * xMax;
            }
            while(yStart < dist || yStart > yMax - dist){
                yStart = Math.random() * yMax;
            }
            drawCenterCircle(xStart, yStart, dist);
        }
    }
    public static void drawGridCircles(double radius){
        for(double i = radius; i < xMax; i += 2 * radius){
            for(double j = radius; j < yMax; j += 2 * radius){
                drawCenterCircle(i, j, radius);
            }
        }
    }
    public static void drawGridOverlapCircles(double radius){
        for(double i = radius; i < xMax; i += radius){
            for(double j = radius; j < yMax; j += radius){
                drawCenterCircle(i, j, radius);
            }
        }
    }
    public static double[][] produceEdgeCoords(double x, double y, double r){
        double[][] ans = new double[360][2];
        for(int i = 0; i < 360; i++){ //for every degree
            double rad = i * Math.PI / 180;
            ans[i][0] = x + Math.cos(rad) * r;
            ans[i][1] = y + Math.sin(rad) * r;
        }
        return ans;
    }
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
    public static void fillAreaWithNonOverlappingCircles(int numberOfCircles){
        ArrayList<CircleCoords> existingCircles = new ArrayList<>();
        for(int i = 0; i < numberOfCircles; i++){
            //generate random start
            double xStart = Math.random() * xMax;
            double yStart = Math.random() * yMax;
            //generate random distance
            double dist = (Math.random() * 1.2) + .1; // between .1 and 1.3

            while(xStart < dist || xStart > xMax - dist || yStart < dist || yStart > yMax - dist || inCircle(existingCircles, xStart, yStart, dist)){
                xStart = Math.random() * xMax;
                yStart = Math.random() * yMax;
                dist = (Math.random() * 1.2) + .1;
            }
            drawCenterCircle(xStart, yStart, dist);
            existingCircles.add(new CircleCoords(xStart, yStart, dist));
        }
    }
    public static int drawChainingCircles(int numberOfCircles, boolean debugOn){
        ArrayList<CircleCoords> existingCircles = new ArrayList<>();
        // FIRST ITERATION
        double xStart = Math.random() * xMax;
        double yStart = Math.random() * yMax;
        //generate random distance
        double dist = (Math.random() * 5.0) + 2.5; // between 2.5 and 7.5

        while(xStart < dist || xStart > xMax - dist || yStart < dist || yStart > yMax - dist || inCircle(existingCircles, xStart, yStart, dist)){
            xStart = Math.random() * xMax;
            yStart = Math.random() * yMax;
            dist = (Math.random() * 5.0) + 2.5;
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
            double randDist = (Math.random() * 5.0) + 2.5 + oldR;
            double newX = oldX + randDist * Math.cos(randAngle);
            double newY = oldY + randDist * Math.sin(randAngle);
            double newRad = randDist - oldR;
            int tryCounter = 0;
            while(newX < newRad || newX > xMax - newRad || newY < newRad || newY > yMax - newRad || inCircle(existingCircles, newX, newY, newRad)){
                if(tryCounter > 10000){
                    return -1 * i;
                }
                randAngle = Math.random() * 2 * Math.PI;
                randDist = (Math.random() * 5.0) + 2.5 + oldR;
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

    public static void setup(){
        System.out.println();
        System.out.println("G90"); // absolute positioning
        System.out.println("M03 S1000");
        System.out.println("G1 Z19 F1000");
    }

    public static void tearDown(){
        moveToHome();
        System.out.println("M05");
        System.out.println("M02");
    }
    public static void main(String[] args){
        // System.out.println("Start prog");
        setup();
        //drawCenterCircle(10, 10, 10);
        drawChainingCircles(10, false);
        tearDown();
        // System.out.println("End prog");
    }
}
