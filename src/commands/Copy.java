/**
 * This class provides the implementation of Copy "cp" command
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;

public class Copy extends Command {
	
	private String copyToFileMessage = "Error: Copy or move destination must be directory";
	private String copyRootMessage = "Error: Cannot copy or move root directory";
	private String copyToSelfMessage = "Error: Cannot copy or move file or folder to self";
	
	/**
	 * Default constructor
	 */
	public Copy()
	{
		super("cp");
	}
	
	/**
	 * Copy file/folder to specified directory
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	The JSell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise.  
	 */	
	public String runCommand(String[] commandArgs, JShell shell) {
		
		Directory root = shell.getWorkDir().getRoot();
		String destPath = null;
		String sourcePath = null;
		
		// Check if output needs to be redirected
		if(checkForRedirect(commandArgs)) {
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}
		
		//Ensure there are exactly 3 arguments
		if(!isValidArgs(commandArgs))
		{
			errors = invalidArgsMessage;
			return finish(shell);
		}
		
		//Get absolute paths and shortcut paths
		try {
			sourcePath = getPath(commandArgs[1], root);
			destPath = getPath(commandArgs[2], root);
		} catch (DirectoryException e) {
			errors = e.getMessage();
			return finish(shell);
		}
			
		//Ensure source and destination of copy are valid
		errors = arePathsValid(sourcePath,destPath,shell);

		//If paths are invalid, return error message(s)
		if(!errors.equals("")){
			return finish(shell);
		}
		
		//Paths and arguments valid: proceed with copy
	
		try {
			 //If object being copied is a file:
			if (!DirectoryNavigator.getFile(sourcePath, root).isDirectory())
			{
				//Process a file
				processFile(sourcePath, destPath, shell);
			}
			else //Otherwise, process a directory.
			{
				processDirectory(sourcePath, destPath, shell);
			}
		} catch (DirectoryException e) {
			errors = e.getMessage();
		}
		
		return finish(shell);
	}
	
	/**
	 * Check that exactly three arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is exactly three
	 */
	public boolean isValidArgs (String[] cmdArgs) {		
		return (cmdArgs.length == 3);
	}
	
	/**
	 * Ensure that item being copied and location of copy
	 * are valid
	 *  
	 * @param sourcePath Absolute path of object being copied
	 * @param destPath Absolute path of copy location
	 * @shell JShell The JSell object for current session
	 * @return Blank string if source and path are valid
	 */	
	public String arePathsValid(String sourcePath,String destPath, JShell shell)
	{

		//Ensure root not being copied
		if(sourcePath.equals("/"))
		{
			return copyRootMessage;
		}
		
		//Ensure file/folder not being copied to self
	    if(sourcePath==destPath)
	    {
	            return copyToSelfMessage;
	    }
		
		// Ensure source file/folder exists
		if(!checkExistence(sourcePath,shell))
		{
			return String.format(notFoundMessage, sourcePath, "file or directory");
		}
		
		//  Ensure existence of destination path
		if(!checkExistence(destPath,shell))
		{
			return String.format(notFoundMessage, destPath,"file or directory");
		}
		
		// If it exists, check whether final destination is a file
		try {
			if (!DirectoryNavigator.getFile(destPath, shell.getWorkDir().getRoot()).isDirectory())
			{
				return copyToFileMessage;
			}
		} catch (DirectoryException e) {
			return e.getMessage();
		}
		
		// Check whether file/folder by same name exists at location
		if(pathExists(sourcePath,destPath,shell)){
			String target = sourcePath.split("/")[sourcePath.split("/").length-1];
			return String.format(fileExistsMessage, target);
		}
		
		//No errors found
		return ""; 
	
	}
	
	/**
	 * Ensure that a file/folder of the same
	 * name as the one being copied doesn't already
	 * exist at destination
	 * @param sourcePath Absolute path of object being copied
	 * @param destPath Absolute path of copy location
	 * @shell JShell The JSell object for current session
	 * @return True only if potential path is valid copy location
	 */	
	public boolean pathExists(String sourcePath, String destPath, JShell shell){
		
		// Ensure source directory/file doesn't already exists at destination
		String target = sourcePath.split("/")[sourcePath.split("/").length-1];//Get file/folder to copy
		String potential_path = destPath + target;
				
		return checkExistence(potential_path, shell);
	}
	/**
	 * Check existence of file/folder at end of path
	 *  
	 * @param path Absolute path of object being checked
	 * @shell JShell The JSell object for current session
	 * @return True only if object exists
	 */
	public boolean checkExistence(String path, JShell shell)
	{		
		//Returns true if path exists
		Directory workDir = shell.getWorkDir();
		return DirectoryNavigator.exists(path, ((ShellDirectory)workDir.getRoot()));
	}
	
	/**
	 * Create new copy of file and add
	 * to destination directory
	 *  
	 * @param sourcePath Absolute path of file being copied
	 * @param destPath Absolute path of location of copied file
	 * @throws DirectoryException 
	 * @shell JShell The JSell object for current session
	 */
	public void processFile(String sourcePath, String destPath, JShell shell) throws DirectoryException
	{
		//Get file to be copied
		ShellFile origFile = DirectoryNavigator.getFile(sourcePath, shell.getWorkDir());
		
		//Create a new object that is a clone of the original file
		ShellFile cloneFile = cloneFile(origFile);
		
		//Get destination folder
		Directory destDir = (Directory)DirectoryNavigator.getFile(destPath, shell.getWorkDir());
		
		//Add cloned file into destination directory
		addFileToDir(destDir, cloneFile);
		
	}

	/**
	 * Create new copy of directory, including,
	 * contents, and add it to destination folder
	 *  
	 * @param sourcePath Absolute path of directory being copied
	 * @param destPath Absolute path of location of copied file
	 * @shell JShell The JSell object for current session
	 * @throws ShellDirectoryException
	 */
	public void processDirectory(String sourcePath, String destPath, JShell shell) throws DirectoryException
	{
		//Get directory to be copied
		Directory origDir = (Directory) DirectoryNavigator.getFile(sourcePath, shell.getWorkDir());

		//Create clone of the original directory
		Directory cloneDir = cloneDirectory((ShellDirectory)origDir);
		
		//Get destination folder
		Directory destDir = (Directory) DirectoryNavigator.getFile(destPath, shell.getWorkDir());
		
		//Add cloned folder to destination
		addDirToDir(destDir, cloneDir);
		
	}
	
	/**
	 * Create new ShellFile object that is
	 * a clone of the provided file
	 *  
	 * @param ShellFile original ShellFile object
	 * to be cloned
	 * @return new ShellFile object that is clone of
	 * original
	 */
	public ShellFile cloneFile(ShellFile original)
	{
		ShellFile clone;
		
		if(original.isShortCut()){
			ShellShortcut originalShortcut = (ShellShortcut)original;
			clone = new ShellShortcut(originalShortcut.getName(), originalShortcut.getTargetPath(), originalShortcut.getShell());
			}
		else{
			clone = new ShellFile();
			clone.setName(original.getName());
			clone.setContents(original.toString());
		}

		return clone;
	}
	
	/**
	 * Create new ShellDirectory object that is
	 * a clone of the provided object
	 *  
	 * @param ShellDirectory original ShellDirectory
	 *  object to be cloned
	 * @throws ShellDirectoryException
	 * @return new ShellDirectory object that is 
	 * clone of original
	 */
	public ShellDirectory cloneDirectory (ShellDirectory original) throws DirectoryException
	{
		ShellDirectory clone = new ShellDirectory();
		clone.setName(original.getName());
		
		String spacing = ShellDirectory.spacing;
		String [] copy_list = original.toString().trim().split(spacing);

        if(!copy_list[0].equals("")){  //If the folder contains items to copy

            for (String item :copy_list) // Copy every item in the list of folder contents
            {
                if(!original.getFile(item).isDirectory()) //Current item to copy is a file
                {
                    ShellFile origFile = original.getFile(item); //Get file to copy
                    ShellFile cloneFile = cloneFile(origFile); // Create new file with same content and name
                    clone.addFile(cloneFile); //Add file to clone directory - Parent set automatically in ShellDirectory class
                }
                else // Current item to copy is a directory
                {
                    ShellDirectory subDir = (ShellDirectory)original.getFile(item); // Get directory to be copied
                    ShellDirectory subClone = cloneDirectory(subDir); //Clone the directory
                    clone.addDirectory(subClone); //Add cloned sub directory to its parent
                }                         
            }    
        }        	
		return clone; //Return cloned directory
	}
	
	/**
	 * Insert ShellFile into ShellDirectory
	 * 
	 * @param ShellFile file ShellFile object
	 * to be inserted
	 * @param Directory dir Directory object
	 * where file is added
	 * @throws DirectoryException 
	 */
	private void addFileToDir(Directory dir, ShellFile file) throws DirectoryException {
		dir.addFile(file);	
	}
	
	/**
	 * Insert Directory into Directory
	 * 
	 * @param Directory dir Directory
	 * object to be inserted
	 * @param Directory destination
	 * Directory object where dir added
	 * @throws DirectoryException 
	 */	
	private void addDirToDir(Directory destination, Directory dir) throws DirectoryException {
			destination.addDirectory(dir);	
	}
	
	private String getPath(String path, Directory wDir) throws DirectoryException{
		
			String absPath = DirectoryNavigator.getAbsolutePath(path, wDir);
			
			return absPath;
	}
	
	/**
	 * Process output of command
	 * If redirecting, redirect results
	 * to file, else print results to console.
	 * Print error messages to console.
	 * @param shell
	 * @return String of result + errors
	 */
	protected String finish(JShell shell) {
		// Send output to file
		if(isRedirecting) {
			errors += processOutput(redirectArgs, shell, result);
			return errors;
		}
		
		// Send output to console
		else
			return errors + result;
	}
	
	/**
	 * Documentation for Copy
	 *
	 * @return a String[] of the documentation of Copy
	 */
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"2", // Min Arguments
							"2", // Max Arguments
							"Copy item OLDPATH to NEWPATH. " +
							"Both OLDPATH and NEWPATH may be relative to the current directory or may be full paths. " +
							"If NEWPATH is a directory, copy the item into the directory.", // Functionality
							"cp [oldpath] [newpath]"}; // Usage
		
		return helpDocs;
	}
}