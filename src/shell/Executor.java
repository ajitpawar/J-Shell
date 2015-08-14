package src.shell;

import src.commands.*;

import java.util.*;

public class Executor {

	private static Map<String, Command> commandMap;

	static {
		
		commandMap = new HashMap<String, Command>();
		
		// Load all Command objects to memory
		commandMap.put("mkdir", new MakeDir());
		commandMap.put("cd", new ChangeDir());
		commandMap.put("ls", new ListFiles());
		commandMap.put("pwd", new PresentWorkDir());
		commandMap.put("mv", new Move());
		commandMap.put("cp", new Copy());
		commandMap.put("cat", new Cat());
		commandMap.put("echo", new Echo());
		commandMap.put("man", new Man());
		commandMap.put("rm", new Remove());
		commandMap.put("get", new Get());
		commandMap.put("reset", new Reset());
		commandMap.put("find", new Find());
		commandMap.put("ln", new Link());
		commandMap.put("grep", new Grep());
	}
	
	public static Command getCommand(String commandName) {
		return commandMap.get(commandName);
	}

	public String runCommand(String[] commandArray, JShell shell) {
		
		String commandNotFoundError = "%s: command not found";
		String result = new String();
		
		// If commandArray is empty, return the empty string
		// Else, handle the command, i.e. the first string in commandArray
		int commandLength = commandArray.length;
		if (commandLength != 0) {
			
			// Fetch the Command object corresponding to the request
			String commandName = commandArray[0];
			Command command = getCommand(commandName);
			
			// Check if command exists and set result accordingly
			if (command == null) {
				result = String.format(commandNotFoundError, commandName);
			} else {
				result = command.runCommand(commandArray, shell);
			}
		}
		
		return result;
	}
	
}
