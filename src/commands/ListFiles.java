package src.commands;

import src.filesys.*;
import src.shell.JShell;

import java.util.Iterator;
import java.util.Arrays;

/**
 * This class provides the implementation of List Files "ls" command
 * with added recursive functionality
 * @version 2.0
 */
public class ListFiles extends Command {
	
	private static final String RECURSE_ARG = "-R";
	
	/**
	 * Default constructor
	 */
	public ListFiles() {
		super("ls");
	}

	/**
	 * Execute "ls" command.
	 * List contents of current working directory
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	public String runCommand(String[] commandArgs, JShell shell) {
		
		// Check if output needs to be redirected
		if (checkForRedirect(commandArgs)) {
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}
		
		// Case 0: Invalid arguments
		if (!isValidArgs(commandArgs)) {
			return invalidArgsMessage;
		}
		
		Directory cwd = shell.getWorkDir();
		boolean recurse = isRecurseArg(commandArgs);
		
		// Setup recurse array if necessary
		if (recurse) {
			commandArgs = removeOptionArg(commandArgs);
		}
		
		// Case 1: no path supplied
		if (commandArgs.length == 1) {
			if (recurse) {
				result = directoryRecurser(new Directory[]{cwd});
			} else {
				result = cwd.toString();
			}
			
			return result;
		}
		
		String path;
		boolean pathIsFile;
		boolean isDir;
		ShellFile target;
		int length = commandArgs.length;
		
		int i;
		for (i = 1; i < length; i++) {
			
			path = commandArgs[i];
			
			// Try to retrieve object at path
			try{
				
				target = DirectoryNavigator.getFile(path, cwd, true);
				isDir = target.isDirectory();
				
				if (recurse && isDir) {
					result += directoryRecurser(new Directory[]{(Directory) target});
				} else {
					result += target.toString();
				}
				
			} catch (DirectoryException e) {
				errors += e.getMessage() + "\n";
			}			
		}
		
		// Send output to file
		if (isRedirecting) {
			errors += processOutput(redirectArgs, shell, result);
			return errors;
		}
		
		// Send output to console
		else 	return errors + result;
	}
	
	private static boolean isRecurseArg(String[] commandArgs) {
		return ((commandArgs.length > 1) &&
				commandArgs[1].equals(RECURSE_ARG));
	}
	
	/**
	 * Create a formatted string representation of a directory's contents
	 *
	 * @param dir the directory to create a string representation of
	 * @return String the formatted representation of dir
	 */
	private String directoryFormatter(Directory dir) {
		
		String result;
		String dirPath = dir.getPath();
		String dirContents = dir.toString();
		
		result = dirPath + ":\n";
		if (dirContents.length() > 0) {
			result += dirContents + "\n";
		}
		
		return result;

	}
	
	private String directoryRecurser(Directory[] dirArray) {
		
		// Case 0: dirArray is empty
		if (dirArray.length == 0) {
			return "";
		}
		
		String result = new String();
		Directory dir;
		Directory[] subDirArray;
		String dirName;
		String dirContents;
		int length = dirArray.length;
		
		// Append the contents formatted contents of each
		// file to the result
		int i;
		for (i = 0; i < length; i++) {

			dir = dirArray[i];
			int dirLength = dir.length();
			result += directoryFormatter(dir);
			
			subDirArray = new Directory[dirLength];
			int index = 0;
			for (ShellFile file : dir) {
				if (file.isDirectory()) {
					subDirArray[index++] = (Directory) file;
				}
			}
			
			// Truncate the directory array to fit amount
			// of added directories perfectly
			subDirArray = Arrays.copyOf(subDirArray, index);
			
			result += directoryRecurser(subDirArray);

		}
		
		return result;

	}
		
	/**
	 * Check that more than zero arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is not zero
	 */
	protected boolean isValidArgs (String[] cmdArgs) { 
		return (cmdArgs.length >= 1);
	}
		
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"0", // Min Arguments
							"1", // Max Arguments
							"Print the contents of the given directory or file.", // Functionality
							"ls [directory/file]"}; // Usage
		
		return helpDocs;
	}
	
	public static void main(String[] args) throws Exception {
		String[] result = removeOptionArg(new String[]{"a","b","c","d"});
		System.out.println(Arrays.toString(result));
		
		ListFiles ls = new ListFiles();
		JShell js = new JShell();
		Directory workDir = new ShellDirectory();
		ShellFile file = new ShellFile("File 1");
		workDir.addFile(file);
		js.setWorkDir(workDir);
		String output;
		
		output = ls.runCommand(new String[]{"ls", "-R"}, js);
		System.out.println(output);
	}
	
}