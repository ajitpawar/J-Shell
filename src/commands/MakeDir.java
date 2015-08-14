package src.commands;

import java.util.Arrays;

import src.filesys.*;
import src.shell.JShell;

/**
 * This class provides the implementation of Make Directory "mkdir" command
 * @version 1.0
 */
public class MakeDir extends Command {
	
	/**
	 * Default constructor
	 */
	public MakeDir() {
		super("mkdir");
	}
	
	/**
	 * Execute "mkdir" command.
	 * Create new directory 
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
		if (!isValidArgs(commandArgs))
			return invalidArgsMessage;
		
		// Make individual directories
		for(int i=1; i<commandArgs.length; i++) {

			String path = commandArgs[i];		// path of directory to create
			String[] pathArray = path.split("/");
			int size = pathArray.length;				
			String folderName = pathArray[size-1];		// name of directory to create
		
			try {
				
				// Check if directory already exists
				if (DirectoryNavigator.exists(path, cwd))
					throw new DirectoryException
					(String.format(fileExistsMessage, folderName));
				
				// Build path of parent directory
				String parentPath = buildParentPath(pathArray, cwd);
				
				// Retrieve directory object at parentPath
				ShellFile parentFile = DirectoryNavigator.getFile(parentPath, cwd, true);
				if (parentFile.getClass() != ShellDirectory.class) 
					throw new DirectoryException
					(String.format(notFoundMessage, folderName, "directory"));
				
				// Add new directory
				ShellDirectory parentDir = (ShellDirectory) parentFile;
				Directory newDir = new ShellDirectory(pathArray[size-1], parentDir);		
				parentDir.addDirectory(newDir);
			
			} catch (DirectoryException e) {
				errors += e.getMessage() + "\n"; 	// A directory already exists
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
	 * Build path for the parent directory from full path
	 * @param pathArray The array representing full path
	 * @param cwd Current working directory
	 * @return Path representing parent directory
	 */
	private String buildParentPath(String[] pathArray, Directory cwd) {		
		
		String path = "";
		
		if(pathArray.length < 2)
			path = cwd.getPath();		// path is a single directory name
		
		else {
			for(int i=0; i < pathArray.length - 1; i++) {
				path += pathArray[i] + "/";		// path is relative
			}
		}
		return path;
	}
	
	
	/**
	 * Check that atleast two arguments have been passed
	 * by the user
	 *  
	 * @param commandArgs Arguments passed by the user
	 * @return True only if number of arguments is atleast two
	 */
	protected boolean isValidArgs (String[] commandArgs) {		
		return (commandArgs.length >= 2);
	}
	
	
	/**
	 * Documentation for MakeDir
	 *
	 * @return a String[] of the documentation of MakeDir
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"1", // Min Arguments
							"Infinity", // Max Arguments
							"Create a directory at given path.", // Functionality
							"mkdir [name 1] [name 2] ... [name n]"}; // Usage
		
		return helpDocs;
	}
	
}
