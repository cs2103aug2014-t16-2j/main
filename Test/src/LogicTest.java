
public class LogicTest {
	public static String[] executeInputCommand(String userCommand){		
		String[] array={userCommand,"Successful"};
			if(userCommand.equals("exit")){
				System.exit(0);
			}
		return array;
	}
	public static String getData(){		
		return "The task is \n1. Eat\n2. Sleep";
	}
}