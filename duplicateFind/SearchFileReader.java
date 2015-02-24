package duplicateFind;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

public class SearchFileReader {
    private static MessageDigest messageDigest;     //message digest to generate hash value for file content
    static {
        try {
            messageDigest = MessageDigest.getInstance(/*"SHA-256"*/"MD5");   //this use Java default implementation of MD5 hashing
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * generate File objects for each file in the root directory as well as sub directories recursively
     * @param fileContents new objects will be added to this list
     * @param folder current folder
     * @return  filled hashtable
     */
    public static Hashtable<String, File> readFiles(Hashtable<String, File> fileContents, File folder){
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile()) {
                    fileContents.put(f.getPath(), f);
                } else{
                    readFiles(fileContents, f);
                }
            }
        }
        return fileContents;

    }

    /**
     * generate hash values for each of the files in root directory as well as sub directories recursively
     * @param folder current folder
     * @param hashValues filling hashtable
     * @return  filled hashtable
     */
    public static Hashtable<String, String> readFilesInHashMode(File folder, Hashtable<String, String> hashValues){
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isFile()) {
                    try {
                        FileInputStream fin = new FileInputStream(f);
                        byte data[] = new byte[(int) f.length()];
                        fin.read(data);
                        fin.close();
                        String hash = new BigInteger(1, messageDigest.digest(data)).toString(16);
                        hashValues.put(f.getPath(), hash);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else{
                    readFilesInHashMode(f, hashValues);
                }
            }
        }
        return hashValues;

    }

    /**
     *
     * @param resultPath  read the result file to display current result
     * @return current result
     */
    public static String readResultFile(String resultPath){
        try {
            StringBuffer resultBuffer = new StringBuffer();
            try {
                FileInputStream fstream = new FileInputStream(new File(resultPath));
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null)   {
                    resultBuffer.append(strLine + "\n");
                }
                in.close();
            } catch (IOException e) {
                return "No Results to Display";
            } catch (Exception e1){
                return "No Results to Display";
            }
            return resultBuffer.toString();
        } catch (Exception e) {
            return "No Results to Display";
        }
    }


}
