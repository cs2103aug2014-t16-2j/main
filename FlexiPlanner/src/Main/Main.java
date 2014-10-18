package Main;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import UI.*;

public class Main {
	public static void main(String args[]) {

		FlexiPlannerUI userInterface = new FlexiPlannerUI();
		try {
			userInterface.loadInterfaceandData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end of main
}