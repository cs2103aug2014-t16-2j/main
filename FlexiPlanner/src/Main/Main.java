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
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Tray systemTray = new Tray(userInterface, userInterface.getJFrame());
		systemTray.createSystemTray();
	}
}