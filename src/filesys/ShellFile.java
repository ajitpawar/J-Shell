package src.filesys;

import src.shell.InputOutput;

import java.io.Serializable;
import java.util.ArrayList;

public class ShellFile implements Serializable {

	private static final String DEFAULT_NAME = "New_File";
	static final long serialVersionUID = 42L;

	protected String name;
	private String contents;
	protected Directory parentDir;
	protected ArrayList<ShellShortcut> shortcuts;
	
	public ShellFile() {
		this(DEFAULT_NAME);
	}

	public ShellFile(String name) {
		this.contents = new String();
		this.name = name;
		this.shortcuts = new ArrayList<ShellShortcut>();
	}

	public String toString() {
		return contents;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean isDirectory() 
	{
		return false;
	}	

	public boolean isShortCut() { //Need this -K
		return false;
	}
	
	public Directory getParent()
	{
		return parentDir;
	}
	
	public void setParent(Directory parent)
	{
		parentDir = parent;
	}

	public void addShortcut(ShellShortcut addMe) {
		shortcuts.add(addMe);
	}

	public void nullifyShortcuts() {
		for (ShellShortcut shortcut : shortcuts) {
			shortcut.remove();
		}

		if (shortcuts.size() > 0) {
			InputOutput.print("Associated Shortcuts have been removed\n");
		}
	}

	public boolean hasShortcut(ShellShortcut findMe) {
		return shortcuts.contains(findMe);
	}
	
	public String getPath() 
	{
		String parentPath = this.getParent().getPath();
		
		if(parentPath.equalsIgnoreCase("/"))
			return parentPath + this.name;
		else
			return parentPath + "/" + this.name;
	}
	
	public void appendContents(String appendMe) {
		contents += "\n" + appendMe;
	}
	
	public void setContents(String newContents) {
		contents = newContents;
	}
	
	public void remove() {
		if (parentDir != null) {
			parentDir.removeFile(name);
		}
	}
	
	public String typeToString() {
		return "file";
	}

}
