public class GcodeCompiler {
    static final double xHome = 0;
    static final double yHome = 0;
    static final double zUp = 100; // want home z pos to be "UP"
    static final double xMax = 1000; // find out later
    static final double yMax = 1000; // find out later
    static final double zFloor = 0; // find out later
    static final double zPaper = 50; // find out later

    public static void move(double x, double y, double z){ // if any coord is negative, omit
        if(x > xMax || y > yMax || z > zUp){
            System.out.printf("ERROR: Coords out of bounds %f %f %f\n", x ,y ,z);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("G00");
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

    public static void moveHome(){
        System.out.printf("G00 X%f Y%f Z%f\n", xHome, yHome, zUp);
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

    public static void main(String[] args){
        // System.out.println("Start prog");
        System.out.println("G90"); // absolute positioning
        drawCornerRectangle(100, 100, 100, 100);
        drawCenterRectangle(150, 150, 100, 100);
        System.out.println("M02");
        // System.out.println("End prog");
    }
}
