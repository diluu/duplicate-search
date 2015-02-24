package duplicateFind;

import java.util.Scanner;

/**
 * this is the main class of this program
 * which is a console application
 *
 */
public class ConsoleApplication {

    public static void main(String[] args){
        new ConsoleApplication();
    }

    /**
     * this reads the input from console to choose an operation
     *  <ul>
     *  <li>Start New Search</li>
     *  <li>View Current Result</li>
     *  <li>Abort Current Search</li>
     *  <li>Resume Previous Search</li>
     *  <li>Exit</li>
     *  </ul>
     *  if new search option is selected, then needs to enter
     *  <ul>
     *      <li>Folder Path</li>
     *      <li>Result File Path</li>
     *      <li>Search Thread Count</li>
     *      <li>Mode</li>
     *  </ul>
     *  <li>Mode</li> can be either <li>Content Based</li>  or
     *  <li>Hash Based</li>
     *
     */
    public ConsoleApplication() {
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("========================= Find Duplicates ================");
            System.out.println("Select an Option...");
            System.out.println("(1) Start New Search");
            if(SearchDuplicateContext.isSearching()){
                System.out.println("(2) Abort Current Search");
                System.out.println("(3) View Current Result");
                System.out.println("(4) Exit");
            } else if(SearchDuplicateContext.isAborted()){
                System.out.println("(2) Resume Previous Search");
                System.out.println("(3) View Current Result");
                System.out.println("(4) Exit");
            } else{
                System.out.println("(2) View Current Result");
                System.out.println("(3) Exit");
            }
            int option = 0;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice...");
            }
            switch (option){
                case 1:
                    SearchDuplicateContext.stopCurrentOperation();
                    System.out.println("Enter Folder Path: ");
                    SearchDuplicateContext.setFolderPath(scanner.nextLine());
                    System.out.println("Enter Result File Path: ");
                    SearchDuplicateContext.setResultPath(scanner.nextLine());
                    System.out.println("Enter Search Thread Count");
                    int threadCount;
                    try {
                        threadCount = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        threadCount = 25;
                    }
                    SearchDuplicateContext.setThreadCount(threadCount);
                    System.out.println("Enter Mode");
                    System.out.println("(1) Content Based");
                    System.out.println("(2) Hash Based");
                    int mode;
                    try {
                        mode = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        mode = 0;
                    }
                    SearchDuplicateContext.setMode(mode - 1);
                    SearchDuplicateContext.startNewSearch();
                    break;
                case 2:
                    if(SearchDuplicateContext.isSearching() || SearchDuplicateContext.isAborted()){
                       SearchDuplicateContext.abortOrResumeCurrentOperation();
                    }  else{
                        SearchDuplicateContext.viewResult();
                    }
                    break;
                case 3:
                    if(SearchDuplicateContext.isSearching() || SearchDuplicateContext.isAborted()){
                      SearchDuplicateContext.viewResult();
                    } else{
                        System.exit(0);
                    }
                    break;
                case 4:
                    if(SearchDuplicateContext.isSearching() || SearchDuplicateContext.isAborted()){
                       System.exit(0);
                    } else{
                        System.out.println("Invalid Choice...");
                    }
                    break;
                default:
                    System.out.println("Invalid Choice...");
            }

        }
    }
}
