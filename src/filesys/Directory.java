package src.filesys;

import src.shell.*;

import java.util.Iterator;
import java.io.Serializable;

public interface Directory extends Iterable<ShellFile>, Serializable {

	public int length();
	public boolean isDirectory();
	public void setParent(Directory parentDir);
	public void addFile(ShellFile fileAdd) throws DirectoryException;
	public void removeFile(String fileName);
	public void addDirectory(Directory dirAdd) throws DirectoryException;
	public void removeDirectory(String dirName);
	public void remove();
	public ShellFile getFile(String fileName);
	public boolean hasFile(String fileName);
	public String toString();
	public String[] toStringArray();
	public String getPath();
	public String getName();
	public void setName(String name);
	public Directory getParent();
	public Directory getRoot();
	public Iterator<ShellFile> iterator();
	public boolean isSubDirectory(Directory parentDir);	

}
