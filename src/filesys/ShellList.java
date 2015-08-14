package src.filesys;

import src.shell.JShellException;

import java.util.Iterator;
import java.util.ListIterator;
import java.io.Serializable;

interface ShellList extends Serializable {
	
	public ShellFile getFile(String fileName);
	public void addFile(ShellFile addMe) throws ShellListException;
	public void removeFile(String fileName);
	public boolean hasFile(String fileName);
    public Iterator iterator();
	public ListIterator listIterator();
	public boolean isEmpty();
	public int length();
	
}

class ShellListException extends JShellException {

	static final long serialVersionUID = 42L;

	public ShellListException(String message) {
		super(message);
	}
	
}
