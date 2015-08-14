package src.filesys;

import src.shell.JShellException;

public class DirectoryException extends JShellException {

	static final long serialVersionUID = 42L;
	
	public DirectoryException(String message) {
		super(message);
	}
	
}