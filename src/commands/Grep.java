package src.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import src.filesys.*;
import src.shell.JShell;
import src.shell.Executor;

public class Grep extends Command {

	private static final String OPTION = "-R";
	private static String regex = "";
	
	/**
	 * Default constructor
	 */
	public Grep() {
		super("grep");
	}
	
	/**
	 * Execute "grep" command.
	 * Search (recursively) within files to find lines that match regex
	 * 
	 * @param cmdArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	public String runCommand(String[] cmdArgs, JShell shell) {
		
		Directory cwd = shell.getWorkDir();		
		boolean recurse = checkOptions(cmdArgs);  // true if "-R" supplied	
		
		/* Return new arguments with "-R" argument removed
		 * from list of commands
		 */		  
		if (recurse) 
			cmdArgs = removeOptionArg(cmdArgs);		
		
		
		/* Check if output needs to be redirected
		 * Return new arguments with redirect arguments removed
		 * from list of commands 
		 */
		if(checkForRedirect(cmdArgs)) {
			isRedirecting = true;
			cmdArgs = getArgs(cmdArgs);
		}

		// Check for correct number of arguments
		if (!isValidArgs(cmdArgs)) 
			errors = invalidArgsMessage;
		
		String path;
		ShellFile src;						// Starting directory point
		int size = cmdArgs.length;
		regex = cmdArgs[1];			// Regex to be matched
		
		// Traverse list of files or directories to grep
		for(int i=2; i<size; i++) {
			
			path = cmdArgs[i];
			try {				
				src = DirectoryNavigator.getFile(path, cwd, true);
				
				// Recursively traverse deeper into directory
				if(recurse && src.isDirectory())
					result += recurse((Directory) src);
				
				// Non-recursive. Ignore directories.
				else if(src.isDirectory())
					continue;
				
				// Grep contents of file
				else	result += matchRegex(src); 
					
			} catch (DirectoryException e) {
				errors += e.getMessage() + "\n";
			}			
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
	 * Recursively traverse directories and grep contained files
	 * 
	 * @param dir Directory to inspect for files to be grepped
	 * @return String containing all lines in all files matching regex
	 */
	private String recurse(Directory dir) {
		
		String output = "";  // contains final result of all matched lines
		
		// Recursively traverse deeper
		for (ShellFile inspect : ((ShellDirectory) dir)) {
			
			// For directories, traverse deeper
			if(inspect.getClass() == ShellDirectory.class)
				output += recurse((Directory) inspect);
			
			// Grep the file with regex
			else
				output += matchRegex(inspect);
		}
		
		return output;
	}
	
	
	/**
	 * Grep contents of file with user supplied regex
	 * 
	 * @param file File to be grepped
	 * @return String containing lines that matched regex
	 */
	private String matchRegex(ShellFile file) {
		
		String output = "";
		String[] input = file.toString().split("\\n");	// Split file line-by-line	
		Pattern p = Pattern.compile(regex);	
		
		// For each newline in the file
		for(int i=0; i<input.length; i++) {
			Matcher m = p.matcher(input[i]);
			if(m.find())
				// Add the line to result
				output += file.getPath() + ": " + input[i] + "\n";
		}
	
		// Final result containing all lines that matched regex
		return output;
	}
	

	/**
	 * Check if "-R" recursive option is supplied
	 * 
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if recursive option is supplied
	 */
	private boolean checkOptions(String[] cmdArgs) {
		return ((cmdArgs.length >= 3) &&
				cmdArgs[1].equals(OPTION));
	}
	
	
	/**
	 * Check that at least one argument has been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is at least one
	 */
	protected boolean isValidArgs(String[] cmdArgs) {
		return (cmdArgs.length >= 3);
	}
	
	
	/**
	 * Documentation for Remove
	 *
	 * @return a String[] of the documentation of Grep
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
				"1", // Min Arguments
				"Infinity", // Max Arguments
				"Recursively find files that contain regex", // Functionality
				"grep [-R] [name 2] ... [name n]"}; // Usage

		return helpDocs;
	}
	
}