package main;

import java.io.IOException;

//import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import ui.*;

public class Main {
	public static void main(String args[]) {

		FlexiPlannerUI userInterface = new FlexiPlannerUI();
		
		try {
			userInterface.loadInterfaceandData();
			//userInterface.redirectSystemStreams();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Tray systemTray = new Tray(userInterface, FlexiPlannerUI.getJFrame());
		systemTray.createSystemTray();
	}
}