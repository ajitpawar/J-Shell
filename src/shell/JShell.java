package src.shell;

import src.filesys.*;

import java.util.ArrayList; 
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

public class JShell {
	
	private InputOutput io;
	private Executor executor;
	private Directory workDir;
	
	private static final String NOT_SAVED = "JShell could not save its state";
	private static final String ERROR_MESSAGE = "JShell has experienced an internal error:\n\n%s\n";
	private static final String SAVE_NAME = "jshell.data";
	
	/**
	 * Default constructor
	 * Create following session objects:
	 * IO (user input/output handler)
	 * Executor (command execution)
	 * ShellDirectory (current working directory)
	 */
	public JShell() {
		io = new InputOutput();
		executor = new Executor();
		workDir = new ShellDirectory();
	}
	
	/**
	 * Get working directory for current session
	 * @return Current working directory
	 */
	public Directory getWorkDir() {
		return workDir;
	}
	
	/**
	 * Set working directory for current session
	 * @param newDir Directory to be set as cwd
	 */
	public void setWorkDir(Directory newDir) {
		workDir = newDir;
	}
	
	
	/**
	 * The main Shell function:
	 * Prompt the user, send the arguments provided
	 * by user to Executor for corresponding command execution
	 * Print there result of execution back to user
	 * Catch any unexpected internal errors and print to user
	 *
	 * Uses: InputOutput, Executor, Directory
	 */
	public void runShell() {
		
		// Initialize variables
		String input;
		String output;
		String[] parsedArgs;
		
		// Exit flag
		Boolean exit = false;
		
		// Continue to run until exit condition
		while (!exit) {
		
			//Prompt for user input
			input = io.userPrompt(workDir);
			
			// Continue to operate on input only if the input has content,
			// i.e., if the user hasn't merely pressed Enter
			if (input.length() != 0) {
			
				// Check for exit condition
				if (input.compareToIgnoreCase("exit") == 0) {
					exit = true;
					
				// Check for the empty input, i.e. if user merely pressed Enter
				// Else compute appropriate output and continue to prompt
				} else {
					
					// Try to parse and execute the command
					// If no exceptions are thrown, print the output to the user
					try {
						parsedArgs = parseString(input);
						output = executor.runCommand(parsedArgs, this); 
						io.print(output);
						
					// Catch unexpected exceptions during parsing our executing
					} catch (Exception e) {
						io.print(String.format(ERROR_MESSAGE, e));
					}
				 	
				}
			}
		}

		// Exit code
		try {
			saveState();
		} catch (IOException e) {
			io.print(NOT_SAVED);
		}
	}
	
		   
	/**
	 * Split the user's input based on following pattern:
	 * 	Match any character(s) between two quotes,
	 * 	otherwise match valid characters only
	 * 	Following is the list of valid characters:
	 * 		letters (case insensitive)
	 * 		digits
	 * 		special chars - slash, dot, plus, minus, underscore 
	 * @param userInput Command input from user 
	 * @return String[] of parsed command arguments
	 */
	public String[] parseString (String userInput)  {
		
		ArrayList<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("(\".*\")|[^\" ]*");
		Matcher m = p.matcher(userInput);
		
		// Get all possible pattern matches
		while (m.find()) {			
			String match = m.group();		
			if (!match.isEmpty()) {		// Ignore matches of white space
				list.add(match.replaceAll("\"*",""));	// Remove all quotes
			}
		}

		// Parsed arguments
		return list.toArray(new String[list.size()]);
	}

	public void saveState() throws IOException {
		
		FileOutputStream fos;
		ObjectOutputStream out;

		fos = new FileOutputStream(SAVE_NAME);
		out = new ObjectOutputStream(fos);
		out.writeObject(workDir);
		out.close();

	}

	public void loadState() {

	    FileInputStream fis;
	    ObjectInputStream in;
	    Directory stateDir;
	    
	    try {
	    	fis = new FileInputStream(SAVE_NAME);
	    	in = new ObjectInputStream(fis);
	    	stateDir = (Directory) in.readObject();
	    	stateDir = stateDir.getRoot();
	    	in.close();
	    } catch(Exception e) {
	    	stateDir = new ShellDirectory("/");
	    }

	    workDir = stateDir;

	}
	
	public static void main(String[] args) {
		JShell shell = new JShell();
		shell.loadState();
		shell.runShell();
	}
}
