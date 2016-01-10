package utils;

public class Printer {
    
    public static String pageBreak() {
        String str = "";
        for (int i = 0; i < 50; i++) {
            str += "\n";
        }
        return str;
    }
    
}
