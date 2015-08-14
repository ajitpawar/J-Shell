/**
 * This JUnit 4 class tests ChangeDir class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.ChangeDir;
import src.commands.Command;
import src.filesys.*;
import src.shell.*;


public class testChangeDir {

	Command cd;
	JShell shell;
	String[] cmdArgs;

	@Before
	public void setUp() {
		cd = new ChangeDir();
		shell =  new JShell();
	}

	/**
	 * Test runCommand method in ChangeDir
	 * @throws DirectoryException
	 */
	@Test
	public void testRunCommand() throws Exception {
		
		// Setup
		ShellDirectory d0 = new ShellDirectory();
		ShellDirectory d1 = new ShellDirectory("folder1", d0);
		ShellDirectory d2 = new ShellDirectory("folder2", d1);
		ShellFile f1 = new ShellFile("file1");
		d0.addDirectory(d1);
		d1.addDirectory(d2);	// "/folder1/folder2"
		d1.addFile(f1);			// "/folder1/file1"
			
		// Current working directory is root
		shell.setWorkDir(d0);
		Assert.assertEquals("/", shell.getWorkDir().getPath()); // before
			
		// Current working directory should change to "folder1"
		cmdArgs = new String[]{"cd", "folder1"};
		cd.runCommand(cmdArgs, shell);
		Assert.assertEquals("/folder1", shell.getWorkDir().getPath()); // after
			
		// Try changing to a file instead of directory
		cmdArgs = new String[]{"cd", "file1"};
		Assert.assertEquals("Error: file1 - No such directory exists", cd.runCommand(cmdArgs, shell));
	
		// Current working directory should change to "/folder1/folder2"
		cmdArgs = new String[]{"cd", "folder2"};
		cd.runCommand(cmdArgs, shell);
		Assert.assertEquals("/folder1/folder2", shell.getWorkDir().getPath()); // after
			
		cmdArgs = new String[]{"cd", "user", "home"};
		Assert.assertEquals ("Error: Invalid number of arguments", cd.runCommand(cmdArgs, shell));
			
		cmdArgs = new String[]{"cd", "NotExists"};
		Assert.assertEquals ("Error: NotExists - No such directory exists", cd.runCommand(cmdArgs, shell));
	}

	@Test
	public void testNonExistent() throws Exception {

		// Setup 1
		cmdArgs = new String[]{"cd", "nonexistent"};

		// Test 1
		Assert.assertEquals("Should return an error", "Error: nonexistent - No such directory exists", cd.runCommand(cmdArgs, shell));

		// Setup 2
		ShellDirectory root = new ShellDirectory();
		ShellDirectory dir1 = new ShellDirectory("Dir1", root);
		root.addDirectory(dir1);
		shell.setWorkDir(root);
		cmdArgs = new String[]{"cd", "Dir1/nonexistent"};

		// Test 2
		assertEquals("Nonexistent should return error", "Error: Dir1/nonexistent - No such directory exists", cd.runCommand(cmdArgs, shell));

	}

	@Test
	public void goBack() throws Exception {

		// Setup 1
		ShellDirectory root = new ShellDirectory();
		ShellDirectory dir1 = new ShellDirectory("Dir1", root);
		root.addDirectory(dir1);
		shell.setWorkDir(dir1);
		cmdArgs = new String[]{"cd", ".."};

		// Test 1
		assertEquals("Should go back successfully", "", cd.runCommand(cmdArgs, shell));
		assertEquals("Should go back to parent directory", shell.getWorkDir(), root);

		// Setup 2
		shell.setWorkDir(dir1);
		cmdArgs = new String[]{"cd", "../"};

		// Test 2
		assertEquals("Should go back successfully", "", cd.runCommand(cmdArgs, shell));
		assertEquals("Should go back to parent directory", shell.getWorkDir(), root);

	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testChangeDir.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
