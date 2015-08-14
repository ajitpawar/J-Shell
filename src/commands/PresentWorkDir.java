/**
 * This class provides the implementation of PresentWorkDir "pwd" command
 * 
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;

public class PresentWorkDir extends Command {

	public PresentWorkDir() {

		super("pwd");
	}

	/**
	 * main method to run pwd command
	 * 
	 * @param commandArgs
	 *            Array that contains user input
	 * @param shell
	 *            JShellobject that user is running presently
	 * @return Output String that user will get
	 */
	public String runCommand(String[] commandArgs, JShell shell) {

		Directory workDir = shell.getWorkDir();
		
		// Check if output needs to be redirected
		if(checkForRedirect(commandArgs)) {
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}		
		
		if (!isValidArgs(commandArgs)) {
			// give an error message to user but after that print pwd
			result += "pwd: ignoring non-option arguments" + "\n";
		}
		result += workDir.getPath();

		// Send output to file
		if(isRedirecting) {
			errors += processOutput(redirectArgs, shell, result);
			return errors;
		}
		
		// Send output to console
		else 	return errors + result;		
	}

	/**
	 * Check that exactly one argument has been passed by the user
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @return True only if number of arguments is exactly one
	 */
	protected boolean isValidArgs(String[] cmdArgs) {
		return (cmdArgs.length == 1);
	}

	/**
	 * Documentation for PresentWorkDir
	 * 
	 * @return a String of the documentation of PresentWorkDir
	 */
	public String[] getHelp() {

		String[] helpDocs = { cmdName, // Name
				"0", // Min Arguments
				"0", // Max Arguments
				"Print the entire path of the current working directory.", // Functionality
				"pwd" }; // Usage

		return helpDocs;
	}
}
