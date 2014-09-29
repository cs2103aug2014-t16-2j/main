import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
//import java.util.Scanner;

class Parser {

	//private Scanner sc = new Scanner(System.in);
	private final List<String> commandWords = Arrays.asList("add", "schedule", "delete", "remove", "modify", "edit", "clear", "reschedule", "change", "undo", "redo");
	private final List<String> dateTimeWords = Arrays.asList("on", "from", "to", "@", "at");
	private final List<String> monthWords = Arrays.asList("jan", "january", "feb", "febuary", "mar", "march", "apr", "april", "may", "jun", "june", "jul", "july", "aug", "august", "sep", "september", "oct", "october", "nov", "november", "dec", "december");
	private final List<String> ordinalNumWords = Arrays.asList("st", "nd", "rd" ,"th");
	
	/*public static void main(String[] args){
		Parser p = new Parser();
		String input=p.sc.nextLine();
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		Action a = new Action(p.getCommand(words), p.getTask(words));
		System.out.println(a.getCommand());
		for (LocalDateTime ldt : a.getTask().getDateTimeList()){
			System.out.println(ldt);
		}
		System.out.println(a.getTask().getContent());
	}*/
	
	public Action getAction(String input) {
		MyStringList words = new MyStringList();
		words.addAll(Arrays.asList(input.split(" ")));
		return new Action(getCommand(words), getTask(words));
	}
	
	private String getCommand(MyStringList words) {
		for (String c : commandWords) {
			if (words.containsIgnoreCase(c)) {
				words.removeIgnoreCase(c);
				return c;
			}
		}
		return "add";
	}
	private Task getTask(MyStringList words) {
		Task t = new Task();
		findDateTime(words, t);
		findContent(words, t);
		return t;
	}
	private void findDateTime(MyStringList words, Task t) {
		GregorianCalendar gc = new GregorianCalendar();
		for (int index = 0; index < words.size(); index++) {
			String w = words.get(index);
			LocalDate ld = null;
			LocalTime lt = null;
			switch (w.length()) {
			case 1 :
			case 2 :
				ld = findDateWithDay(words, index, gc);
				break;
			/*case 3 :
				findDateWithDayOrMonth(words, ld, index, gc);
				break;
			case 4 :
				try{
					int num=Integer.parseInt(words.get(index));
					if (num < 10000 && num >= 2014) {
						ldt = findDateIfYear(words,t,index,num);
					}
				} catch (Exception e) {
				}*/
			default :
				/*catch (Exception e) {
					ldt = findDateIfNotNumeric(words,t,index);
				}*/
			}
			if (ld != null) {
				t.setDate(ld);
				//removeUselessWord(words, index);
			}
			/*if (lt != null) {
				t.setTime(lt);
				removeUselessWord(words, index);
			}*/
		}
	}
	
	/*private void findDateWithDayOrMonth(MyStringList words, LocalDate ld, int index, GregorianCalendar gc) {
		for (String onw : ordinalNumWords) {
			if(words.get(index).endsWith(onw)){
				words.set(index, words.get(index).substring(0, words.get(index).lastIndexOf(onw)));
				findDateWithDay(words, ld, index, gc);
				return;
			}
		}
		findDateWithMonth(words, ld, index, gc);
	}*/
	
