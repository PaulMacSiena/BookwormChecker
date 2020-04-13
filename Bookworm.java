/**
 * Class that can be used to search for a list of words in a board of the game 'bookworm'. 
 * A word is found if it can be made by connecting adjacent cells in a non-repeating order. 
 * See http://www.crazygames.com/game/bookworm for more details on rules for finding words. 
 *
 * @author Paul Macfarlane
 * @version 1.0
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.IOException;

public class Bookworm
{
    private String[][] board = new String[8][7]; // will hold the board game characters (stored as strings)
    private String[] dictionary; // this consists of the words to check for existence in board
    private String[] line = new String[7]; // used to temporarily store each line of characters to fill the board
    private ArrayList<String> found = new ArrayList<String>(); //arrayList to hold the words that were found on the board

    /**
     * One argument constructor for Bookworm. Initializes the board, dictionary, and line arrays
     * @param fileName, name of the file to read board characters and words to find
     * @throws FileNotFoundException if fileName does not match a file in the directory of java file
     */
    public Bookworm(String fileName) throws FileNotFoundException {

        File inputFile = new File(fileName);
        int fileLines =0; //scan through 1 time to see the amount of words we will need to store in an array
        try {
            Scanner in = new Scanner (inputFile);

            // count how many lines are in the file
            while (in.hasNext())
            {
                in.nextLine();
                fileLines++;
            } 
            dictionary = new String[fileLines-8];
            //-8 because there are 7 lines for each column of the game,and one that is a space
            in.close();
            in.reset();
        } catch (FileNotFoundException e) {
            System.out.println ("File not found in directory");
            System.exit(0);
        }
        // now scan through a second time
        File inputFile1 = new File(fileName);
        try {
            Scanner inFile = new Scanner(inputFile1);
            //read in each line of characters
            for (int i =0; i<=6; i++){
                line[i] = inFile.nextLine();
            }
            inFile.nextLine(); // skip the blank line

            // fill the even columns
            for (int col =0; col <7;col = col +2){      

                for (int row =0; row < line[col].length();  row++){               
                    if(line[col].substring(row,row+1).equals("Q")){ // take care of the 'Q' case
                        board[row][col] = "QU";
                    }
                    else {
                        board[row][col]= line[col].substring(row,row+1);
                    }
                }
            }

            // fill the odd columns
            for (int col =1; col <6;col = col +2){      
                for (int row =0; row < line[col].length(); row++){               
                    if(line[col].substring(row,row+1).equals("Q")){ // take care of the 'Q' case
                        board[row][col] = "QU";
                    }
                    else {
                        board[row][col]= line[col].substring(row,row+1);
                    }
                }
            }

            for (int row =0; row <board.length;row++){
                for (int col =0; col < 7; col++){
                    if (board[row][col] ==null){
                        board[row][col] = "";
                    }
                }
            }
            // a printing loop for the sake if visualization of the board
            /*
            for (int row =0; row <board.length;row++){
            for (int col =0; col < 7; col++){
            if (board[row][col] ==null){
            board[row][col] = "";
            }
            else if (col%2==1 && row == 7){
            System.out.print( "   [" +board[row][col] + "]" );
            }
            else{
            System.out.print("["+ board[row][col] +"]");
            }
            }
            System.out.println();
            }
             */

            // fill dictionary with words to check for 
            int i =0;
            while (inFile.hasNext()){
                dictionary[i] =inFile.nextLine();
                i++;
            }
            inFile.close();
            inFile.reset();
        }
        catch (FileNotFoundException e) {
            System.out.println ("File not found in directory");
            System.exit(0);
        }

    }

    /**
     * Recursive helper function. This searches for the first letter of the word to check for, then calls 
     * the main recursive function on that words without the first character included. 
     * @param word The word to check for
     * @return true if then word is found in the board, else return false
     */
    public boolean findWord(String word){

        for (int i =0; i<=7; i++){
            for (int j =0; j<7; j++){

                if (j%2==0 && i==7 ) {
                    //skip these indexes, because they are not part of the board
                }
                else{ 
                    if (word.startsWith("QU")){ // special casw where the word starts with a QU
                        if (board[i][j].equals("QU")){
                            boolean temp = findWord(word.substring(2),i,j, new boolean[8][7]); // if this returns true, we are done, else check next locations
                            if (temp) return true;
                        }
                    }
                    else if (word.startsWith(board[i][j])){ 
                        boolean temp = findWord(word.substring(1),i,j, new boolean [8][7]); // if this returns true, we are done, else check next locations
                        if (temp) return true;
                    }
                }
            }

        }
        // if we have checked every index, and not found the word, we can safely return false
        return false;
    }

    /**
     * Main recursive function. Works by checking if adjacent indexes contain the letters that the word starts with.
     * The word continously shrinks until it reaches a length of 0. If it gets to this point it is found.
     * @param word The word to look for
     * @param row The current row in the board that we are checking in
     * @param col the current column in the board that we are checking in
     * @param checked A boolean matrix which keeps track of which indices have been checked
     * @return True if the word has been found, else false
     */
    public boolean findWord(String word, int row, int col, boolean[][] checked){
        checked[row][col]=true; // mark this index as checked
        if(word.length()==0) return true; //word was found
        else if(row < 0 || row >7 || (row >6 && col%2==0) || col<0 || col >6) return false; // we are off the board

        else if (word.length()==1){ // this happens when there is only one char left in the word.
            // if even, check all the adjacents, 
            if (col%2==0){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(1),row-1,col,checked)) return true; //checked above
                    checked[row-1][col] =false; //can reset this particular index to be unchecked
                }
                if (row+1 <7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(1),row+1,col,checked)) return true;//check below
                    checked[row+1][col] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(1),row,col-1,checked)) return true;//check left above
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 && word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(1),row,col+1,checked)) return true; //check right above
                    checked[row][col+1] =false;
                }
                if (row +1 < 7 && col -1 >=0 && word.startsWith(board[row+1][col-1]) && !checked[row+1][col-1]){ 
                    if (findWord(word.substring(1),row+1,col-1,checked)) return true; //check left below
                    checked[row+1][col-1] =false;
                }
                if (row + 1 <7 && col +1<=6 && word.startsWith(board[row+1][col+1]) && !checked[row+1][col+1]){ 
                    if (findWord(word.substring(1),row+1,col+1,checked)) return true;//check right below
                    checked[row+1][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                // change the checked back to false
                checked[row][col] =false;
                return false;
            }
            //if odd check all the adjacents 
            else{ //(col%2==1){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(1),row-1,col,checked)) return true; //check above
                    checked[row-1][col] =false;
                }
                if (row +1 <=7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(1),row+1,col,checked)) return true; //check below
                    checked[row+1][col] =false;
                }
                if (row -1 >=0 && col -1 >=0 && word.startsWith(board[row-1][col-1]) && !checked[row-1][col-1]){ 
                    if (findWord(word.substring(1),row-1,col-1,checked)) return true; //check left above
                    checked[row-1][col-1] =false;
                }
                if (row -1 >=0 && col+1<=6 && word.startsWith(board[row-1][col+1]) && !checked[row-1][col+1]){ 
                    if (findWord(word.substring(1),row-1,col+1,checked)) return true; //check right above
                    checked[row-1][col+1] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(1),row,col-1,checked)) return true;//check left below
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 &&word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(1),row,col+1,checked)) return true; //check right below
                    checked[row][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                checked[row][col] =false;
                return false;
            }
        }
        else if (word.startsWith("QU")){ // special case with 'QU'
            if (col%2==0){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(2),row-1,col,checked)) return true; //check above
                    checked[row-1][col] =false;
                }
                if (row+1 <7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(2),row+1,col,checked)) return true;//check below
                    checked[row+1][col] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(2),row,col-1,checked)) return true;//check left above
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 && word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(2),row,col+1,checked)) return true; //check right above
                    checked[row][col+1] =false;
                }
                if ( col -1 >=0 && word.startsWith(board[row+1][col-1]) && !checked[row+1][col-1]){ 
                    if (findWord(word.substring(2),row+1,col-1,checked)) return true; //check left below
                    checked[row+1][col-1] =false;
                }
                if (col +1<=6 && word.startsWith(board[row+1][col+1]) && !checked[row+1][col+1]){ 
                    if (findWord(word.substring(2),row+1,col+1,checked)) return true;//check right below
                    checked[row+1][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                checked[row][col] =false;
                return false;
            }
            //if odd check all the adjacents 
            else{ //(col%2==1){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(2),row-1,col,checked)) return true; //check above
                    checked[row-1][col] =false;
                }
                if (row +1 <=7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(2),row+1,col,checked)) return true; //check below
                    checked[row+1][col] =false;
                }
                if (row -1 >=0 && col -1 >=0 && word.startsWith(board[row-1][col-1]) && !checked[row-1][col-1]){ 
                    if (findWord(word.substring(2),row-1,col-1,checked)) return true; //check left above
                    checked[row-1][col-1] =false;
                }
                if (row -1 >=0 && col+1<=6 && word.startsWith(board[row-1][col+1]) && !checked[row-1][col+1]){ 
                    if (findWord(word.substring(2),row-1,col+1,checked)) return true; //check right above
                    checked[row-1][col+1] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(2),row,col-1,checked)) return true;//check left below
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 &&word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(2),row,col+1,checked)) return true; //check right below
                    checked[row][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                checked[row][col] =false;
                return false;
            }

        }
        else{ // the typical case, where we are looking an adjacent letter 
            // if col is even, check all the adjacents, 
            if (col%2==0){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(1),row-1,col,checked)) return true; //check above
                    checked[row-1][col] =false;
                }
                if (row+1 <7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(1),row+1,col,checked)) return true;//check below
                    checked[row+1][col] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(1),row,col-1,checked)) return true;//check left above
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 && word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(1),row,col+1,checked)) return true; //check right above
                    checked[row][col+1] =false;
                }
                if ( col -1 >=0 && word.startsWith(board[row+1][col-1]) && !checked[row+1][col-1]){ 
                    if (findWord(word.substring(1),row+1,col-1,checked)) return true; //check left below
                    checked[row+1][col-1] =false;
                }
                if (col +1<=6 && word.startsWith(board[row+1][col+1]) && !checked[row+1][col+1]){ 
                    if (findWord(word.substring(1),row+1,col+1,checked)) return true;//check right below
                    checked[row+1][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                checked[row][col] =false;
                return false;
            }
            //if odd col check all the adjacents 
            else{ //(col%2==1){
                if (row -1 >=0 && word.startsWith(board[row-1][col]) && !checked[row-1][col]){ 
                    if (findWord(word.substring(1),row-1,col,checked)) return true; //check above
                    checked[row-1][col] =false;
                }
                if (row +1 <=7 && word.startsWith(board[row+1][col]) && !checked[row+1][col]){ 
                    if (findWord(word.substring(1),row+1,col,checked)) return true; //check below
                    checked[row+1][col] =false;
                }
                if (row -1 >=0 && col -1 >=0 && word.startsWith(board[row-1][col-1]) && !checked[row-1][col-1]){ 
                    if (findWord(word.substring(1),row-1,col-1,checked)) return true; //check left above
                    checked[row-1][col-1] =false;
                }
                if (row -1 >=0 && col+1<=6 && word.startsWith(board[row-1][col+1]) && !checked[row-1][col+1]){ 
                    if (findWord(word.substring(1),row-1,col+1,checked)) return true; //check right above
                    checked[row-1][col+1] =false;
                }
                if (col -1 >=0 && word.startsWith(board[row][col-1]) && !checked[row][col-1]){ 
                    if (findWord(word.substring(1),row,col-1,checked)) return true;//check left below
                    checked[row][col-1] =false;
                }
                if (col +1 <=6 &&word.startsWith(board[row][col+1]) && !checked[row][col+1]){ 
                    if (findWord(word.substring(1),row,col+1,checked)) return true; //check right below
                    checked[row][col+1] =false;
                }
                //at this point, nothing was adjacent, return false
                checked[row][col] =false;
                return false;
            }
        }

    }

    public static void main(String[] args)  throws FileNotFoundException{
        if (args.length!=1){
            System.out.print("Enter the name if the file to read from: ");
            Scanner scan = new Scanner(System.in);
            String fileName = scan.next();

            try {
                Bookworm book = new Bookworm(fileName);

                for (String word : book.dictionary){
                    if (book.findWord(word)){
                        System.out.println(word + " was found.");
                    }
                    else{
                        System.out.println(word + " was not found.");
                    }
                }
                //extra test cases
                /*
                String[] test = {"LAWN", "BLUEJ", "BBF", "BET", "VAN","QUTENA", "NENTOFQUTENA"};

                for (String word : test){
                if (book.findWord(word)){
                System.out.println(word + " was found.");
                }
                else{
                System.out.println(word + " was not found.");
                }
                }
                 */

            } catch (FileNotFoundException e) {
                System.out.println ("File not found in directory");
            }
        }
        else {           
            String fileName = args[0];           
            try {
                Bookworm book = new Bookworm(fileName);

                for (String word : book.dictionary){
                    if (book.findWord(word)){
                        System.out.println(word + " was found.");
                    }
                    else{
                        System.out.println(word + " was not found.");
                    }
                }
                //extra test cases
                /*
                String[] test = {"LAWN", "BLUEJ", "BBF", "BET", "VAN","QUTENA", "NENTOFQUTENA"};

                for (String word : test){
                if (book.findWord(word)){
                System.out.println(word + " was found.");
                }
                else{
                System.out.println(word + " was not found.");
                }
                }
                 */

            } catch (FileNotFoundException e) {
                System.out.println ("File not found in directory");
            }

            
            
        }
    }
}

