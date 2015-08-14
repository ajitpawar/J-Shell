/**
 * This class provides the implementation of Change Directory "get" command
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;
import java.io.*;
import java.net.URL;

public class Get extends Command {

	/**
	 * Default constructor
	 */
	public Get() {
		super("get");
	}
	
	/**
	 * Execute "get" command.
	 * Change current working directory
	 * @param commandArgs	The arguments passed by user
	 * @shell JShell	 JShell object for current session
	 * @return The result of this execution. Empty string if successful,
	 * error message otherwise
	 */	

	@Override
	public String runCommand(String[] commandArgs, JShell shell) {
		
		// Check if output needs to be redirected
		if(checkForRedirect(commandArgs)) {
			isRedirecting = true;
			commandArgs = getArgs(commandArgs);
		}
		
		if (isValidArgs(commandArgs)) {
			try{
				ShellFile fileFromUrl = makeFileFromUrl(commandArgs);
				saveFileinPresentDir(fileFromUrl, shell);
				result = "";
			}
			catch(Exception e){
				String nonExistLink = commandArgs[1];
				errors = String.format("No subject alternative DNS name matching %s found.", nonExistLink);
			}
		} else {
			errors = invalidArgsMessage;
		}
		
		// Send output to file
		if(isRedirecting) {
			errors = processOutput(redirectArgs, shell, result);
			return errors;
		}
		
		// Send output to console
		else 	return errors + result;
	}
	
	/**
	 * make a ShellFile that contains same contents that the file from url has
	 * and return it.
	 * @param commandArgs The arguments passed by user	
	 * @return ShellFile A file from url
	 * throw an Excpetion when it's not possible to make a file that contains contents from the given url
	 */	

	private ShellFile makeFileFromUrl(String[] commandArray) throws Exception {
		String urlAddress = commandArray[1];
		URL url = new URL(urlAddress);
		String ContentsOfUrl = getContentsFromUrl(url);
		String FilenameOfUrl = getFilenameFromUrl(url);
		ShellFile filefromUrl = new ShellFile(FilenameOfUrl);
		filefromUrl.setContents(ContentsOfUrl);
		return filefromUrl;

		
		// get all the contents from the url
	}
	
	/**
	 * get a contents from url
	 * @param String A url path
	 * @return String A cotents from the url
	 * throw an IOException when it's not possible to get a content from the given url
	 */	
	private String getContentsFromUrl(URL url) throws IOException {
		InputStream urlConnection = url.openStream();
		InputStreamReader urlByteReader = new InputStreamReader(urlConnection);
		BufferedReader urlStringReader = new BufferedReader(urlByteReader);
		String lineToRead = urlStringReader.readLine();
		String urlContents = "";
		while (lineToRead != null){
			urlContents += lineToRead;
			lineToRead = urlStringReader.readLine();
		}
		return urlContents;
	}
	
	/**
	 * get a filename to store from url e.g., www/dir1/file1.html => file name to store: file1.html
	 * @param String A url path
	 * @return String A filename from the url
	 */	
	private String getFilenameFromUrl(URL url) {
		String filename;
		if(url.getFile().contains("html")|| url.getFile().contains("txt")){
			filename = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
		}
		else{
			String urlAddress = url.toString() ;
			String [] urlArray= urlAddress.split("/");
			if (urlArray[urlArray.length -1].equals("") && urlArray.length > 2){
				filename = urlArray[urlArray.length - 2];
			}
			else{
				filename = urlArray[urlArray.length - 1];
			}
		}
		return filename;
	}
	/**
	 * save the file in the present working directory that retrieved  from url
	 * @param ShellFile A file retrieved from url 
	 * @param JShell A shell that is presently working
	 * throw DirectoryException if it is not possible to store the file in the present working directory
	 */
	private void saveFileinPresentDir(ShellFile fileFromUrl, JShell shell) throws DirectoryException {
		Directory presentDirectory = shell.getWorkDir();
		presentDirectory.addFile(fileFromUrl);
	}
	
	/**
	 * Check that exactly two arguments have been passed
	 * by the user
	 *  
	 * @param cmdArgs Arguments passed by the user
	 * @return True only if number of arguments is exactly two
	 */

	@Override
	protected boolean isValidArgs(String[] commandArgs) {
		return (commandArgs.length == 2);
	}
	
	/**
	 * Documentation for ChangeDir
	 *
	 * @return a String[] of the documentation of get command
	 */
	@Override
	public String[] getHelp() {
		// TODO Auto-generated method stub
		String[] helpDocs = {
				cmdName, // Name
				"1", // Min Arguments
				"1", // Max Arguments
				"Get a file from the given url and save the file in the present working directory", // Functionality
				"get [url]"}; // Usage
		return helpDocs;
	}

}

