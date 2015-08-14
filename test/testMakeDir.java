package test;

import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.Before;
import org.junit.Test;
import src.commands.*;
import src.shell.*;
import src.filesys.*;
import static org.junit.Assert.*;
import junit.framework.Assert;

public class testMakeDir {

	private static JShell shell;
	private static Command cmd;
	
	@Before
	public void setUp() {
		
		shell = new JShell();
		cmd = Executor.getCommand("mkdir");
		
		// Create folder structure: /Folder1/Folder2
		Directory d1 = new ShellDirectory();
		Directory d2 = new ShellDirectory("Folder1", d1);
		Directory d3 = new ShellDirectory("Folder2", d2);
		
		try {
			d1.addDirectory(d2);
			d2.addDirectory(d3);
			
		} catch(Exception e) {
			fail(e.getMessage());
		}
		
		// Set PWD to "/Folder1"
		shell.setWorkDir(d2);
	}
	
	/**
	 * 	Directory already exists
	 */
	@Test
	public void testDirectoryExists() {
		
		String[] s1 = {"mkdir", "/Folder1/Folder2"};
		String[] s2 = {"mkdir", "Folder2"};
		String[] s3 = {"mkdir", "./Folder2"};
		
		Assert.assertEquals("Error: A file with name Folder2 already exists\n", cmd.runCommand(s1, shell));
		Assert.assertEquals("Error: A file with name Folder2 already exists\n", cmd.runCommand(s2, shell));
		Assert.assertEquals("Error: A file with name Folder2 already exists\n", cmd.runCommand(s3, shell));
		
	}

	/**
	 * Parent directories don't exist
	 */
	@Test
	public void testDirectoryDoNotExist() {
		
		String[] s4 = {"mkdir", "/NotExists/Folder2"};
		Assert.assertEquals("Error: NotExists - No such file or directory exists\n", cmd.runCommand(s4, shell));
	}
	
	
	/**
	 * Create multiple new directories
	 */
	@Test
	public void testCreateMultipleDirectories(){
		
		String[] s5 = {"mkdir", "A"};
		String[] s6 = {"mkdir", "A/B"};
		String[] s7 = {"mkdir", "A/B/C"};
		String[][] arr = {s5, s6, s7};
		
		for(int i=0; i<arr.length; i++)
			cmd.runCommand(arr[i], shell);
		Assert.assertTrue(DirectoryNavigator.exists("/Folder1/A/B/C", shell.getWorkDir()));
	}
	
	
	/**
	 *  Change PWD to /A/B/C and re-test
	 */
	@Test
	public void testChangePwd() {
		
		String[] s5 = {"mkdir", "A"};
		String[] s6 = {"mkdir", "A/B"};
		String[] s7 = {"mkdir", "A/B/C"};
		String[][] arr = {s5, s6, s7};
		
		try {
			ShellFile dir = DirectoryNavigator.getFile("A/B/C", shell.getWorkDir());
			shell.setWorkDir( (ShellDirectory) dir);
			String[] s8 = {"mkdir", "C1/C2"};
			Assert.assertEquals("Error: C1 - No such file or directory exists", cmd.runCommand(s8, shell));
			
			for(int i=0; i<arr.length; i++)
				cmd.runCommand(arr[i], shell);
			Assert.assertTrue(DirectoryNavigator.exists("/Folder1/A/B/C/A/B/C", shell.getWorkDir()));
			
		} catch(DirectoryException e) {}
	}
	
	
	/**
	 *  Create multiple directories with relative paths
	 */
	@Test
	public void testCreateMultipleDirs() {
		
		String[] s5 = {"mkdir", "A"};
		String[] s6 = {"mkdir", "A/B"};
		String[] s7 = {"mkdir", "A/B/C"};
		String[][] arr = {s5, s6, s7};
		
		s5[1] = "../../A";
		s6[1] = "../../A/B";
		s7[1] = "../../A/B/C";
		
		shell.setWorkDir(shell.getWorkDir());
		for(int i=0; i<arr.length; i++)
			cmd.runCommand(arr[i], shell);
		Assert.assertTrue(DirectoryNavigator.exists("/A/B/C", shell.getWorkDir()));
	}
	
	
	/**
	 * Handle relative paths
	 */
	@Test
	public void testRelativePaths() {
		
		shell.setWorkDir(shell.getWorkDir());
		String[] s9 = {"mkdir", "../Test1"};
		cmd.runCommand(s9, shell);
		Assert.assertTrue(DirectoryNavigator.exists("/Test1", shell.getWorkDir()));
		Assert.assertFalse(DirectoryNavigator.exists("/Folder1/Test1", shell.getWorkDir()));
	}
	
}
