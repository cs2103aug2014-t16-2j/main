package main;

import ui.*;

public class Main {
	public static void main(String args[]) {

		FlexiPlannerUI userInterface = new FlexiPlannerUI();
		Tray systemTray = new Tray(userInterface);
		
		userInterface.loadUI();
		systemTray.createSystemTray();
	}
}
