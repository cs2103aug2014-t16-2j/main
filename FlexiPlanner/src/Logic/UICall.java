package Logic;

import java.io.FileNotFoundException;
import java.io.IOException;


public class UICall {
	
	Logic logic;
	
	public UICall () throws FileNotFoundException, IOException {
		logic = new Logic();
	}
	
	public void executeInputCommand(String command) {
		logic.execute(command);
	}
	
	public String getData() {
		return null;
	}
	

}
