package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.ListFiles;
import src.filesys.*;
import src.shell.*;


public class testListFiles {

	private static String result;
	private static String [] cmd;
	private static ListFiles ls;
	private static JShell shell;
	private static String spacing = ShellDirectory.spacing;
	
	@Before
	public void setUp(){
		ls = new ListFiles();
		shell = new JShell();
	}
	
	@Test
	public void testNoFiles() {
		cmd = new String[]{"ls"};
		result = ls.runCommand(cmd, shell);
		Assert.assertEquals("Empty directory should be empty string with ls", "", result);
	}
	
	@Test
	public void testOneFile() throws DirectoryException {
		
		// Setup
		cmd = new String[]{"ls"};
		Directory oneFileDir = new ShellDirectory();
		oneFileDir.addFile(new ShellFile("One File"));
		shell.setWorkDir(oneFileDir);
		result = ls.runCommand(cmd, shell);
		
		// Test
		Assert.assertEquals("Result does not match expected result", "One File", result);
		
	}
	
	@Test
	public void testMultiple() throws DirectoryException {
		
		// Setup
		Directory dir = new ShellDirectory();
		dir.addFile(new ShellFile("d"));
		dir.addFile(new ShellFile("b"));
		dir.addFile(new ShellFile("c"));
		dir.addFile(new ShellFile("a"));
		shell.setWorkDir(dir);
		cmd = new String[]{"ls"};
		result = ls.runCommand(cmd, shell);
		
		// Test
		Assert.assertEquals("Result should be sorted as expected", "a\nb\nc\nd", result);
	}
	
	@Test
	public void testPath() throws DirectoryException {
		
		// Setup 1
		shell.setWorkDir(directorySetUp());
		cmd = new String[]{"ls", "Dir1/Dir2/File1.txt"};
		result = ls.runCommand(cmd, shell);
		
		// Test 1
		assertEquals("Result of ls [filepath] is not as expected", "File Contents", result);
		
		// Setup 2
		cmd = new String[]{"ls", "Dir1/Dir2"};
		result = ls.runCommand(cmd, shell);
		
		// Test 2
		assertEquals("Result of ls [dirpath] is not as expected", "File1.txt", result);
	}
	
	@Test
	public void testNoFile() throws DirectoryException {
		
		// Setup 1
		shell.setWorkDir(directorySetUp());
		cmd = new String[]{"ls", "nonexistent"};
		result = ls.runCommand(cmd, shell);
		
		// Test 1
		assertEquals("Result of ls [nonexistent] is not as expected", "Error: nonexistent - No such file or directory exists\n", result);
		
		// Setup 2
		cmd = new String[]{"ls", "Dir1/nonexistent"};
		result = ls.runCommand(cmd, shell);
		
		// Test 2
		assertEquals("Result of ls [direxists/filenotexists] is not as expected", "Error: nonexistent - No such file or directory exists\n", result);
	}
	
	@Test
	public void testRecursive() throws DirectoryException {
		
		/// Setup 1
		shell.setWorkDir(directorySetUp());
		cmd = new String[]{"ls", "-R"};
		result = ls.runCommand(cmd, shell);
		
		// Test 1
		assertEquals("Result of ls [nonexistent] is not as expected", "/:\nDir1\n/Dir1:\nDir2\n/Dir1/Dir2:\nFile1.txt\n", result);
		
		// Setup 2
		cmd = new String[]{"ls", "-R", "Dir1"};
		result = ls.runCommand(cmd, shell);
		
		// Test 2
		assertEquals("Result of ls [dir] is not as expected", "/Dir1:\nDir2\n/Dir1/Dir2:\nFile1.txt\n", result);
		
		// Setup 3
		cmd = new String[]{"ls", "-R", "Dir1/Dir2"};
		result = ls.runCommand(cmd, shell);
		
		// Test 3
		assertEquals("Result of ls [dir] is not as expected", "/Dir1/Dir2:\nFile1.txt\n", result);
		
		// Setup 4
		cmd = new String[]{"ls", "-R", "Dir1/Dir2"};
		result = ls.runCommand(cmd, shell);
		
		// Test 4
		assertEquals("Result of ls [dir] is not as expected", "/Dir1/Dir2:\nFile1.txt\n", result);
		
	}
	
	private Directory directorySetUp() throws DirectoryException {
		
		Directory root = new ShellDirectory();
		Directory dir = new ShellDirectory("Dir1", root);
		Directory dir2 = new ShellDirectory("Dir2", dir);
		ShellFile file = new ShellFile("File1.txt");
		file.setContents("File Contents");
		root.addDirectory(dir);
		dir.addDirectory(dir2);
		dir2.addFile(file);
		
		return root;
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testListFiles.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
