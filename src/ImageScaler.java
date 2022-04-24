import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ImageScaler {
    public static String corrected(String s){ //specifically for small numbers
        int index = s.indexOf('E');
        int exp = Integer.parseInt(s.substring(index + 1));
        exp *= -1;
        String ret = s.charAt(0) + "0.";
        for(int i = 0; i < exp - 1; i++){
            ret += "0";
        }
        for(int i = 1; i < index; i++){
            if(s.charAt(i) == '-'){
                ret = ret.substring(0, 1) + "-" + ret.substring(1);
            } else if(s.charAt(i) != '.'){
                ret += s.charAt(i);
            }
        }
        return ret;
    }
    public static void readFromFile(String filename, int scaleFactor){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                if(words.length == 0){
                    continue;
                } else {
                    for (int i = 0; i < words.length; i++) {
                        if(words[i].length() == 0){
                            continue;
                        }
                        char ch = words[i].charAt(0);
                        if (ch == 'X' || ch == 'Y' || ch == 'I' || ch == 'J') {
                            double val = Double.parseDouble(words[i].substring(1));
                            val *= scaleFactor;
                            words[i] = ch + "" + val;
                            if(words[i].contains("E")){
                                words[i] = corrected(words[i]);
                            }
                        }
                    }
                    String newLine = String.join(" ", words);
                    System.out.println(newLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        readFromFile("fancy.txt", 2);
        //System.out.println(corrected("J-2.0E-4"));
    }
}
