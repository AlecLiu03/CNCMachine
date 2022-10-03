public class GcodeCompiler {
    static final double xHome = 0;
    static final double yHome = 0;
    static final double zUp = 100; // want home z pos to be "UP"
    static final double xMax = 10; // find out later
    static final double yMax = 10; // find out later
    static final double zFloor = 0; // find out later
    static final double zPaper = 5; // find out later

    public static void move(double x, double y, double z){ // if any coord is negative, omit
        if(x > xMax || y > yMax || z > zUp){
            System.out.printf("ERROR: Coords out of bounds %f %f %f\n", x ,y ,z);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("G01");
        if(x >= 0){
            sb.append(" X" + x);
        }
        if(y >= 0){
            sb.append(" Y" + y);
        }
        if(z >= 0){
            sb.append(" Z" + z);
        }
        System.out.println(sb.toString());
    }
    public static void moveToHome(){
        System.out.printf("G00 X%f Y%f Z%f\n", xHome, yHome, zUp);
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
    public static void main(String[] args){
        // System.out.println("Start prog");
        System.out.println("G90"); // absolute positioning
        drawRandomLines(100);
        System.out.println("M02");
        // System.out.println("End prog");
    }
}
