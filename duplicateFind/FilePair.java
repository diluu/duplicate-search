package duplicateFind;

/**
 * This class defines the structure of a pair of files
 *
 */
public class FilePair {
    private String file1Name;
    private String file2Name;

    public FilePair(String file1Name, String file2Name) {
        this.file1Name = file1Name;
        this.file2Name = file2Name;
    }

    public String getFile1Name() {
        return file1Name;
    }

    public String getFile2Name() {
        return file2Name;
    }

    @Override
    /** here the equals methods returns true if the two file names are equal to the
     * @param obj 's  file names in any order
     *
     */
    public boolean equals(Object obj) {
        if (obj instanceof FilePair) {
            FilePair newPair = (FilePair) obj;
            if(newPair.file1Name.equals(file1Name)){
                  if(newPair.file2Name.equals(file2Name)){
                      return true;
                  }
            } else if(newPair.file2Name.equals(file1Name)){
                  if(newPair.file1Name.equals(file2Name)){
                       return true;
                  }
            }
        }
        return false;
    }
}
