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
	
	public String getData() {
		return logic.dataToShow();
	}
	
	public boolean hasTask(String date){
		return logic.hasTask(date);
	}
	

}
