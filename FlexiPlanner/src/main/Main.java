package main;

import java.io.IOException;

//import javax.swing.SwingUtilities;


import java.time.LocalDateTime;

import org.json.simple.parser.ParseException;

import storage.ReminderPatternParser;
import storage.TaskData;
import ui.*;

public class Main {
	public static void main(String args[]) {
		/*final LocalDateTime ltd1 = LocalDateTime.of(2014, 11, 1, 18, 59);
		ReminderPatternParser rpp = new ReminderPatternParser();
		Object obj = rpp.parse("Meet with boss at 10am this is a very long label \"remind me 1mins before\"");
		
		TaskData t1 = new TaskData("Meet with boss at 10am this is a very long label \"remind me 1mins before\"");
		
		System.out.println(t1.getReminder() == null);

		t1.setRemindDateTime(ltd1.minusMinutes((Integer) obj));

		t1.setReminder();

		while (true) {
			
		}*/

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
