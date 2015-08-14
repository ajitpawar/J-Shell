package src.filesys;
import java.io.Serializable;

import src.shell.JShell;

public class ShellShortcut extends ShellFile implements Serializable {
	
	private String targetPath;
	private Directory dir; 
	private JShell workShell; //added
	private String BrokenLinkMessage = "Error: %s - No such %s exists";
	private boolean isValidLink = true;
	
	public ShellShortcut(String symblicLinkName,String targetPathforLink, JShell shell){
		name = symblicLinkName;
		targetPath = targetPathforLink;	
		dir = shell.getWorkDir();
		workShell = shell;
		
		try {
			ShellFile file = getTarget(dir);
			file.addShortcut(this);
		} catch (DirectoryException e) {
			// Catch
		}
	}
	
	public ShellFile getTarget(Directory presentWorkingDirectory) throws DirectoryException {
		if(!isValidLink){
			throw new DirectoryException(String.format(BrokenLinkMessage, targetPath, "file or directory"));
		}
		
		try {
			String TargetPath = getAbsoluteTargetPath();
			ShellFile targetPath = DirectoryNavigator.getFile(TargetPath, presentWorkingDirectory);
			return targetPath;
		} catch (DirectoryException e) {
			throw new DirectoryException(String.format(BrokenLinkMessage, targetPath, "file or directory"));
		}
	}
	
	protected String getAbsoluteTargetPath() throws DirectoryException {
		return DirectoryNavigator.getAbsolutePath(targetPath, dir);
	}
	
	public String getTargetPath() {
		return targetPath;
	}
	
	public JShell getShell() {
		return workShell;
	}
	
	public boolean isShortCut() { //Need this -K
		return true;
	}
	
	public void nullifyLink() {
		isValidLink = false;
	}

	public boolean isValidLink() {
		return isValidLink;
	}
	
}
