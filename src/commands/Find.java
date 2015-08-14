package src.commands;
import java.util.regex.*;
import src.filesys.*;
import src.shell.JShell;

public class Find extends Command {

	/**
	 * Default constructor
	 */
	public Find()
	{
		super("find");
	}
	
	public String runCommand(String[] commandArgs, JShell shell) {
		
		// Check if output needs to be redirected
		if(checkForRedirect(commandArgs))
		{
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}
		
		//Ensure user input is valid
		errors = checkCmdArgs(commandArgs,shell);
		if(!errors.equals(""))
		{
			return finish(shell);
		}
		
		//Get absolute path of folder being searched
		String absPath="";
		try
		{
			absPath = DirectoryNavigator.getAbsolutePath(commandArgs[2], shell.getWorkDir().getRoot());
		} 
		
		//If path is invalid, catch and return error message
		catch (DirectoryException e) {
			errors = e.getMessage();
			return finish(shell);
		}
		
		// commandArgs OK, proceed with find execution
		try 
		{
			// If search path ends with file
			if (!DirectoryNavigator.getFile(absPath, shell.getWorkDir(), true).isDirectory()){
				//Get file to be match against regex and process it
				ShellFile current=null;
				current = DirectoryNavigator.getFile(absPath, shell.getWorkDir(), true);				
				processFile(commandArgs,absPath,current);
			}
			
			// Otherwise, search path ends with a folder
			else{
				//Get folder to be searched	and process it
				ShellDirectory current=null;
				current = (ShellDirectory) DirectoryNavigator.getFile(absPath, shell.getWorkDir(), true);
				processFolder(commandArgs,absPath,current,shell);
			}
		}
		
		// Catch and return error caused by calling getFile
		catch (DirectoryException e)
		{
			errors = e.getMessage();
			return finish(shell);
		}
		
		// Successfully executed find
		return finish(shell);
	}
	
	private String checkCmdArgs(String[] cmdArgs, JShell shell){
		
		//Ensure there are exactly 3 command arguments
		if(!isValidArgs(cmdArgs)){
			return invalidArgsMessage;
		}
		
		//Ensure regex pattern in valid
		String regCheck = isValidRegex(cmdArgs[1]);
		if(!regCheck.isEmpty()){
			// Return error message stored in regCheck
			return regCheck;
		}
		
		// cmdArgs are valid
		return "";
	}

	/**
	 * Check that exactly three arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is exactly three
	 */
	public boolean isValidArgs(String[] cmdArgs) {
		
		return (cmdArgs.length==3);
	}
	
	public String isValidRegex(String regex){
		
		//Attempt to compile regex pattern
		try
		{
			Pattern.compile(regex);	
		}
		
		//Catch and return invalid regex error
		catch (PatternSyntaxException e)
		{
			return e.getMessage();
		}
		
		//Regex compiled successfully
		return "";
	}
	
	protected void processFile(String[] cmdArgs, String absPath, ShellFile current){
		
		//Get regex pattern and file name being matched
		String regex = cmdArgs[1];
		String name = current.getName();
		
		//Compare file name against regex
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(name);
		
		//If match exists, append file's absolute path to result
		if (m.find()){
			
			//If first path in result, don't put add blank line
			if (result.equals("")){
				result += absPath;
			} else{
				result += "\n"+absPath;
			}
		}
	}
	
	protected void processFolder(String[] cmdArgs, String absPath, ShellDirectory current, JShell shell){
		
		//Match name of current directory against regex
		processFile(cmdArgs,absPath,(ShellFile)current);
		
		//Get list of directory contents
		String[] contents = current.toStringArray();
		
		for(String item: contents)
		{	
			//Make absolute path for item being process
			String new_path = absPath+item+"/";
			
			try 
			{
				//If item is a file
				if(!DirectoryNavigator.getFile(new_path, current, true).isDirectory()) {
					
					//Get and process file
					ShellFile sub_file = DirectoryNavigator.getFile(new_path, shell.getWorkDir(), true);
					processFile(cmdArgs,new_path,sub_file);
				}
				
				//If item is a folder
				else { 
					
					//Get and process folder recursively
					ShellDirectory sub_dir = (ShellDirectory) DirectoryNavigator.getFile(new_path, shell.getWorkDir(), true);
					processFolder(cmdArgs,new_path,sub_dir,shell);
				}
			} 
			
			//Catch error caused by getFile and append to errors
			catch (DirectoryException e)
			{
				errors += e.getMessage();
			}
		}
	}

	protected String finish(JShell shell) {

		if(isRedirecting) {
			
			// Send output of result to file
			errors += processOutput(redirectArgs, shell, result);
			
			// Return errors for printing to console
			return errors;
		}
		
		// Return errors and result for printing to console
		else
			return errors + result;
	}
	
	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
				"3", // Min Arguments
				"5", // Max Arguments
				"Return paths of objects in PATH with name that matches REGEX. " +
				"PATH may be relative to the current directory or may be full paths. " +
				"find \"REGEX\" [PATH]"}; // Usage

		return helpDocs;
	}
}