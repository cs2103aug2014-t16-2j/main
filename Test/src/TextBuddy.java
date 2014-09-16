
import static org.junit.Assert.assertEquals;//This is used for testing purposes
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/*
 * This is a TextBuddy whereby user will be able to add, delete, fully clear, sort or search the text file created.
 For add: add _______ 
 For delete: delete 1 (The number will be shown when display is entered)
 For display: display 
 For sort: sort
 For search: search _______
 If same file name exist, program will straight aways exit, you need to create a file which does not exist in your memory
 */

/**
 *
 * @author Eugene Tan Teck Li
 */
public class TextBuddy {

    public static File file = new File("");
    private static final String MESSAGE_ADDED = "added to ";
    private static final String MESSAGE_DELETED = "deleted from ";
    private static final String MESSAGE_COMMAND_INPUT = "command: ";
    private static final String MESSAGE_CLEAR_LIST = "all content deleted from ";
    private static final String MESSAGE_ERROR = "Error occured. try again.";
    private static final String MESSAGE_INVALID_COMMAND = "invalid command.";
    private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. ";
    private static final String MESSAGE_FILE_NOT_FOUND = "File not found";
    private static final String MESSAGE_EMPTY = " is empty";
    private static final String MESSAGE_NOTHING_DELETE = "Nothing to delete";
    private static final String MESSAGE_FILE_EXIST = "File already exists. Please try with a different file name.";
    private static final String MESSAGE_READY_TO_USE = " is ready for use";
    private static final String MESSAGE_SORTED = " is sorted";
    private static final String MESSAGE_NOTHING_TO_SORT = "Nothing to sort";
    private static final String NOTHING_FOUND = "Nothing to search";

    //To display all task or show empty if no task
    private static void display() {
        int noOfLines = 0;
        if (file.length() == 0) {
            System.out.println(file.getName() + MESSAGE_EMPTY);
        } else {
            try {//if file found
                BufferedReader in = new BufferedReader(new FileReader(file.getName()));
                String note1;
                //read text file
                while ((note1 = in.readLine()) != null) {//read till no information
                    noOfLines++;
                    System.out.println(noOfLines + ": " + note1);
                }
                in.close();
            } catch (Exception event) {
                System.out.println(MESSAGE_ERROR);
            }//if no such file is found
        }
    }

    //To add a task
    private static void add(String note) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getName(), true));
            if (note != null) {
                if (file.length() != 0) {
                    out.newLine();
                }
                out.write(note);//edit file(write into text file)                        
                System.out.println("\"" + note + "\" " + MESSAGE_ADDED + file.getName());
            }
            out.close();//close file

        } catch (Exception event) {
            System.out.println(MESSAGE_ERROR);
        }

    }

    //To delete a task
    private static void delete(int del) {
        ArrayList<String> tempStorage = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getName()));
            getContentInTextFile(in, tempStorage);
            System.out.println(MESSAGE_DELETED + file.getName() + " \"" + tempStorage.get(del - 1) + "\"");
            updateFileWithDeletedContent(tempStorage, del);
        } catch (Exception event) {
            System.out.println(MESSAGE_NOTHING_DELETE);
        }//if no such file is found or nothing in file
    }

    private static void sort() {
        ArrayList<String> tempStorage = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getName()));
            getContentInTextFile(in, tempStorage);
        } catch (Exception event) {
            System.out.println(MESSAGE_ERROR);
        }
        if (tempStorage.isEmpty()) {
            System.out.println(MESSAGE_NOTHING_TO_SORT);
        } else {
            Collections.sort(tempStorage, new SortIgnoreCase());//this sort is to ignore case
            clear();
            updateFile(tempStorage);
            System.out.println(file.getName() + MESSAGE_SORTED);
            //This assert below is used for testing purposes
            /*ArrayList<String> expectedArray= new ArrayList<String>();
            expectedArray.add("a");
            expectedArray.add("b");
            expectedArray.add("c");
            assertEquals(expectedArray,tempStorage);*/
        }
    }

    public static class SortIgnoreCase implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    private static void search(String input) {
    	int containOrNot = 0;
        ArrayList<String> tempStorage = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getName()));
            getContentInTextFile(in, tempStorage);
        } catch (Exception event) {
            System.out.println(MESSAGE_ERROR);
        }
        if (tempStorage.isEmpty()) {
            System.out.println(NOTHING_FOUND);
        } else {
            Collections.sort(tempStorage);
            for (int a = 0; a < tempStorage.size(); a++) {
                if (tempStorage.get(a).toLowerCase().contains(input.toLowerCase())) {
                	containOrNot=1;
                	System.out.println(tempStorage.get(a));
                    assertEquals("finally you found me",tempStorage.get(a).toLowerCase());
                }
            }
        }
        if(containOrNot==0){
        	System.out.println(NOTHING_FOUND);
        }
    }

    private static void updateFileWithDeletedContent(ArrayList<String> tempStorage, int del) {
        tempStorage.remove(del - 1);
        clear();
        updateFile(tempStorage);
    }

    private static void updateFile(ArrayList<String> tempStorage) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file.getName(), true));
            for (int a = 0; a < tempStorage.size(); a++) {
                out.write(tempStorage.get(a));
                if (a < tempStorage.size() - 1) {
                    out.newLine();
                }
            }
            out.close();
        } catch (Exception event) {
            System.out.println(MESSAGE_ERROR);
        }
    }

    private static void getContentInTextFile(BufferedReader in, ArrayList<String> tempStorage) throws IOException {
        String temp;
        while ((temp = in.readLine()) != null) {
            tempStorage.add(temp);
        }
        in.close();
    }

    //To clear the file
    private static void clear() {
        try {
            PrintWriter writer = new PrintWriter(file.getName());
            writer.print("");
            writer.close();
        } catch (Exception event) {
            System.out.println(MESSAGE_FILE_NOT_FOUND);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String userInput = new String();
        file = new File("test.txt");
        System.out.print(MESSAGE_WELCOME);
        checkFileExist();
        for (;;) {//this loop will carry on till exit command is given
            System.out.print(MESSAGE_COMMAND_INPUT);
            userInput = sc.nextLine();
            executeUserInput(userInput);
        }
    }

    //Will exit if file exist
    private static void checkFileExist() {
        try {
            if (file.createNewFile()) {
                System.out.println(file.getName() + MESSAGE_READY_TO_USE);
            } else {
                System.out.println(MESSAGE_FILE_EXIST);
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println(MESSAGE_ERROR);
            System.exit(0);
        }
    }

    private static void executeUserInput(String userInput) throws NumberFormatException {
        if (userInput.startsWith("display")) {
            display();
        } else if (userInput.startsWith("add")) {
            add(userInput.substring(4));
        } else if (userInput.startsWith("delete")) {
            int del = Integer.parseInt(userInput.substring(7));
            delete(del);
        } else if (userInput.startsWith("clear")) {
            clear();
            System.out.println(MESSAGE_CLEAR_LIST + file.getName());
        } else if (userInput.startsWith("sort")) {
            sort();
        } else if (userInput.startsWith("search")) {
            search(userInput.substring(7));
        } else if (userInput.equals("exit")) {
            System.exit(0);
        } else {
            System.out.println(MESSAGE_INVALID_COMMAND);
        }
    }

}
