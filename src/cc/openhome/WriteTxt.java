package cc.openhome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.Writer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
 
public class WriteTxt {
    public static void main(String args[]) throws IOException {
//        String str = "測試";
//        FileWriter fw = new FileWriter("svm.txt");
//        fw.write(str);
//        fw.close();
    }
    
    public void input(String path, ArrayList<HashMap<String, Object>> svm_list) throws IOException {
//        System.out.println(path);
        
        String str;
        FileWriter fw = new FileWriter(path + "/data/svm.txt");
        
        for (HashMap<String, Object> svm : svm_list) {
            for (Object value : svm.values()) {
                str = value + " ";
                fw.write(str);
//                System.out.println(value);
            }
            fw.write("\n");
        }
        fw.close();
//        System.out.println(svm_list);
    }
}