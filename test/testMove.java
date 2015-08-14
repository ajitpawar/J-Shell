/**
 * This JUnit 4 class tests all methods of Move class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.HashMap;

import src.commands.Move;
import src.filesys.*;
import src.shell.*;

public class testMove {
	
	private testCopy copytest = new testCopy();
	private HashMap<String,ShellDirectory> directoryMap = copytest.getDirectoryMap();
	private JShell shell = new JShell();
	private Move m = new Move();
	private Directory root = directoryMap.get("mDir");
	
	//		 Folder structure used for testing:
	//		/Folder1/Folder2/
	//              /mix/
	//		/Folder3/Folder4/Folder5/New_File/
	//      /root file/
	//      /Folder 6/
	
	/**
	 * Test deleteDirectory method of Move
	 * @throws DirectoryException 
	 */
	@Test
	public void test_deleteDirectory() throws DirectoryException {
		shell.setWorkDir(root);
		
		//Delete sub-directory
		m.delete("/Folder1/Folder2/", shell);
		assertEquals("Failed to delete subdirectory",false,DirectoryNavigator.exists("Folder1/Folder2/", root));
		
		//Delete root-directory
		m.delete("/Folder1/", shell);
		assertEquals("Failed to delete root directory",false,DirectoryNavigator.exists("Folder1/", root));
		
		//Delete directory with space
		m.delete("/Folder 6/", shell);
		assertEquals("Failed to delete directory with space in name",false,DirectoryNavigator.exists("Folder 6/", root));

		
	}

	/**
	 * Test deleteFile method of Move
	 * @throws DirectoryException 
	 */
	@Test
	public void test_deleteFile() throws DirectoryException{
		shell.setWorkDir(root);
		
		//Delete file in sub-directory
		m.delete("/Folder1/mix/", shell);
		assertEquals("Failed to delete subdirectory",false,DirectoryNavigator.exists("Folder1/mix/", root));
		
		//Delete file in deep sub-directory
		m.delete("/Folder3/Folder4/Folder5/New_File/", shell);
		assertEquals("Failed to delete subdirectory",false,DirectoryNavigator.exists("/Folder3/Folder4/Folder5/New_File/", root));


		//Delete file in root 
		m.delete("/root file/", shell);
		assertEquals("Failed to delete root file",false,DirectoryNavigator.exists("root file", root));

	}

	/**
	 * Test runCommand method of Move
	 * @throws DirectoryException
	 */
	@Test
	public void test_runCommand() throws DirectoryException{
		shell.setWorkDir(root);
		
		//User shouldn't move entire root
		String[] commandArgs3 = "mv / /Folder1/Folder2/".split(" ");	
		assertEquals("Able to copy entire root","Error: Cannot copy or move root directory", m.runCommand(commandArgs3, shell));

		//Basic move: root folder to another one
		String[] commandArgs = "mv /Folder1 /Folder3".split(" ");
		m.runCommand(commandArgs, shell);
		assertEquals("Failed to move root folder to another root folder","Folder1\nFolder4", shell.getWorkDir().getFile("Folder3").toString());
		assertEquals("Failed to delete after move",false, DirectoryNavigator.exists("/Folder1/",root));
				
		//Basic move: root file with space in name to sub-directory
		String[] commandArgs5 = shell.parseString("cp \"/root file\" /Folder3/Folder4");
		m.runCommand(commandArgs5, shell);
		ShellFile clone5 = DirectoryNavigator.getFile("/Folder3/Folder4/root file/", shell.getWorkDir());
		assertEquals("Failed to move root folder to sub-directory","root file", clone5.getName());
		assertEquals("Failed to delete moved file from source",false, DirectoryNavigator.exists("/root file/", root));
		
		//Basic move: root folder with space in name to sub-directory
		String[] commandArgs6 = shell.parseString("cp \"/Folder 6\" /Folder3/Folder4");
		m.runCommand(commandArgs6, shell);
		assertFalse(DirectoryNavigator.exists("/Folder 6/", root));
		assertTrue(DirectoryNavigator.exists("/Folder3/Folder4/Folder 6/", root));
		ShellDirectory clone6 = (ShellDirectory)DirectoryNavigator.getFile("Folder3/Folder4/Folder 6", root);
		assertEquals("Failed to move root folder to sub-directory","Folder 6", clone6.getName());

	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testMove.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}
