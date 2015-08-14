package src.filesys;

import java.util.Iterator;
import java.util.ListIterator;

public class ShellDirectory extends ShellFile implements Directory, Iterable<ShellFile> {

	private static final String DEFAULT_NAME = "New Folder";
	public static final String spacing = "\n";
	public static final long serialVersionUID = 42L;

	private ShellList files;

	public ShellDirectory() {
		this(DEFAULT_NAME);
	}

	public ShellDirectory(String name) {
		this(name, null);
	}

	public ShellDirectory(Directory parent) {
		this(DEFAULT_NAME, parent);
	}
	
	/**
	 * Constructor for ShellDirectory. Constructs a new empty directory with
	 * the name and parent directory given as arguments. If the name is
	 * the empty String, the name is reverted to the default name of ShellDirectories.
	 *
	 * @param name the name the directory will be given; if name is the empty String,
	 * the directory will be named according to the default name of ShellDirectories.
	 *
	 * @param parent the directory to assign as parent directory of the newly constructed directory.
	 */
	public ShellDirectory(String name, Directory parent) {
		
		// Decide name for directory
		String newName;
		if (name.length() == 0) {
			newName = DEFAULT_NAME;
		} else {
			newName = name;
		}
		
		// Main setup for directory
		this.name = newName;
		this.parentDir = parent;
		this.files = new SortedList();
		
	}
	
	public boolean isDirectory() {
		return true;
	}

	public boolean isShortCut() { //Need this -K
		return false;
	}
	
	public Directory getParent() {
		return parentDir;
	}
	
	/**
	 * Set the parent of this directory to given directory
	 *
	 * Use this method carefully. It will, in
	 * all cases, break the general folder structure,
	 * unless it is repaired with additional manual commands
	 *
	 * @param parent the directory to set as the parent of this directory
	 */
	public void setParent(Directory parent) {
		parentDir = parent;
	}
	
	public Directory getRoot() {
		
		// Perform a recursive call to find root directory
		if (parentDir == null) {
			return this;
		} else {
			return parentDir.getRoot();
		}

	}

	public String getName() {
		return name;
	}
	
	/**
	 * Change name of this directory only of @param name is not
	 * the empty string
	 * @param name the new name to be given to this directory
	 */
	public void setName(String name) {
		if (name.length() > 0) {
			this.name = name;
		}
	}
	
	public String getPath() {

		// If there is no parent, return "/"
		if (parentDir == null) {
			return "/";

		// If there is a parent, return "parentAddress/thisDir";
		// this is a recursive call, such that the result is the entire address
		} else {

			String parentName = parentDir.getPath();

			if (parentName == "/") {
				return parentName + name;
			} else {
				return parentName + "/" + name;
			}

		}

	}
	
	public String toString() {

		String result = new String();
		Iterator iterator = files.iterator();
		ShellFile inspect;
		
		// Accumulate the file names into the result
		while (iterator.hasNext()) {
			inspect = (ShellFile) iterator.next();
		    result += inspect.getName() + spacing;
		}
		
		// Cut off the last spacing if applicable
		if (result.length() > 0) {
			result = result.substring(0, (result.length() - spacing.length()));
		}

		return result;

	}
	
	public String[] toStringArray() {
		
		String[] result = new String[length()];
		Iterator iterator = files.iterator();
		ShellFile inspect;
		int length = length();
		
		int i;
		for (i = 0; i < length; i++) {
			inspect = (ShellFile) iterator.next();
		    result[i] = inspect.getName();
		}

		return result;

	}
	
	/**
	 * Get the file with fileName as its name from directory
	 *
	 * @param fileName the name of the file to fetch
	 * @return a ShellFile with name fileName, or null if no such file exists in directory
	 */
	public ShellFile getFile(String fileName) {
		return files.getFile(fileName);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<ShellFile> iterator() {
		return files.iterator();
	}
	
	/**
	 * Add a ShellFile to this directory
	 *
	 * @param fileAdd the file to add to this directory
	 * @exception ShellDirectoryException thrown if file or diretory with
	 * identical name to fileAdd already exists, or any other unexpected
	 * exception is thrown from the SortedList file list
	 */
	public void addFile(ShellFile fileAdd) throws DirectoryException {
		try {
			fileAdd.setParent(this); //Need for copy
			files.addFile(fileAdd);
		} catch (ShellListException e) {
			String message = e.getMessage();
			throw new DirectoryException(message);
		}
	}
	
	public void removeFile(String fileName) {
		files.removeFile(fileName);
	}
	
	public boolean hasFile(String fileName) {
		return files.hasFile(fileName);
	}
	
	/**
	 * Add a Directory to this directory
	 *
	 * @param difAdd the directory to add to this directory
	 * @exception ShellDirectoryException thrown if file or diretory with
	 * identical name to dirAdd already exists, or any other unexpected
	 * exception is thrown from the SortedList file list
	 */
	public void addDirectory(Directory dirAdd) throws DirectoryException {
		// dirAdd needs to know this directory is its parent
		dirAdd.setParent(this);

		// Add dirAdd as if it were a file
		this.addFile((ShellFile) dirAdd);

	}
	
	public void removeDirectory(String dirName) {
		removeFile(dirName);
	}
	
	public void remove() {
		if (parentDir != null) {
			parentDir.removeDirectory(name);
		}
	}
	
	public int length() {
		return files.length();
	}

	public boolean isEmpty() {
		return (length() == 0);
	}

	/**
	 * Evaluate whether this directory is a subdirectory of another directory
	 * A directory is a subdirectory of itself
	 *
	 * @param parentDir the directory which is evaluated as to
	 * whether it is a parent of this directory
	 * @return a boolean as to whether this directory is a subdirectory of
	 * the directory passed in as an argument.
	 */
	public boolean isSubDirectory(Directory parentDir) {
		if (this == parentDir ||
				(parentDir != null &&
				parentDir.isSubDirectory(parentDir))) {
			return true;
		}		
		return false;
	}

	public String typeToString() {
		return "directory";
	}
}
