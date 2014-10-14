package Logic;


import java.io.FileNotFoundException;
import java.io.IOException;


public class UICall {
	
	Logic logic;
	
	public UICall () throws FileNotFoundException, IOException {
		logic = new Logic();
	}
	
	public String[] executeInputCommand(String command) {
		
		logic.execute(command);
		String[] a = {command +" Successful"};
		return a;
	}
	
	public String getData(String s) {
		if (s.isEmpty()) {
		return logic.dataToShow();
		} else if (s.equalsIgnoreCase("search")) {
			return logic.searchRes;
		}
		return null;
	}
	
	public boolean hasTask(String date){
		return logic.hasTask(date);
	}
	

}
