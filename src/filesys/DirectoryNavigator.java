package src.filesys;

import java.util.ArrayList;
import java.util.Map.*;
import java.util.HashMap;


/**
 * This class provides methods to validate and traverse a file or directory
 * path and retrieve the object represented by that path
 * @version 2.0
 */
public class DirectoryNavigator {

	// Not Found error message
	protected static String notFoundMessage = "Error: %s - No such %s exists";
	
	// Map of symbolic links
	public static HashMap<ShellFile, ShellShortcut> linkMap = new HashMap<ShellFile, ShellShortcut>();
	
	
	/**
	 * Check if file or directory exists at path	
	 * @param path The pathname representing file or directory
	 * @param pwd Current working directory
	 * @return True only if the file or directory exists
	 */
	public static boolean exists (String path, Directory pwd) {

		try {
			// Get absolute path first
			String absPath = getAbsolutePath(path, pwd);
		
			String[] pathArray = absPath.split("/");
			Directory current = pwd.getRoot();	
			ShellFile temp;
			
			// Root passed as sole argument
			if (pathArray.length == 1) 
				return true;
			
			// Traverse path starting from root
			for (int i=1; i<pathArray.length; i++){
				
				String name = pathArray[i];
				if (!current.hasFile(name))		// File not found.
					return false;
				
				temp = current.getFile(name);
				if (temp.getClass() == ShellDirectory.class)	 // Directory found. Traverse deeper.
					current = (Directory) temp;
				//else if(temp.getClass() == ShellShortcut.class)
					//current = (Directory) getShortcutTarget(temp);
				else continue;				
			}
	
			return true;
			
		} catch (DirectoryException e) {					
			return false;		// Invalid path. Cannot get absolute path.
		}		
		
	}
	
	
	/**
	 * Traverse path and return it's absolute path if valid
	 * @param path The relative pathname to traverse
	 * @param dir The working directory to start traversing from
	 * @return The absolute path of given relative path
	 * @throws DirectoryException when file or directory does not exist at 
	 * any given point in path
	 */
	public static String getAbsolutePath (String path, Directory workDir) 
			throws DirectoryException {

		String[] pathArray = path.split("/");
		String output = "";
		
		// Path is already absolute
		if (path.startsWith("/")) {
			if(!path.endsWith("/")) 	
				path += "/";		// Make every path end with a "/"
			return path;	
		}
		
		// Store names of traversed files
		ArrayList<String> values = new ArrayList<String>();		
		Directory current = workDir;		// starting point
		ShellFile temp;		
		
		// Iteratively traverse the path
		for (int i=0; i<pathArray.length; i++) {		
			String filename = pathArray[i];

			// Case 1: "." Relative to current directory
			if (filename.equalsIgnoreCase(".")) {
				continue;
			}
			
			// Case 2: ".." Relative to parent directory
			if (filename.equalsIgnoreCase("..")) {
				
				// Go up one level to parent directory
				if (current.getParent() != null)
						current = current.getParent();
				else{
					current = workDir.getRoot();	// we have reached topmost level ie. root
					values.clear();	// clear all stored filenames since our path
											// will start from root now
				}
			}
			
			// Case 3: (no dots) Relative to current directory
			else{
				
				// File or directory not found in current directory
				if (!current.hasFile(filename))
					throw new DirectoryException(String.format
							(notFoundMessage, filename, "file or directory"));
				
				temp = current.getFile(filename);
				if (temp.getClass() == ShellDirectory.class)	// Directory found. Traverse deeper.
					current = (Directory) temp;
				else if(temp.getClass() == ShellShortcut.class) {
					ShellFile f = DirectoryNavigator.getFile(temp.getPath(), current, true);
					if(f.getClass() == ShellFile.class)
						values.add(f.getName());
					else	current = (Directory) f;
				}					
				else	values.add(filename);		// File found. Store name.
			}
		}
		
		// String together names of all visited files
		for (String s: values){
			output += s + "/";
		}
		
		// Return absolute path 
		if (current.getPath().equals("/"))
			return current.getPath() + output;
		else	return current.getPath() + "/" + output;
		
	}

	/**
	 * Retrieve file or directory object represented by path
	 * 
	 * @param path The pathname for file or directory to retrieve
	 * @param pwd Current working directory
	 * @return The file or directory object retrieved
	 * @throws DirectoryException when file or directory does not exist at path
	 */
	public static ShellFile getFile (String path, Directory pwd) 
			throws DirectoryException {
		
		// Get absolute path first
		String absPath = getAbsolutePath(path, pwd);		
		
		String[] pathArray = absPath.split("/");
		int size = pathArray.length;
		Directory current = pwd.getRoot();
		ShellFile file = new ShellFile();
		
		// Check if path is valid
		if(!exists(absPath, pwd))
			throw new DirectoryException(String.format
					(notFoundMessage, pathArray[--size], "file or directory"));
		
		// Traverse path starting from root
		for(int i=1; i<size; i++) {			
			String fname = pathArray[i];
			file = current.getFile(fname);	
			
			if(file.getClass() == ShellFile.class || file.getClass() == ShellShortcut.class)	
				return file;
			else current = (Directory) file;
		}
		
		return (ShellFile) current;
	}
	
	
	/**
	 * Retrieve file, directory or link object represented by path
	 * Specify whether to retrieve link object or the target pointed to by link
	 * 
	 * @param path The pathname for file or directory to retrieve
	 * @param pwd Current working directory
	 * @param bool True if target pointed to by the link is to be returned
	 * False if link object itself is to be returned. 
	 * @return The file, directory or link object retrieved
	 * @throws DirectoryException when file, directory or link does not exist at path
	 */
	public static ShellFile getFile (String path, Directory pwd, boolean bool)
			throws DirectoryException {
		
		ShellFile file = getFile(path, pwd);
		if(file.getClass() == ShellShortcut.class && bool) {
			ShellShortcut link = (ShellShortcut) file;
			return link.getTarget(pwd);			
		}	
		
		return file;		
	}
	
}

