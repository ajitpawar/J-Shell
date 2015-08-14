package src.shell;

import src.filesys.*;

import java.io.*;

public class InputOutput {

	private static BufferedReader userInput;
	
	static {
		userInput = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/**
	 * Print a formatted line to the JShell user,
	 * Read the user's input and return it
	 *
	 * @param workDir the working directory the JShell is currently in
	 * @return an unformatted String of the user's input
	 */
	public String userPrompt(Directory workDir) {
		
		String promtFormat = String.format("%s# ", workDir.getPath()); //Build user prompt based on the current working directory
		System.out.print(promtFormat);
		String rtn_value = "";
		try 
		{
			rtn_value = userInput.readLine(); //and read in user input
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return rtn_value;
		
	}
	
	/**
	 * Present the user with a message,
	 * prompt the user to type in a response
	 * return the raw response String
	 *
	 * @param message the message to prompt the user with
	 * @return String the input typed by the user after presenting
	 * the message
	 */
	public static String messageUser(String message) {
		
		System.out.print(message);
		
		// If there is an error, result is stays "No";
		String result = "No";	
		try {
			result = userInput.readLine(); //and read in user input
		} 
		catch (IOException e) {}
		
		if (result.length() == 0) {
			result = "Yes";
		}
		
		return result;
		
	}
	
	/**
	 * Promt the user with a message
	 * prompt the user to type in a response
	 * evaluate that response, and return it into a boolean
	 * Invalid responses will get reprompted
	 *
	 * Inputs "Yes", "yes", "y" etc. all return true
	 * Inputs "No", "no", "n" etc. all return false
	 *
	 * @param message the message to present to the user
	 * @return boolean whether the user typed yes are no
	 */
	public static boolean askUser(String message) {
		
		String tryAgain = "Please type 'yes' or 'no':\n";
		
		boolean answer;
		String result = messageUser(message).toLowerCase();

		while (true) {
			if (result.startsWith("y")) {
				answer = true;
				break;
			} else if (result.startsWith("n")) {
				answer = false;
				break;
			} else {
				result = messageUser(tryAgain + message);
			}
		}
		
		return answer;
	}

	/**
	 * Print any given message to the JShell user
	 */
	public static void print(String printMe) {

		if (printMe.length() > 0) {
			System.out.println(printMe);
		}
		
	}

}
