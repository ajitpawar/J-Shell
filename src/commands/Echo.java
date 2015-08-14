/**
 * This class provides the implementation of Echo "echo" command
 * @version 1.0
 */
package src.commands;

import src.shell.JShell;
import src.filesys.*;

import java.util.Arrays;
import java.util.List;

public class Echo extends Command {

	public Echo() {
		super("echo");
	}

	private String errormsg;

	/**
	 * main method to run Echo command
	 * 
	 * @param commandArgs
	 *            Array that contains user input
	 * @param shell
	 *            JShellobject that user is running presently
	 * @return Output String that user will get
	 */
	public String runCommand(String[] commandArgs, JShell shell) {

		// check the arguments is valid
		if (isValidArgs(commandArgs)) {
			// check the arguments is just plain string
			if (isPlainString(commandArgs)) {
				String userOut = new String();
				// if it is a plain string return the string
				for (int i = 1; i < commandArgs.length; i++) {
					userOut += commandArgs[i] + ' ';
				}

				return userOut.trim();
			}
			List<String> cmdList = Arrays.asList(commandArgs);

			String[] pathContents = getPathContentsArray(commandArgs);
			String[] directoryFile = getDirectoryFileArray(pathContents[0],
					shell);
			ShellDirectory lastDir;
			try {
				lastDir = (ShellDirectory) DirectoryNavigator.getFile(
						directoryFile[0], shell.getWorkDir(), true);
			} catch (DirectoryException e) {
				return String.format(notFoundMessage, pathContents[0], "file or directory");
			}

			// if it has a > (set sign), set the user given contents to a new
			// file
			if (cmdList.indexOf(">>") == -1) {
				// make a new File to set the Contents
				ShellFile setFile = new ShellFile(directoryFile[1]);
				setFile.setContents(pathContents[1]);
				// check that the file with the user given name can be added to
				// the directory(lastDir)
				boolean addFile = addFiletoDir(lastDir, setFile);
				if (addFile == false) {

					return String.format(fileExistsMessage, directoryFile[1]);
				} else {
					return "";
				}
			}

			else {
				// get the file to append the contents
				ShellFile appendFile;
				try {
					appendFile = DirectoryNavigator.getFile(pathContents[0],
							shell.getWorkDir(), true);
				} catch (DirectoryException e) {
					return String.format(notFoundMessage, pathContents[0], "file or directory");
				}

				appendFile.appendContents(pathContents[1]);
				return "";
			}
		} else {
			return errormsg;
		}
	}

	/**
	 * method to check if file can be added to the given directory
	 * 
	 * @param dir
	 *            Directory object that file may be added
	 * @param File
	 *            ShellFile Object that user wants to add
	 * @return boolean object tells that the file can be added or not
	 */
	private boolean addFiletoDir(Directory dir, ShellFile file) {

		try {
			dir.addFile(file);
			return true;

		} catch (DirectoryException e) {

			return false;
		}

	}

	/**
	 * Check that valid arguments have been passed by the user
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @return True only if argument has a valid form.
	 */
	protected boolean isValidArgs(String[] cmdArgs) {

		// if user arguments doesn't contain > or >> return true
		if (isPlainString(cmdArgs)) {

			return true;
		} else {
			// get Content to append or set to a file and path that contains
			// name of the file in the user input
			// return false if user input contains invalid number of >. e.g.,
			// echo [contents] >>> [path]
			String[] pathContent = getPathContentsArray(cmdArgs);
			if (pathContent[1].indexOf('>') != -1) {

				errormsg = illegalArgsMessage;
				return false;
			}
			// return false if user input doesn't contain file name e.g., echo
			// [contents] >>
			if (pathContent[0] == "") {

				errormsg = invalidArgsMessage;
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * check if the user command is just plain string that is not containing set
	 * sign or append sign
	 * 
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @return True only if argument doesn't have '>'
	 */

	public Boolean isPlainString(String[] cmdArgs) {

		// check if the user command is just plain string that is not containing
		// set sign > or append sign >>

		for (int i = 1; i < cmdArgs.length; i++) {

			int sign = cmdArgs[i].indexOf('>');
			if (sign != -1) {

				return false;
			}
		}
		return true;
	}

	/**
	 * get a path and contents separately from the user input in the PathCotnets
	 * Array
	 * 
	 * 
	 * @param cmdArgs
	 *            Arguments passed by the user
	 * @return {path, contents} e.g., user input: echo [contents] > path
	 */

	public String[] getPathContentsArray(String[] cmdArgs) {

		String contents = new String();
		String path = new String();
		for (int i = 1; i < cmdArgs.length; i++) {
			if (cmdArgs[i].equals(">") || cmdArgs[i].equals(">>")) {

				if (i < cmdArgs.length - 1) {
					path = cmdArgs[i + 1];
					i++;
				} else {
					path = "";
				}
			} else {

				contents += cmdArgs[i] + " ";
			}
		}

		String[] pathContents = { path, contents.trim() };
		return pathContents;
	}

	/**
	 * from the user given path(e.g., A/B/C/d.txt), separate the directory
	 * path(e.g., A/B/C) that doesn't contain the file name(d.txt) and file name
	 * and return the directoryFile Array{A/B/C, d.txt}.
	
	 * @param path
	 *            user given String that contains the path of the file that user
	 *            wants appends or set a contents to it. e.g., echo [contents] >
	 *            path
	 * @return {directory path, filename}
	 */

	public String[] getDirectoryFileArray(String path, JShell shell) {

		int lastSlash = path.lastIndexOf('/');
		String dirPath = new String();
		String filename = new String();
		// if path just contains the file name that get the present working
		// directory for the dirPath
		if (lastSlash == -1) {

			dirPath = shell.getWorkDir().getPath();
			filename = path;
		}
		// if path contains / then separate the directory name and file name
		// from the path
		else {

			dirPath = path.substring(0, lastSlash);
			filename = path.substring(lastSlash + 1);
		}
		String[] directoryFile = { dirPath, filename };
		return directoryFile;
	}

	/**
	 * Documentation for Echo
	 * 
	 * @return a String of the documentation of Echo
	 */
	public String[] getHelp() {

		String[] helpDocs = {
				cmdName, // Name
				"0", // Min Arguments
				"Infinity", // Max Arguments
				"Print the given text from user on the Command window"
						+ "\n"
						+ "Write the given text from user on the given file followed by '>'"
						+ "\n"
						+ "Append the given text from user to the given file followed by '>>'", // Functionality
				"echo [text]" + "\n" + "echo [text] > [file]" + "\n"
						+ "echo [text] >> [file]" + "\n"
						+ "echo > [file] [text]" + "\n"
						+ "echo >> [file] [text]" }; // Usage

		return helpDocs;
	}
}
