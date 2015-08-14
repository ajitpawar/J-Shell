/**
 * This class provides the implementation of Move "mv" command
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;

public class Move extends Copy {
	
	protected static String cmdName;
	
	/**
	 * Default constructor
	 */
	public Move()
	{
		cmdName = "mv";
	}
	
	/**
	 * Move file to specified directory
	 *  
	 * @param sourcePath Absolute path of file being moved
	 * @param destPath Absolute path of location of move
	 * @throws DirectoryException 
	 * @shell JShell The JSell object for current session
	 */	
	public void processFile(String sourcePath, String destPath, JShell shell) throws DirectoryException
	{
		super.processFile(sourcePath, destPath, shell);
		delete(sourcePath,shell);
	}

	/**
	 * Move folder to specified directory
	 *  
	 * @param sourcePath Absolute path of folder being moved
	 * @param destPath Absolute path of location of move
	 * @throws DirectoryException 
	 * @shell JShell The JSell object for current session
	 */
	public void processDirectory(String sourcePath, String destPath, JShell shell) throws DirectoryException
	{
		super.processDirectory(sourcePath, destPath, shell);
		delete(sourcePath,shell);
	}
	
	/**
	 * Delete specified file or folder
	 *  
	 * @param sourcePath Absolute path of file being deleted
	 * @throws DirectoryException 
	 * @shell JShell The JSell object for current session
	 */	
	public void delete(String sourcePath, JShell shell) throws DirectoryException
	{
		//Get file to delete
		ShellFile deleteMe = DirectoryNavigator.getFile(sourcePath, shell.getWorkDir());
		
		//Remove potential shortcut links
		deleteMe.nullifyShortcuts();
		
		//Proceed to delete file
		deleteMe.getParent().removeFile(deleteMe.getName());
	}

	/**
	 * Documentation for Move
	 *
	 * @return a String of the documentation of Move
	 */
	public String[] getHelp() {
		
		String[] helpDocs = {cmdName, // Name
			"2", // Min Arguments
			"2", // Max Arguments
			"Move item OLDPATH to NEWPATH. " +
			"Both OLDPATH and NEWPATH may be relative to the current directory or may be full paths. " +
			"If NEWPATH is a directory, move the item into the directory.", // Functionality
			 "mv [oldpath] [newpath]"}; // Usage
		
		return helpDocs;
	}


}
