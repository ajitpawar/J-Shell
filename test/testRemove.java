package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.Remove;
import src.commands.Command;
import src.filesys.*;
import src.shell.*;


public class testRemove {

	private static String result;
	private static String [] cmd;
	private static Remove rm;
	private static JShell shell;
	private static Directory workDir;
	
	@Before
	public void setUp(){
		rm = new Remove();
		shell = new JShell();
		workDir = shell.getWorkDir();
	}

	@Test
	public void testNoFile() {
		cmd = new String[]{"rm", "nonexistent"};
		result = rm.runCommand(cmd, shell);
		String error = String.format(Command.notFoundMessage,
									"nonexistent", "file or directory");
		assertEquals("Remove a nonexistent file should return error", 
					error + "\n", result);
	}

	@Test
	public void testOneFile() throws Exception {

		// Setup
		workDir.addFile(new ShellFile("File1"));
		cmd = new String[]{"rm", "-f", "File1"};
		result = rm.runCommand(cmd, shell);

		// Test
		assertEquals("Successful remove should return empty string", "", result);
		assertFalse("Removed file should not exist", workDir.hasFile("File1"));
	
	}

	@Test
	public void testEmptyDirectory() throws Exception {

		// Setup
		workDir.addDirectory(new ShellDirectory("Dir1", null));
		cmd = new String[]{"rm", "-f", "Dir1"};
		result = rm.runCommand(cmd, shell);

		// Test
		assertEquals("Successful remove should return empty string", "", result);
		assertFalse("Removed file should not exist", workDir.hasFile("Dir1"));
	
	}

	@Test
	public void testNonEmptyDirectory() throws Exception {

		// Setup
		Directory dir1 = new ShellDirectory("Dir1", null);
		Directory dir2 = new ShellDirectory("Dir2", dir1);
		ShellFile file1 = new ShellFile("File1");
		ShellFile file2 = new ShellFile("File2");
		dir1.addFile(file1);
		dir2.addFile(file2);
		workDir.addDirectory(dir1);
		cmd = new String[]{"rm", "-f", "Dir1"};
		result = rm.runCommand(cmd, shell);

		// Test
		assertEquals("Successful remove should return empty string", "", result);
		assertFalse("Removed file should not exist", workDir.hasFile("Dir1"));
	
	}

	public void testSubDirectory() throws Exception {

		// Setup 1
		Directory dir1 = new ShellDirectory("Dir1", null);
		Directory dir2 = new ShellDirectory("Dir2", dir1);
		ShellFile file1 = new ShellFile("File1");
		ShellFile file2 = new ShellFile("File2");
		dir1.addFile(file1);
		dir2.addFile(file2);
		workDir.addDirectory(dir1);
		cmd = new String[]{"rm", "-f", "Dir1/Dir2"};
		result = rm.runCommand(cmd, shell);

		// Test 1
		assertEquals("Successful remove should return empty string", "", result);
		assertTrue("Parent of removed file should exist", workDir.hasFile("Dir1"));
		assertFalse("Removed file should not exist", dir1.hasFile("Dir2"));

		// Setup 2
		cmd = new String[]{"rm", "-f", "Dir1/File1"};
		result = rm.runCommand(cmd, shell);

		// Test 2
		assertEquals("Successful remove should return empty string", "", result);
		assertTrue("Parent of removed file should exist", workDir.hasFile("Dir1"));
		assertFalse("Removed file should not exist", dir1.hasFile("File1"));
	
	}

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testRemove.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}
	}

}