	private LocalDate findDateWithDay(MyStringList words, int index, GregorianCalendar gc){
		try {
			int num = Integer.parseInt(words.get(index));
			if (index + 1 < words.size() && num <= 31 && num > 0) {
				int day = 0;
				int mth = 0;
				int yr = 0;
				try {
					int num2 = Integer.parseInt(words.get(index + 1));
					if (num2 <= 12 && num2 > 0) {
						day = num;
						mth = num2;
						try {
							yr = Integer.parseInt(words.get(index + 2));
							if (yr >= 2014) {
								words.remove(index + 2);
							}
						} catch (Exception e) {
							yr = gc.get(GregorianCalendar.YEAR);
						}
						words.remove(index + 1);
						words.remove(index);
					} else if (num2 <= 31 && num2 > 0 && num <= 12) {
						day = num2;
						mth = num;
						try {
							yr = Integer.parseInt(words.get(index + 2));
							if (yr >= 2014) {
								words.remove(index + 2);
							}
						} catch (Exception e) {
							yr = gc.get(GregorianCalendar.YEAR);
						}
						words.remove(index + 1);
						words.remove(index);
					}
				} catch (Exception e) {
					for (String m : monthWords) {
						if (words.get(index + 1).equalsIgnoreCase(m)) {
							day = num;
							mth = getMth(m);
							try {
								yr = Integer.parseInt(words.get(index + 2));
								if (yr >= 2014) {
									words.remove(index + 2);
								}
							} catch (Exception e2) {
								yr = gc.get(GregorianCalendar.YEAR);
							}
							words.remove(index + 1);
							words.remove(index);
							break;
						}
					}
				}
				if (yr != 0) {
					return LocalDate.of(yr, mth, day);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/*private void findDateWithMonth(MyStringList words, LocalDate ld, int index, GregorianCalendar gc){
		for (String m : monthWords) {
			if (words.get(index).equalsIgnoreCase(m) && index + 1 < words.size()) {
				try {
					int num = Integer.parse
					day = num;
					mth = getMth(m);
					try {
						yr = Integer.parseInt(words.get(index+2));
						if (yr >= 2014) {
							words.remove(index+2);
						}
					} catch (Exception e2) {
						yr = gc.get(GregorianCalendar.YEAR);
					}
					words.remove(index);
					words.remove(index+1);
					break;
			}
		}
	}

	private void findDateIfYear(MyStringList words, Task t, int index, int num){
		int day=0;
		int mth=0;
		int yr=0;
		int hr=0;
		try{
			int num2=Integer.parseInt(words.get(index+2));
			yr=num;
			mth=num2;
			try{
				day=Integer.parseInt(words.get(index+3));
				words.remove(index+3);
			}
			catch(Exception e){
				for(String m:monthWords){
					if(words.get(index+3).equalsIgnoreCase(m)){
						day=mth;
						mth=getMth(m);
						words.remove(index+3);
						break;
					}
				}
			}
			words.remove(index);
			words.remove(index+1);
			words.remove(index+2);
		}
		catch(Exception e){
			boolean isNotMth=true;
			for(String m:monthWords){
				if(words.get(index+2).equalsIgnoreCase(m)){
					isNotMth=false;
					day=num;
					mth=getMth(m);
					try{
						yr=Integer.parseInt(words.get(index+3));
						words.remove(index+3);
					}
					catch(Exception e2){
					}
					words.remove(index);
					words.remove(index+1);
					words.remove(index+2);
					break;
				}
			}
			if(isNotMth){
				hr=num;
			}
		}
	}
	private void findDateIfNotNumeric(MyStringList words, Task t, int index){
		boolean isNotMth=true;
		for(String m:monthWords){
			if(words.get(index+1).equalsIgnoreCase(m)){
				isNotMth=false;
				try{
					t.setDay(Integer.parseInt(words.get(index+2)));
					t.setMth(getMth(m));
					try){
						t.setYr(Integer.parseInt(words.get(index+3)));
						words.remove(index+3);
					}
					catch(Exception e2){
					}
					words.remove(index);
					words.remove(index+1);
					words.remove(index+2);
				}
				catch(Exception e2){
				}
				break;
			}
		}
		if(isNotMth){
			if(words.get(index+1).contains("/")){
				String[] date=words.get(index+1).split("/");
				try{
					t.setDay(Integer.parseInt(date[0]));
					t.setMth(Integer.parseInt(date[1]));
					if(date.length==3){
						t.setYr(Integer.parseInt(date[2]));
					}
					words.remove(index);
					words.remove(index+1);
				}
				catch(Exception e2){
					t.setDay(0);
					t.setMth(0);
				}
			}
			else if(words.get(index+1).contains(":")){
				String[] time=words.get(index+1).split(":");
				try{
					t.setHr(Integer.parseInt(time[0]));
					t.setMin(Integer.parseInt(time[1]));
					if(time.length==3){
						t.setSec(Integer.parseInt(time[2]));
					}
					words.remove(index);
					words.remove(index+1);
				}
				catch(Exception e2){
					t.setHr(0);
					t.setMin(0);
				}
			}
			else if(words.get(index+1).endsWith("am")){
				String time=words.get(index+1).replace("","am");
				try{
					t.setHr(Integer.parseInt(time));
					words.remove(index);
					words.remove(index+1);
				}
				catch(Exception e2){
				}
			}
			else if(words.get(index+1).endsWith("pm")){
				String time=words.get(index+1).replace("","pm");
				try{
					t.setHr(Integer.parseInt(time)+12);
					words.remove(index);
					words.remove(index+1);
				}
				catch(Exception e2){
				}
			}
		}
	}*/
	
	private void findContent(MyStringList words, Task t){
		StringBuilder sb=new StringBuilder();
		for(String w:words){
			sb.append(w + " ");
		}
		t.setContent(sb.toString());
	}
	private int getMth(String m){
		switch(m){
		case "jan":
		case "january": return 1;
		case "feb":
		case "febuary": return 2;
		case "mar":
		case "march": return 3;
		case "apr":
		case "april": return 4;
		case "may": return 5;
		case "jun":
		case "june": return 6;
		case "jul":
		case "july": return 7;
		case "aug":
		case "august": return 8;
		case "sep":
		case "september": return 9;
		case "oct":
		case "october": return 10;
		case "nov":
		case "november": return 11;
		case "dec":
		case "december": return 12;
		default: return 0;
		}
	}
}

class MyStringList extends ArrayList<String>{
	public boolean containsIgnoreCase(String param){
		for(String s:this){
			if(param.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}
	public int indexOfIgnoreCase(String param){
		for(String s:this){
			if(param.equalsIgnoreCase(s)){
				return this.indexOf(s);
			}
		}
		return -1;
	}
	public void removeIgnoreCase(String param){
		for(String s:this){
			if(param.equalsIgnoreCase(s)){
				this.remove(s);
				break;
			}
		}
	}
}

class Action{
	String command;
	Task task;
	public Action(String cmd, Task t) {
		command = cmd;
		task = t;
	}
	public String getCommand() {
		return command;
	}
	public Task getTask() {
		return task;
	}
}
class Task{
	private List<LocalDateTime> dateTimeList;
	private String content;
	public Task(){
		dateTimeList = new ArrayList<LocalDateTime>();
		content="";
	}
	public void setContent(String c){
		content=c;
	}
	public String getContent(){
		return content;
	}
	public void setDate(LocalDate ld){
		boolean notSet = true;
		for (LocalDateTime ldt : dateTimeList){
			if (ldt.getYear() == 0) {
				ldt.withDayOfMonth(ld.getDayOfMonth());
				ldt.withMonth(ld.getMonth().getValue());
				ldt.withYear(ld.getYear());
				notSet = false;
				break;
			}
		}
		if (notSet) {
			dateTimeList.add(LocalDateTime.of(ld.getYear(), ld.getMonth().getValue(), ld.getDayOfMonth(), 0, 0, 0));
		}
	}
	public void setTime(LocalTime lt){
		boolean notSet = true;
		for (LocalDateTime ldt : dateTimeList){
			if (ldt.getHour() == 0) {
				ldt.withHour(lt.getHour());
				ldt.withMinute(lt.getMinute());
				ldt.withSecond(lt.getSecond());
				notSet = false;
				break;
			}
		}
		if (notSet) {
			dateTimeList.add(LocalDateTime.of(0, 1, 1, lt.getHour(), lt.getMinute(), lt.getSecond()));
		}
	}
	public List<LocalDateTime> getDateTimeList(){
		return dateTimeList;
	}
}