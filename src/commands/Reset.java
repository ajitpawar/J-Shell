package src.commands;

import src.shell.JShell;
import src.shell.InputOutput;
import src.filesys.*;

public class Reset extends Command {

	private static final String ASK_USER = "Are you sure you want to reset the shell? >>> ";

	public Reset() {
		super("reset");
	}
	
	public String runCommand(String[] cmdArgs, JShell shell) {

		String result = new String();

		if (!isValidArgs(cmdArgs)) {
			result = invalidArgsMessage;
		} else {

			boolean answer = InputOutput.askUser(ASK_USER);
			if (answer) {
				Directory freshDirectory = new ShellDirectory("/");
				shell.setWorkDir(freshDirectory);
			}

		}

		return result;
	}

	protected boolean isValidArgs(String[] cmdArgs) {
		return (cmdArgs[0].equals(cmdName) && 
				cmdArgs.length == 1);
	}

	public String[] getHelp() {
		String[] helpDocs = {cmdName, // Name
							"0", // Min Arguments
							"0", // Max Arguments
							"Reset the shell and delete all its files", // Functionality
							"reset"}; // Usage
		
		return helpDocs;
	}

}