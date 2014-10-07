import java.io.BufferedReader;
import java.io.FileReader;

public class LogicTest {
	public static String[] executeInputCommand(String userCommand){		
		String[] array={userCommand+" Successful"};

		if(userCommand.equals("exit")){
			System.exit(0);
		}
		return array;
	}
	public static String getData(){		
		return "The task is \n1. Eat\n2. Sleep";
	}
	public static boolean haveTaskOrNot(String date){
		try {
			BufferedReader in = new BufferedReader(new FileReader(date+".txt"));
			String tempNote="";
			if ((tempNote=in.readLine()) != null){
				in.close();//close file				
				return true;				
			}
			else{
				in.close();//close file
				return false;
			}

		} catch (Exception event) {
			return false;
		}		
	}
}