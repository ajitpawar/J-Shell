package src.commands;

import src.filesys.*;
import src.shell.JShell;
import src.shell.Executor;

public class Man extends Command {
	
	private static final String name = "Man";
	private static final String NO_ENTRY = "No manual entry for %s\n";
	
	/**
	 * Default constructor
	 */
	public Man() {
		super(name);
	}
	
	/**
	 * Execute "man" command.
	 * Print usage manual for commands
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	public String runCommand(String[] commandArgs, JShell shell) {
		
		// Check for correct number of arguments
		if(!isValidArgs(commandArgs))
			return invalidArgsMessage;		
			
		String commandName;
		Command commandObject;
		int i;
		for (i = 1; i < commandArgs.length; i++) {
			
			// Fetch name of command
			commandName = commandArgs[i];
			
			// Fetch corresponding command object from
			// the static part of Executor, may be null
			commandObject = Executor.getCommand(commandName);
			
			// If command doesn't exist
			if (commandObject == null) {
				result += String.format(NO_ENTRY, commandName) + "\n";
			}
			
			// If command does exist
			else {
				String[] helpDocs = commandObject.getHelp();
				result += helpFormatter(helpDocs) + "\n";
			}
			
		}
		
		// Trim off last redundant line break
		return result.substring(0, (result.length() - 2));		

	}
	
	/**
	 * Check that two or more arguments have been passed
	 * by the user
	 *  
	 * @param commandArgs Arguments passed by the user
	 * @return True if and only if number of arguments is greater than 1
	 */
	protected boolean isValidArgs (String[] commandArgs) {		
		return (commandArgs.length >= 2);
	}
	
	/**
	 * Unified formatter for help documentation
	 * At index:
	 * 0 - name
	 * 1 - min. arugments
	 * 2 - max. arguments
	 * 3 - functionality
	 * 4 - usage
	 *
	 * @param helpContents the array of contents to be formatted to one String
	 * @return a String of the formatted contents within helpContents
	 */
	public static String helpFormatter(String[] helpContents) {
		
		String result = helpSeparatorLarge +

						"Name:\n" + helpContents[0] +
						helpSeparatorSmall +
						"Minimum Arguments:\n" + helpContents[1] + "\n" +
						"Maximum Arguments:\n" + helpContents[2] +
						helpSeparatorSmall +
						"Functionality:\n" + 
						helpContents[3] + 
						helpSeparatorSmall +
						"Usage:\n" + helpContents[4] + "\n" +

						helpSeparatorLarge;
		
		return result;
		
	}
	
	/**
	 * Documentation for Man
	 *
	 * @return a String[] of the documentation of MakeDir
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"1", // Min Arguments
							"Infinity", // Max Arguments
							"Print documentation for the specified command.", // Functionality
							"man [command 1] [command 2] ... [command n]"}; // Usage
		
		return helpDocs;
	}
	
}
