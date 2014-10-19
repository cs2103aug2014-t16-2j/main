package Logic;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;


public class UICall {

	Logic logic;

	public UICall () throws FileNotFoundException, IOException, ParseException {
		logic = new Logic();
	}

	public String[] executeInputCommand(String command) throws IOException, ParseException {

		logic.execute(command);
		String[] a = {command +" Successful"};
		return a;
	}

	public String getData(String s) throws IOException, ParseException {		
		if (s.toLowerCase().startsWith("search")) {
			return logic.searchRes;
		}
		return logic.dataToShow();
	}

	public boolean hasTask(String date) throws IOException, ParseException{
		return logic.hasTask(date);
	}
	
	public String getOverdue() {
		return logic.getOverdue();
	}

	public String getTodayTask() throws IOException, ParseException {
		return logic.getTodayTask();
	}

}
