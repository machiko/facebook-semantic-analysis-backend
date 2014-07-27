package cc.openhome;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.Writer;
import java.io.FileNotFoundException;
import java.io.IOException;
 
public class WriteTxt {
    public static void main(String args[]) {
    }
    
    public void writeFile() {
        Writer writer = null;
        
        try {
            String text = "asdf";
 
            File file = new File("write.txt");
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}