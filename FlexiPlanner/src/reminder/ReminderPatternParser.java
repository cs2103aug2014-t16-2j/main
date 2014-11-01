package reminder;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is to parse user's command to set reminder for the to-do tasks.
 * 
 * @author Moe Lwin Hein (A0117989)
 */

/**
 * Users are able to enter commands to set reminder as below:
 * 
 * Sample commands:
 * command 1 : collect movie ticket from box office tomorrow "remind me 3hours before"
 * command 2 : tutorial paper submission tomorrow "set reminder 1h before"
 * command 3 : production meeting on 10 November "9 nov 8am"
 * command 4 : shop groceries for kitchen "remind me 10-11-14 0700" (dd-mm-yy) or (dd-mm-yyyy)
 * command 5 : attend customer meeting next week "reminder me 10/11/14 0830am"
 * command 6 : submit report "reminder me on 10/11/14 8:30am"
 *
 */

public class ReminderPatternParser {
	
	private final String INFO_NO_COMMAND_FOR_REMINDER = "Command to set reminder is not found!\n";
	
	private final String ERROR_INVALID_DATE_TIME = "Invalid date and time!\n";
	
	private final int NO_PATTERN = 0;
	private final int R_PATTERN_1 = 1;
	private final int R_PATTERN_2 = 2;
	private final int R_PATTERN_3 = 3;
	private final int YEAR_OF_TODAY = 2000;
	
	private final String PATTERN_1 = "(?i).*\\s*\"{1}(\\s*\\w*\\s+)*(\\d{1,2})\\s*"
			+ "(m|min|mins|minute|minutes|h|hr|hrs|hour|hours)(\\s+\\w*)*\"{1}(\\s+.*)*";
	
	private final String PATTERN_2 = "(?i).*\\s*\"{1}(\\s*\\w*\\s+)*(\\d{1,2})\\s*"
			+ "(jan|january|feb|february|mar|march|apr|april|may|jun|june|jul|july"
			+ "|aug|august|sep|september|oct|october|nov|november|dec|december|)"
			+ "\\s+(\\d{1,2})(:)?(\\d{1,2})?\\s*(am|pm|)(\\s+\\w*)*\"{1}(\\s+.*)*";
	
	private final String PATTERN_3 = "(?i).*\\s*\"{1}(\\s*\\w*\\s+)*(\\d{1,2})(\\-|\\/|\\\\)"
			+ "(\\d{1,2})(\\-|\\/|\\\\)(\\d{1,2})(\\d{1,2})?\\s*(\\d{1,2})(:)?(\\d{1,2})?\\s*"
			+ "(am|pm|)(\\s+\\w*)*\"{1}(\\s+.*)*";
	
	private final String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
	private final List<String> monthList = (List<String>) Arrays.asList(months);
	
	private Pattern pattern1;
	private Pattern pattern2;
	private Pattern pattern3;
	
	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	
	private Matcher matcher;
	
	private LocalDateTime reminderDateTime;
	private Integer reminderMinutes;
	
	public ReminderPatternParser() {
		pattern1 = Pattern.compile(PATTERN_1);
		pattern2 = Pattern.compile(PATTERN_2);
		pattern3 = Pattern.compile(PATTERN_3);
	}
	
	public boolean hasReminderPatternInCommand(final String commandStr) {
		return findReminderPattern(commandStr) != NO_PATTERN;
	}
	
	public Object parse(final String commandStr) {
		int reminderPattern = findReminderPattern(commandStr);
		
		if (reminderPattern == NO_PATTERN)
		{
			report(INFO_NO_COMMAND_FOR_REMINDER);
			
			return null;
		}
		
		if (!isValidDateTime(reminderPattern, commandStr)) {
			report(ERROR_INVALID_DATE_TIME);
			
			return null;
		}
		
		if (reminderPattern == R_PATTERN_1) {
			return reminderMinutes;
		}
		else {
			reminderDateTime = LocalDateTime.of(year, month, day, hour, minute);
			
			return reminderDateTime;
		}
	}
	
	private boolean isValidDateTime(int reminderPattern, final String commandStr) {
		boolean isValid = false;
		
		switch (reminderPattern) {
		
		case 1: 
			matcher = pattern1.matcher(commandStr);
			matcher.find();
			
			if (matcher.group(3).substring(0, 1).equals("m")) {
				reminderMinutes = Integer.valueOf(matcher.group(2));
				isValid = true;
			}
			else {
				reminderMinutes = Integer.valueOf(matcher.group(2)) * 60;
				isValid = true;
			}
			
			break;
			
		case 2: 
			matcher = pattern2.matcher(commandStr);
			matcher.find();
			
			day = Integer.valueOf(matcher.group(2));
			month = monthList.indexOf(matcher.group(3).substring(0, 3).toLowerCase()) + 1;
			year = 2014;
			hour = Integer.valueOf(matcher.group(4));
			
			if (matcher.group(6) != null) {
				minute = Integer.valueOf(matcher.group(6));
			}
			else {
				minute = 0;
			}
			
			try {
				LocalDateTime.of(year, month, day, hour, minute);
				isValid = true;
			} catch (DateTimeException e) {
				isValid = false;
			}
			
			if (matcher.group(7) != null) {
				isValid = hour <= 12;
				if (matcher.group(7).equalsIgnoreCase("pm")) {
					hour += 12;
					if (hour >= 24) {
						hour = 0;
					}
				}
				else if (matcher.group(7).equalsIgnoreCase("am")) {
					if (hour == 12) {
						hour = 0;
					}
				}
			}
			
			break;
			
		case 3: 
			matcher = pattern3.matcher(commandStr);
			matcher.find();
			
			day = Integer.valueOf(matcher.group(2));
			month = Integer.valueOf(matcher.group(4));
			
			if (matcher.group(7) != null) {
				year = Integer.valueOf(matcher.group(6)+matcher.group(7));
			}
			else {
				year = Integer.valueOf(matcher.group(6)) + YEAR_OF_TODAY;
			}
			
			hour = Integer.valueOf(matcher.group(8));
			
			if (matcher.group(10) != null) {
				minute = Integer.valueOf(matcher.group(10));
			}
			else {
				minute = 0;
			}
			
			try {
				LocalDateTime.of(year, month, day, hour, minute);
				isValid = true;
			} catch (DateTimeException e) {
				isValid = false;
			}
			
			if (matcher.group(11) != null) {
				isValid = hour <= 12;
				if (matcher.group(11).equalsIgnoreCase("pm")) {
					hour += 12;
					if (hour >= 24) {
						hour = 0;
					}
				}
				else if (matcher.group(11).equalsIgnoreCase("am")) {
					if (hour == 12) {
						hour = 0;
					}
				}
			}
			
			break;
			
		default : break;
		}
		
		return isValid;
	}
	
	private int findReminderPattern(final String commandStr) {
		if (commandStr.matches(PATTERN_1)) {
			
			return R_PATTERN_1;
		}
		
		if (commandStr.matches(PATTERN_2)) {
			
			return R_PATTERN_2;
		}
		
		if (commandStr.matches(PATTERN_3)) {
			
			return R_PATTERN_3;
		}
		
		return NO_PATTERN;
	}
	
	private void report(final String toReport) {
		System.out.print(toReport);
	}
}
