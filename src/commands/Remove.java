package src.commands;

import src.shell.JShell;
import src.shell.InputOutput;
import src.filesys.*;

/**
 * This class provides the implementation of Remove "remove" command
 * @version 1.0
 */
public class Remove extends Command {
	
	private static final String FLAG = "-f";
	private static final String ASK_USER = "Would you like to delete %s? >>> ";
	private static final String REMOVE_ERROR = "Error: '.' and '..' may not be removed\n";
	
	public Remove() {
		super("rm");
	}

	/**
	 * Execute "remove" command.
	 * Remove a directory or a file
	 * @param cmdArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	public String runCommand(String[] cmdArgs, JShell shell) {
		
		// Setup
		String errors = new String();
		Directory cwd = shell.getWorkDir();
		boolean flag = isFlag(cmdArgs);
		boolean redirect = checkForRedirect(cmdArgs);
		
		// Flag setup
		if (flag) {
			cmdArgs = removeOptionArg(cmdArgs);
		}

		// Redirection setup
		if (redirect) {
			cmdArgs = getArgs(cmdArgs);
		}
		
		// Case 0: Invalid Arguments
		if (!isValidArgs(cmdArgs)) {
			return invalidArgsMessage;
		}
		
		// Check if output needs to be redirected
		if (checkForRedirect(cmdArgs)) {
			isRedirecting = true;
			cmdArgs = getArgs(cmdArgs);
		}
		
		String path;
		boolean found;
		boolean removed;
		ShellFile target;
		Directory targetParent;
		int length = cmdArgs.length;
		
		int i;
		for (i = 1; i < length; i++) {
			
			path = cmdArgs[i];
			found = true;
			target = null;
			targetParent = null;

			// Case 0: Cannot remove '.' or '..'
			// Aborts current iteration only
			if (!isValidRemovePath(path)) {
				errors += REMOVE_ERROR;
				continue;
			}
			
			// Try to retrieve object at path
			try{
				target = DirectoryNavigator.getFile(path, cwd);
				targetParent = target.getParent();
			} catch (DirectoryException e) {
				errors += e.getMessage() + "\n";
				found = false;
			}

			// Proceed only of the target isn't the root directory
			if (found &&
				(targetParent != null)) {

				// Remove recursively
				if (!flag) {
					removed = removeAskRecurse(target);	
				}
				
				// Remove manually
				else {
					removeItem(target);
					removed = true;
				}

				// If the folder removed is the working directory
				// the working directory is set to its parent
				if (removed && (target == cwd)) {
					shell.setWorkDir(targetParent);
				}

			}
			
		}

		// Optionally redirect output
		if (redirect) {
			errors += processOutput(redirectArgs, shell, result);
			result = errors;
		} else {
			result = errors + result;
		}

		return result;
	}
	
	/**
	 * Check that at least one argument has been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is at least one
	 */
	protected boolean isValidArgs(String[] cmdArgs) {
		return (cmdArgs.length > 1);
	}
	
	/**
	 * Check whether the second argument passed is a flag
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True if the second argument is the flag
	 */
	private boolean isFlag(String[] cmdArgs) {
		return ((cmdArgs.length > 1) &&
				cmdArgs[1].equals(FLAG));
	}

	private boolean isValidRemovePath(String path) {
		return (!path.equals(".") && 
				!path.equals("./") &&
				!path.equals("..") &&
				!path.equals("../"));
	}

	/**
	 * Remove the ShellFile from its parent directory
	 * If the ShellFile has a shortcut, nullify the shortcut
	 *  
	 * @param target The directory to remove
	 * @return void
	 */
	private void removeItem(ShellFile target) {
		target.nullifyShortcuts();
		target.remove();
	}
	
	/**
	 * Recursively ask to remove subdirectories of given directory
	 * Starts recursion from the directory fartherst away from the root
	 *  
	 * @param target The directory to remove along with
	 * its subdirectories and files
	 * @return boolean whether the user has decided to remove the target directory
	 */
	private boolean removeAskRecurse(ShellFile target) {
		
		boolean recurseAnswer = true;
		boolean answer = false;
		
		// Case 0: Target is a directory, recurse first
		if (target.getClass() == ShellDirectory.class) {
			
			for (ShellFile inspect : ((ShellDirectory) target)) {
				if (!removeAskRecurse(inspect)) {
					recurseAnswer = false;
				}
			}
			
			if (!recurseAnswer) {
				return false;
			}
			
			answer = InputOutput.askUser(String.format(ASK_USER, target.getName()));
			
			if (answer) {
				removeItem(target);
				return true;
			} else {
				return false;
			}

		}
		
		answer = InputOutput.askUser(String.format(ASK_USER, target.getName()));
		
		// Case 1: The answer is yes and the target is a file
		if (answer) {
			removeItem(target);
		}
		
		return answer;
		
	}
	
	/**
	 * Documentation for Remove
	 *
	 * @return a String[] of the documentation of Remove
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"1", // Min Arguments
							"Infinity", // Max Arguments
							"Delete given path after confirming with the user.\n" +
							"If '-f' is supplied, do not confirm: just remove.", // Functionality
							"rm [-f] [path]"}; // Usage
		
		return helpDocs;
	}
	
}