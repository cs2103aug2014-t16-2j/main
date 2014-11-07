package storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//@author A0117989H

/**
 * This class handles file related operations.
 *
 */

public class FileManager {
	
	private final int ONE_KILO_BYTE = 1024;
	
	private final String FILE_NAME_PATTERN = "^[\\w,\\s-]+$";
	private final String VALID_EXTENSION_TASKS_FILE = "json";
	private final String VALID_EXTENSION_NORMAL_FILE = "txt";
	private final String NOTHING = "";
	private final String SEPERATOR = "//";
	
	public boolean createFolder(String folderName) throws IOException {
		boolean isCreated = false;
		
		File folder = new File(folderName);
		
		if (folder.exists() && folder.isFile()) {
			return isCreated;
		}
		else if (!folder.exists()) {
			folder.mkdir();
			isCreated = true;
		}
		else {
			isCreated = true;
		}
		
		return isCreated;
	}
	
	/** ******************** **/
	
	public boolean createFile(String filePath) throws IOException {
		boolean isCreated = false;

		File file = new File(filePath);
		
		if (!file.exists()) {
			file.createNewFile();
			isCreated = true;
		}
		else {
			isCreated = false;
		}
		
		return isCreated;
	}
	
	/** ******************** **/
	
	public void writeInJsonFormat(String filePath, JSONObject jsonObj, boolean isAppendable) throws IOException, FileNotFoundException {
		write(filePath, new JsonConverter().toPrettyFormat(jsonObj), isAppendable);
	}
	
	/** ******************** **/
	
	public void write(String filePath, String content, boolean isAppendable) throws IOException, FileNotFoundException {
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath, isAppendable));
		bWriter.write(content);
		bWriter.flush();
		bWriter.close();
	}
	
	/** ******************** **/
	
	public JSONObject readInJsonFormat(String filePath) throws FileNotFoundException, IOException, ParseException{
		if (isEmptyFile(filePath)) {
			return new JSONObject();
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new BufferedReader(new FileReader(filePath)));
		
		return jsonObj;
	}
	
	/** ******************** **/
	
	public ArrayList<String> read(String filePath) throws IOException {
		ArrayList<String> listToReturn = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		
		while ((line = reader.readLine()) != null) {
			listToReturn.add(line);
		}
		
		reader.close();
		
		return listToReturn;
	}
	
	/** ******************** **/
	
	public boolean isValidFileName(final String fileName) {
		if (fileName == null) {
			return false;
		}
		
		if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase(VALID_EXTENSION_TASKS_FILE) &&
			!FilenameUtils.getExtension(fileName).equalsIgnoreCase(VALID_EXTENSION_NORMAL_FILE)) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(FILE_NAME_PATTERN);
		Matcher matcher = pattern.matcher(FilenameUtils.getBaseName(fileName));
		
		return matcher.matches();
	}
	
	/** ******************** **/
	
	public void copyFile(String from, String to) throws IOException, FileNotFoundException {
		InputStream is = new FileInputStream(new File(from));
		OutputStream os = new FileOutputStream(new File(to));
		
		byte[] buffer = new byte[ONE_KILO_BYTE];
		int length;
		
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		
		is.close();
		os.close();
	}
	
	/** ******************** **/
	
	public boolean deleteFile(String filePath) throws FileNotFoundException {
		File fileToDelete = new File(filePath);
		return fileToDelete.delete();
	}
	
	/** ******************** **/
	
	public void clearFile(String filePath) throws FileNotFoundException, IOException {
		write(filePath, NOTHING, false);
	}
	
	/** ******************** **/
	
	public boolean isEmptyFile(String filePath) throws IOException {
		boolean isEmpty = true;
		
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		isEmpty = reader.readLine() == null;
		
		reader.close();
		
		return isEmpty;
	}
	
	/** ******************** **/
	
	public boolean hasFolder(String folderName) {
		File folder = new File(folderName);
		
		return folder.exists() && !folder.isFile();
	}
	
	/** ******************** **/
	
	public String createFilePath(String folderName, final String fileName) {
		return folderName + SEPERATOR + fileName;
	}
	
	/** ******************** **/
	
	public String extractFileName(String filePath) {
		String[] s = filePath.split(SEPERATOR);
		return s[s.length - 1];
	}
	
	/** ******************** **/
	
	public int numOfFilesIn(String folderName) {
		File folder = new File(folderName);
		if (!folder.exists() || (folder.exists() && folder.isFile())) {
			return -1;
		}
		else {
			return folder.list().length;
		}
	}
}
