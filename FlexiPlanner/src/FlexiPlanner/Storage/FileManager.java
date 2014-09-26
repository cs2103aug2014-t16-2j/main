package FlexiPlanner.Storage;

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author A0117989H
 *
 */

public class FileManager {
	
	public static void create(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
	}
	
	public static void writeJson(String filePath, JSONObject jsonObj, boolean isAppendable) throws IOException, FileNotFoundException {
		write(filePath, JsonFormatter.toPrettyFormat(jsonObj), isAppendable);
	}
	
	public static void write(String filePath, String content, boolean isAppendable) throws IOException, FileNotFoundException {
		BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath, isAppendable));
		bWriter.write(content);
		bWriter.flush();
		bWriter.close();
	}
	
	public static JSONObject readJson(String filePath) throws FileNotFoundException, IOException, ParseException{
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new BufferedReader(new FileReader(filePath)));
		return jsonObj;
	}
	
	public static ArrayList<String> read(String filePath) throws FileNotFoundException, IOException {
		ArrayList<String> listToReturn = new ArrayList<String>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		
		while ((line = reader.readLine()) != null) {
			listToReturn.add(line);
		}
		
		reader.close();
		
		return listToReturn;
	}
	
	public static void copy(String from, String to) throws IOException, FileNotFoundException {
		InputStream is = new FileInputStream(new File(from));
		OutputStream os = new FileOutputStream(new File(to));
		
		byte[] buffer = new byte[1024];
		int length;
		
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		
		is.close();
		os.close();
	}
	
	public static boolean delete(String filePath) throws FileNotFoundException {
		File fileToDelete = new File(filePath);
		return fileToDelete.delete();
	}
	
	public static String[] listFilesIn(String folderPath) throws FileNotFoundException {
		File dir = new File(folderPath);
		return dir.list();
	}
	
	public static boolean isEmptyFile(String filePath) throws IOException {
		boolean isEmpty = true;
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		isEmpty = reader.readLine() == null;
		reader.close();
		return isEmpty;
	}
}
