
import java.util.List;
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

	public File file = new File("");
	final String MESSAGE_ADDED = "added to ";
	final String MESSAGE_DELETED = "deleted from ";
	final String MESSAGE_COMMAND_INPUT = "command: ";
	final String MESSAGE_CLEAR_LIST = "all content deleted from ";
	final String MESSAGE_ERROR = "Error occured. try again.";
	final String MESSAGE_INVALID_COMMAND = "invalid command.";
	final String MESSAGE_WELCOME = "Welcome to TextBuddy. ";
	final String MESSAGE_FILE_NOT_FOUND = "File not found";
	final String MESSAGE_EMPTY = " is empty";
	final String MESSAGE_NOTHING_DELETE = "Nothing to delete";
	final String MESSAGE_FILE_EXIST = "File already exists. Please try with a different file name.";
	final String MESSAGE_READY_TO_USE = " is ready for use";
	final String MESSAGE_SORTED = " is sorted";
	final String MESSAGE_NOTHING_TO_SORT = "Nothing to sort";
	final String NOTHING_FOUND = "Nothing to search";
	final String CANNOT_FIND = "Content cannot be found";

	Scanner sc;

	//To display all task or show empty if no task
	void display() {
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

	//To display all task or show empty if no task
	List<String> getData() {
		List<String> list = new ArrayList<String>();
		if (file.length() == 0) {
			System.out.println(file.getName() + MESSAGE_EMPTY);
		} else {
			try {//if file found
				BufferedReader in = new BufferedReader(new FileReader(file.getName()));
				String note1;
				//read text file
				while ((note1 = in.readLine()) != null) {//read till no information
					list.add(note1);
				}
				in.close();
			} catch (Exception event) {
				System.out.println(MESSAGE_ERROR);
				return null;
			}//if no such file is found
		}
		return list;
	}

	//To add a task
	void add(String note) {
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
	void delete(int del) {
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

	void sort() {
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
		}
	}

	public class SortIgnoreCase implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.toLowerCase().compareTo(s2.toLowerCase());
		}
	}

	String search(String input) {
		int containOrNot = 0;
		String forTesting= "";
		ArrayList<String> tempStorage = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file.getName()));
			getContentInTextFile(in, tempStorage);
		} catch (Exception event) {
			System.out.println(MESSAGE_ERROR);
			return MESSAGE_ERROR;//This is for testing
		}
		if (tempStorage.isEmpty()) {
			System.out.println(NOTHING_FOUND);
			return NOTHING_FOUND;//This is for testing
		} else {
			for (int a = 0; a < tempStorage.size(); a++) {
				if (tempStorage.get(a).toLowerCase().contains(input.toLowerCase())) {
					containOrNot=1;
					forTesting=forTesting+tempStorage.get(a);
					System.out.println(tempStorage.get(a));
				}
			}
			if(containOrNot==0){
				System.out.println(CANNOT_FIND);           	
				return CANNOT_FIND;//This is for testing
			}else if(containOrNot==1){
				return forTesting;
			}
		}        
		return "";//This is for testing
	}

	void updateFileWithDeletedContent(ArrayList<String> tempStorage, int del) {
		tempStorage.remove(del - 1);
		clear();
		updateFile(tempStorage);
	}

	void updateFile(ArrayList<String> tempStorage) {
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

	void getContentInTextFile(BufferedReader in, ArrayList<String> tempStorage) throws IOException {
		String temp;
		while ((temp = in.readLine()) != null) {
			tempStorage.add(temp);
		}
		in.close();
	}

	//To clear the file
	void clear() {
		try {
			PrintWriter writer = new PrintWriter(file.getName());
			writer.print("");
			writer.close();
		} catch (Exception event) {
			System.out.println(MESSAGE_FILE_NOT_FOUND);
		}
	}

	public TextBuddy(String[] args){
		sc = new Scanner(System.in);        
		String fileName = args[0];
		file = new File(fileName);
		System.out.println(MESSAGE_WELCOME + file.getName() + MESSAGE_READY_TO_USE);           	
	}

	public static void main(String[] args) {
		TextBuddy tb = new TextBuddy(args);
		tb.startUp();
	}

	void startUp() {
		String userInput;
		for (;;) {//this loop will carry on till exit command is given
			System.out.print(MESSAGE_COMMAND_INPUT);
			userInput = sc.nextLine();
			executeUserInput(userInput);
		}
	}

	void executeUserInput(String userInput) throws NumberFormatException {
		if (userInput.startsWith("display")) {
			display();
		} else if (userInput.startsWith("add")) {
			add(userInput.substring(4));
		} else if (userInput.startsWith("delete")) {
			checkIfValidDeleteThenExecute(userInput);
		} else if (userInput.startsWith("clear")) {
			clear();
			System.out.println(MESSAGE_CLEAR_LIST + file.getName());
		} else if (userInput.startsWith("sort")) {
			sort();
		} else if (userInput.startsWith("search")) {
			checkIfValidSearchThenExecute(userInput);
		} else if (userInput.equals("exit")) {
			System.exit(0);
		} else {
			System.out.println(MESSAGE_INVALID_COMMAND);
		}
	}

	void checkIfValidSearchThenExecute(String userInput) {
		if(userInput.length()==6){
			System.out.println(MESSAGE_ERROR);
		}else{
			search(userInput.substring(7));
		}
	}

	void checkIfValidDeleteThenExecute(String userInput) {
		if(userInput.length()==6){
			System.out.println(MESSAGE_ERROR);
		}else{
			int del = Integer.parseInt(userInput.substring(7));
			delete(del);
		}
	}

}
