/**
 * This class provides the implementation of Cat "cat" command
 * 
 * @version 1.0
 */
package src.commands;
import src.shell.JShell;
import src.filesys.*;


public class Cat extends Command {

	String errormsg;
	
	/**
	 * Default constructor
	 */
	public Cat() {
		super("cat");
	}

	/**
	 * main method to run Cat command
	 * 
	 * @param commandArgs
	 *            Array that contains user input
	 * @param shell
	 *            JShellobject that user is running presently
	 * @return Output String that user will get
	 */
	public String runCommand(String[] commandArgs, JShell shell) {
		
		if(checkForRedirect(commandArgs)) {
			   isRedirecting = true;
			   commandArgs = getArgs(commandArgs);
			}
		
		if (isValidArgs(commandArgs)){
			String [] fileList = getFileList(commandArgs);
			//get file list
			for (int i= 0 ; i< fileList.length - 1; i ++){
				String file = fileList[i];
				 concatenatesContents(file, shell);
			}
			//to eliminate only trailing spaces
			concatenatesLastContents(fileList[fileList.length -1 ], shell);
			
			if(isRedirecting) {
				   errors += processOutput(redirectArgs, shell, result);
				   return errors;
				}
				// Send output to console
			else return errors + result;
			
		}
		else{
			return invalidArgsMessage;
		}

	}
	
	protected String [] getFileList(String [] cmdArgs) {
		String [] fileList = new String [cmdArgs.length - 1];
		for (int i = 1; i < cmdArgs.length; i++) {
			fileList[i - 1] = cmdArgs[i];
		}
		return fileList;
	}
	
	protected void concatenatesContents(String file, JShell shell) {
		try {
			String fileContents = DirectoryNavigator.getFile(file, shell.getWorkDir(), true).toString();
			result += fileContents + "\n";
		} catch (DirectoryException e) {
			errors += String.format(notFoundMessage, file, "file or directory") + "\n";
		}
	}
	
	
	protected void concatenatesLastContents(String file, JShell shell) {
		try {
			String fileContents = DirectoryNavigator.getFile(file, shell.getWorkDir(), true).toString();
			result += fileContents;
		} catch (DirectoryException e) {
			errors += String.format(notFoundMessage, file, "file or directory");
		}
	}

	/**
	 * Check that at least two arguments have been passed by the user
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @return True only if number of arguments is at least two
	 */
	protected boolean isValidArgs(String[] cmdArgs) {
		return (cmdArgs.length >= 2);
	}

	/**
	 * Check that user input paths are correct
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @param pwd
	 *            Directory object that is present working directory
	 * @return True only if all paths are correct
	 */
	public boolean isCorrectPath(String[] cmdArgs, Directory pwd) {
		for (int i = 1; i < cmdArgs.length; i++) {
			if (!DirectoryNavigator.exists(cmdArgs[i], pwd)) {
				errormsg = String.format(notFoundMessage, cmdArgs[i], "file or directory");
				return false;
			}
		}
		return true;
	}

	/**
	 * Documentation for PresentWorkDir
	 * 
	 * @return a String of the documentation of Cat
	 */

	public String[] getHelp() {

		String[] helpDocs = { cmdName, // Name
				"1", // Min Arguments
				"infinity", // Max Arguments
				"Display the contents of FILE in the shell.", // Functionality
				"cat [filename 1] [filename 2] ... [filename n]" }; // Usage

		return helpDocs;
	}
}
