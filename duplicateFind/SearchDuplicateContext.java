package duplicateFind;

import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SearchDuplicateContext {

    //File objects for each files in directory and its sub directories, used in content based mode
    private static Hashtable<String, File> fileContents = new Hashtable<String, File>();
   //Hash strings for each files in directory and its sub directories, used in hash based mode
    private static Hashtable<String, String> hashValues = new Hashtable<String, String>();  //
    private static ExecutorService executor;   //ThreadPool executor service
    private static String folderPath;  //root directory
    private static String resultPath;  //result file
    private static boolean searching; //searching in progress
    private static boolean aborted;  //operation aborted
    private static int threadCount = 25;    //thread pool thread count
    private static ConcurrentLinkedQueue<FilePair> duplicates = new ConcurrentLinkedQueue<FilePair>();  //Duplicate file pairs found
    private static ConcurrentLinkedQueue<FilePair> checkedPairs = new ConcurrentLinkedQueue<FilePair>();  //Checked file pairs
    private static int mode;  //mode

    /**
     *  this is called when user selects to start a new search
     *  new executor service object will be created with the new thread count
     *  the searching operation will begin in a new thread
     *  if the a file with the result file name already exists it wll be deleted
     *  generate the relevant map
     *  find dupicate pairs
     */
    public static void startNewSearch() {
        searching = true;
        executor = Executors.newFixedThreadPool(threadCount);

        Runnable runnable = new Runnable() {
            public void run() {
                File resultFile = new File(resultPath);
                if (resultFile.exists()) {
                    resultFile.delete();
                }
                File folder = new File(folderPath);

                if (mode == 0) {
                    fileContents = SearchFileReader.readFiles(fileContents, folder);
                } else {
                    long time1 = System.currentTimeMillis();
                   hashValues = SearchFileReader.readFilesInHashMode(folder, hashValues);
                    System.out.println("Time to read :" + (System.currentTimeMillis() - time1));
                }
                findDuplicates();
            }
        };
        executor.execute(runnable);
    }

    /**
     * compares each file in the relevant store with every other file in the store in a new thread
     * if operation is abborted it will wait until it resumes
     */
    private static void findDuplicates() {

        String[] arrFiles;
        if (mode == 0) {
            arrFiles = fileContents.keySet().toArray(new String[fileContents.size()]);
        } else {
            arrFiles = hashValues.keySet().toArray(new String[hashValues.size()]);
        }
        for (final String s : arrFiles) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!aborted) {
                        if (mode == 0) {
                            checkDuplicates(s);
                        } else {
                            checkDuplicatesInHashMode(s);
                        }
                    } else {
                        while (aborted) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            executor.execute(runnable);

        }
    }

    /**
     *
     * @param newPair : checks whether this pair is already compared
     * @return <true>if it is in checked pairs</true>
     */
    private static boolean isAlreadyChecked(FilePair newPair){
        for(FilePair pair : checkedPairs){
            if(pair.equals(newPair)){
                return true;
            }
        }
        return false;
    }

    /**
     * compare files in hash mode
     * @param key : compares  the hash value of the file referred by this with every other file's hash value
     * if they are equal new duplicate is generated
     */
    private static void checkDuplicatesInHashMode(String key){
        for(String s : hashValues.keySet()){
            if (!key.equals(s)) {
                String content = hashValues.get(key);
                String currentContent = hashValues.get(s);

                if(content.equals(currentContent)){
                    duplicateFound(new FilePair(key, s));
                }
            }
        }
    }

    /**
     * compare files in content based mode
     * @param key  : compare the content of file referred by this with the content of every other file in different threads
     *             by reading the content line by line
     *             if a mismatch occurs they will ignore in further processing
     *             otherwise if they reach to the end same time they will be considered as a duplicate
     */
    private static void checkDuplicates(final String key) {
        String[] arrFiles = fileContents.keySet().toArray(new String[fileContents.size()]);
        for (final String s : arrFiles) {
            Runnable runnable = new Runnable() {
                public void run() {
                    if (!key.equals(s)) {
                        try {
                            FilePair pair = new FilePair(key, s);
                            /*  if(isAlreadyChecked(pair)){
                               return;
                            }*/ /*else{
                                checkedPairs.add(pair);
                            }*/
                            FileInputStream fstream1 = new FileInputStream(fileContents.get(key));
                            DataInputStream in1 = new DataInputStream(fstream1);
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
                            FileInputStream fstream2 = new FileInputStream(fileContents.get(s));
                            DataInputStream in2 = new DataInputStream(fstream2);
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

                            String strLine1 = br1.readLine();
                            String strLine2 = br2.readLine();
                            boolean  isDifferent = false;
                            while (strLine1 != null || strLine2 != null) {
                                if (strLine1 == null || strLine2 == null) {
                                    isDifferent = true;
                                    break;
                                }else if (!strLine1.equals(strLine2)) {
                                    isDifferent = true;
                                    break;
                                } else{
                                    isDifferent = false;
                                }
                                strLine1 = br1.readLine();
                                strLine2 = br2.readLine();
                            }
                            if(strLine1 == null && strLine2 == null && !isDifferent){
                                duplicateFound(pair);
                            }
                            in1.close();
                            in2.close();
                            checkedPairs.add(pair);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            executor.execute(runnable);
        }
    }

    /**
     *
     * @param newPair this pair will be added to the duplicates list if it not already there
     */
    private static void duplicateFound(FilePair newPair) {
        for (FilePair pair : duplicates) {
            if(pair.equals(newPair)){
                return;
            }
        }
        addDuplicate(newPair);
    }

    /**
     *
     * @param pair this pair will be added to the duplicates list and then writes a new line to the result file in a separate thread
     */
    private static void addDuplicate(FilePair pair) {
        duplicates.add(pair);
        final StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("(");
        resultBuilder.append(pair.getFile1Name());
        resultBuilder.append(",");
        resultBuilder.append(pair.getFile2Name());
        resultBuilder.append(");");
        resultBuilder.append("\n");
        Runnable wrtiteRunable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!aborted) {
                        SearchFileWriter.writeDuplicates(resultPath, resultBuilder.toString());
                    } else {
                        while (aborted) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        executor.execute(wrtiteRunable);
    }

    /**
     *
     * @param resultPath sets the value of result file path
     */
    public static void setResultPath(String resultPath) {
        SearchDuplicateContext.resultPath = resultPath;
    }

    /**
     *
     * @param folderPath sets the path of root directory
     */
    public static void setFolderPath(String folderPath) {
        SearchDuplicateContext.folderPath = folderPath;
    }

    /**
     *
     * @param threadCount sets search thread count
     */
    public static void setThreadCount(int threadCount) {
        SearchDuplicateContext.threadCount = threadCount;
    }

    /**
     *
     * @return  <true>if the operation is already aborted</true>
     */
    public static boolean isAborted() {
        return aborted;
    }

    /**
     *
     * @return <true>if searching is in progress</true>
     */
    public static boolean isSearching() {
        return searching;
    }

    /**
     * stop all current operations before starts a new search
     */
    public static void stopCurrentOperation() {
        if (searching || aborted) {
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * abort the searching in progess or resume the aborted process
     */
    public static void abortOrResumeCurrentOperation() {
        if (searching) {
            aborted = true;
            searching = false;
            System.out.println("Aborted....");
        } else if (aborted) {
            aborted = false;
            searching = true;
            System.out.println("Resumed...");
        }
    }

    /**
     * display the current result
     */
    public static void viewResult() {
        System.out.println(SearchFileReader.readResultFile(resultPath));
    }

    /**
     * sets the operation mode
     * @param mode <0> content based mode</0>
     *             <1> hash based mode</1>
     */
    public static void setMode(int mode) {
        SearchDuplicateContext.mode = mode;
    }
}
