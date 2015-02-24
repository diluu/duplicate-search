package duplicateFind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SearchFileWriter {

    /**
     * writes the found duplicate pairs to result file
     * if file does not exists new file will be created
     * @param resultPath
     * @param result
     */
    public static void writeDuplicates(String resultPath, String result){
        File resultFile = new File(resultPath);
        if (!resultFile.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
                bw.write(result);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter fw = new FileWriter(resultFile,true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.newLine();
                bw.write(result);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
