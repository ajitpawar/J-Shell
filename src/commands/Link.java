package src.commands;

import src.filesys.DirectoryException;
import src.filesys.DirectoryNavigator;
import src.filesys.ShellDirectory;
import src.filesys.ShellFile;
import src.filesys.ShellShortcut;
import src.shell.JShell;

public class Link extends Command {
	/**
	 * Default constructor
	 */
	public Link() {
		super("ln");
	}
	
	/**
	 * Execute "ln" command.
	 * make a sybolic link for an exist fiile or directory
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	
	@Override
	public String runCommand(String[] cmdArgs, JShell shell) {
		
		if(checkForRedirect(cmdArgs)) {
			   isRedirecting = true;
			   cmdArgs = getArgs(cmdArgs);
			}
		
		if (isValidArgs(cmdArgs)){
		String targetPath = getTargetPath(cmdArgs);// get_target
		if(isTargetValid(targetPath, (ShellDirectory)shell.getWorkDir())){
			
			ShellDirectory presentWorkDir = (ShellDirectory) shell.getWorkDir();
			String linkName = getLinkName(cmdArgs, presentWorkDir);// get_linkName
			try{
			ShellDirectory dirToStore = getDirToStoreLink(cmdArgs, presentWorkDir);
			ShellShortcut link = getShortCut(linkName, targetPath, shell);
			storeLink(dirToStore, link);
			}catch(DirectoryException e){
				errors += e.getMessage();
				}
			}
		else{
			errors += String.format(notFoundMessage, targetPath, "file or directory");
		}
		
		
		if(isRedirecting) {
			   errors += processOutput(redirectArgs, shell, result);
			   return errors;
			}
			// Send output to console
			else return errors + result;
		}//get_dir_to_store_link
		else{
			return invalidArgsMessage;
			}
		}
	/**
	 * Check that exactly three arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is exactly two
	 */
	@Override
	protected boolean isValidArgs(String[] cmdArgs) {
		return cmdArgs.length == 3;
	}
	
	/**
	 * get the target's path from the user input
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return target's path(String)
	 */
	protected String getTargetPath(String [] cmdArgs) {
		return cmdArgs[2];
	}
	/**
	 * check whether the target's path is valid or not
	 *  
	 * @param targetpath and present work directory 
	 * @return true if and only if the target's path is valid
	 */
	protected boolean isTargetValid(String targetPath, ShellDirectory pwd) {
		return DirectoryNavigator.exists(targetPath, pwd);
	}
	
	/**
	 * from the Link's path get the name of the link to make a ShellShortCut object 
	 *  
	 * @param cmdArgs that contains user inputs, present working directory  
	 * @return linkName from the link path
	 */
	protected String getLinkName(String[] cmdArgs, ShellDirectory presentWorkDir) {
		String pathContainsFileName;
		if(doesInputContainLinkName(cmdArgs, presentWorkDir)){
			pathContainsFileName = getLinkPath(cmdArgs);
			} 
		else{
			pathContainsFileName = getTargetPath(cmdArgs);
			}
		String [] LinkArray = pathContainsFileName.split("/");
		String LinkName = LinkArray[LinkArray.length -1];
		return LinkName;
	}
	/**
	 * check the link's path from the user input contains the name of the link will be created 
	 * or just stating the directory to store the link
	 *  
	 * @param cmdArgs that contains user input and present work directory 
	 * @return true if and only if use input link's path contains the name of the link
	 */
	protected Boolean doesInputContainLinkName(String[] cmdArgs, ShellDirectory presentWorkDir) {
		String linkPath = getLinkPath(cmdArgs);
		if (DirectoryNavigator.exists(linkPath, presentWorkDir)){
			return false;
		}
		else return true;
	}
	/**
	 * get link path from the user input
	 *  
	 * @param cmdArgs that contains user input 
	 * @return link path(String)
	 */
	protected String getLinkPath(String[] cmdArgs){
		return cmdArgs[cmdArgs.length -2];
	}
	
	/**
	 * get the directory to store the link(ShellShortCut object)
	 * throws DirectoryException if it can't find the directory to store to link
	 * @param cmdArgs that contains user inputs and present work directory
	 * @return ShellDirectory where the ShellShortCut object will be stored.
	 */
	
	protected ShellDirectory getDirToStoreLink(String[] cmdArgs, ShellDirectory presentWorkDir) throws DirectoryException{
		String linkPath = getLinkPath(cmdArgs);
		String dirPathToStoreLink = new String();
		if(doesInputContainLinkName(cmdArgs, presentWorkDir)){
			if(linkPath.indexOf("/") != -1){
				dirPathToStoreLink = linkPath.substring(0,linkPath.lastIndexOf('/'));
			}
			else{
				dirPathToStoreLink = ".";
			}
		
			}
		else{
				dirPathToStoreLink = linkPath;
			}
		ShellFile DirToStore = new ShellDirectory();
		try{
			DirToStore = DirectoryNavigator.getFile(dirPathToStoreLink, presentWorkDir);
		}catch(DirectoryException e){
			throw new DirectoryException(String.format(notFoundMessage, linkPath, "file or directory"));
			}
		//if user gave an file instead of directory, (we can't store a link in it) throw the error.
		if (DirToStore.getClass() == ShellFile.class || DirToStore.getClass() == ShellShortcut.class){
				throw new DirectoryException(String.format(fileExistsMessage, linkPath));
				}
		return (ShellDirectory)DirToStore;
			
		}
	/**
	 * make a ShellShortcut object(Link) and return it
	 *  
	 * @param String linkname, String targetpath and JShell 
	 * @return ShellShortCut object linked to the target
	 */
	protected ShellShortcut getShortCut(String linkName, String targetPath, JShell shell) {
		return new ShellShortcut(linkName, targetPath, shell);
	}
	
	protected void storeLink(ShellDirectory dirToSave, ShellShortcut link) throws DirectoryException {
		try{
			dirToSave.addFile(link);
			}
		catch(DirectoryException e){
			throw new DirectoryException(String.format(fileExistsMessage, link.getName()));
			}
		}

	/**
	 * Documentation for ChangeDir
	 *
	 * @return a String[] of the documentation of ChangeDir
	 */
	@Override
	public String[] getHelp() {
		

		String[] helpDocs = { cmdName, // Name
				"2", // Min Arguments
				"2", // Max Arguments
				"make a sybolic link for an exist fiile or directory", // Functionality
				"ln" }; // Usage

		return helpDocs;
	}
}
