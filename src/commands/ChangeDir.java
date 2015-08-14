/**
 * This class provides the implementation of Change Directory "cd" command
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;

public class ChangeDir extends Command {
	
	/**
	 * Default constructor
	 */
	public ChangeDir() {
		super("cd");
	}
	
	/**
	 * Execute "cd" command.
	 * Change current working directory
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	public String runCommand(String[] commandArgs, JShell shell) {
		
		Directory cwd = shell.getWorkDir();
		
		// Check if output needs to be redirected
		if(checkForRedirect(commandArgs)) {
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}
		
		// Check for correct number of arguments
		if(!isValidArgs(commandArgs))
			return invalidArgsMessage;		
		
		// Begin searching for the provided path
		String path = commandArgs[1];
		
		try {
			
			// Retrieve directory object at path
			ShellFile targetDir = DirectoryNavigator.getFile(path, cwd, true);
			
			if (targetDir.getClass() == ShellDirectory.class) {				
				
				// Set fetched directory to be the current working directory
				shell.setWorkDir((ShellDirectory) targetDir);
			
			} else {
				result = String.format(notFoundMessage, path, "directory");
			}		
			
		} catch (DirectoryException e) {
			errors = String.format(notFoundMessage, path, "directory"); 	// No directory exists at path
		}
		
		// Send output to file
		if(isRedirecting) {
			errors += processOutput(redirectArgs, shell, result);
			return errors;
		}
		
		// Send output to console
		else 	return errors + result;
	}

	
	/**
	 * Check that exactly two arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is exactly two
	 */
	protected boolean isValidArgs (String[] cmdArgs) {		
		return (cmdArgs.length == 2);
	}
	
	
	/**
	 * Documentation for ChangeDir
	 *
	 * @return a String[] of the documentation of ChangeDir
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"1", // Min Arguments
							"1", // Max Arguments
							"Change directory to DIR, which may be relative to the current directory or may be a full path.", // Functionality
							"cd [filename]"}; // Usage
		
		return helpDocs;
	}
	
}
