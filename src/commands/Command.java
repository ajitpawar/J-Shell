package src.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays; 
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import src.shell.*;

public abstract class Command {

	protected String cmdName;
	
	
	// Error Messages
	public static String invalidArgsMessage = "Error: Invalid number of arguments";
	public static String illegalArgsMessage = "Error: Illegal characters in arguments";
	public static String notFoundMessage = "Error: %s - No such %s exists";
	public static String fileExistsMessage = "Error: A file with name %s already exists";
	
	// Command Styling
	protected static String helpSeparatorSmall = "\n------\n";
	protected static String helpSeparatorLarge = "---------------------\n";
	
	// Redirect output
	protected static String[] redirectArgs = {};
	public static String result = ""; 
	public static String errors = ""; 
	static boolean isRedirecting = false;
	
	// Constructor
	public Command(String name) {
		cmdName = name;
	}
	
	// Abstract methods
	public abstract String runCommand(String[] cmdArgs, JShell shell);
	protected abstract boolean isValidArgs(String[] cmdArgs);
	public abstract String[] getHelp();
	
	/**
	 * Remove the second argument in the command argument array
	 * E.g. {"ls", "-R", "path"} returns {"ls", "path"}
	 *      {"rm" "-f"} returns {"rm"}
	 *      {"ls"} returns {"ls"}
	 *
	 * @param argArray the argument array to remove the second element
	 * from
	 * @return a new array with all elements, in order with no gaps, without
	 * the second element of argArray
	 */
	protected static String[] removeOptionArg(String[] argArray) {
		
		// Case 0: < 2 args provided
		if (argArray.length < 2) {
			return argArray;
		}
		
		// Case 1: >= 2 args provided, proceed
		// with removal and reset
		int resultLength = argArray.length - 1;
		String[] resultArray = new String[resultLength];
		resultArray[0] = argArray[0];
		
		int i;
		for (i = 1; i < resultLength; i++) {
			resultArray[i] = argArray[i + 1];
		}
		
		return resultArray;

	}
	
	/**
	 * Redirect the output by echoing it to the output file
	 * 
	 * @param redirectArgs Arguments to pass to echo command
	 * @param shell JShell object for current session
	 * @param result The output to be redirected
	 * @return Errors, if any, while redirecting output
	 */
	protected String processOutput(String[] redirectArgs, JShell shell, String result) {
		
		String output = "";
		List<String> list = new ArrayList<String>();
		
		list.add("echo");
		list.add(result);
		for(String s: redirectArgs){
			list.add(s);
		}
		
		// Redirect output using "echo" command
		String[] newArgs = new String[ list.size() ];
		list.toArray(newArgs);	
		Command cmd = Executor.getCommand("echo");
		output = cmd.runCommand(newArgs, shell);
		
		// Return any errors while echoing
		return output;
	}
	
	
	/**
	 * Check if output for this command is to be redirected
	 * 
	 * @param cmdArgs The arguments passed by user
	 * @return True only if output is to be redirected
	 */
	protected static boolean checkForRedirect(String[] cmdArgs) {
		
		reset();
		int count = 0;
		Pattern p = Pattern.compile(">>|>");
		Matcher m = p.matcher(Arrays.toString(cmdArgs));
		
		while(m.find()) {
			count++;
		}
		
		// Exactly one redirect symbol should be present
		if (count != 1) 
			return false;			
		return true;
	}
	
	
	/**
	 * Initialize result and errors to default values
	 * before every execution of command
	 */
	private static void reset() {
		result = "";
		errors = "";
		isRedirecting = false;
	}
	
	
	/**
	 * Get an array of new arguments by removing the redirect symbol
	 * and its argument
	 * 
	 * @param cmdArgs The arguments passed by user
	 * @return String[] containing command arguments
	 */
	protected static String[] getArgs(String[] cmdArgs) {
		
		int index0 = Arrays.asList(cmdArgs).indexOf(">");
		int index1 = Arrays.asList(cmdArgs).indexOf(">>");
		
		int charIndex = Math.max(index0,index1);
		String[] newArgs = {};
		
		try {
			
			// Create a new array of arguments that doesn't include
			// redirect symbol and its argument
			newArgs = Arrays.copyOfRange(cmdArgs, 0, charIndex);
			redirectArgs = Arrays.copyOfRange(cmdArgs, charIndex, cmdArgs.length);
		} catch( ArrayIndexOutOfBoundsException e ) {}
		
		return newArgs;
	}
	
}
