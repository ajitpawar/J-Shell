/**
 * This JUnit 4 class tests CommandEcho class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.Cat;
import src.commands.Command;
import src.filesys.*;
import src.shell.*;

public class testCat {
	JShell tJShell;
	Cat tCat;
	
	/**
	* setUp method to prepare testing
	*/
	@Before 
	public void setUp(){
		tJShell = new JShell();
		tCat = new Cat(); 
	};
	
	/**
	 * Test runCommand method in Cat
	 * 
	 */	
	@Test
	public void testrunCommand(){
		//Create directories for testing
		Directory root = tJShell.getWorkDir();
		ShellDirectory folder1= new ShellDirectory("A", root);
		ShellDirectory folder2 = new ShellDirectory("B", root);

		
		//Create files to add to directory
		ShellFile file1 = new ShellFile("empty");
		ShellFile file2 = new ShellFile("withcontents");
		String contents = "contents for test";
		
		//Set contents of file
		file2.setContents(contents);
		
		//Add files to directory
		try {
			folder2.addFile(file1);
			folder2.addFile(file2);
			
			//Add directories to root
		} catch (Exception e) {
			//
		}
		try {
			root.addDirectory(folder1);
			root.addDirectory(folder2);
		} catch (DirectoryException e) {
			e.printStackTrace();
		}
		
		
		
		//Test invalid path
		String [] cmd1 = {"cat", "/A/D"};
		assertTrue("worng path", tCat.runCommand(cmd1, tJShell).equals(String.format(Command.notFoundMessage, "/A/D", "file or directory")));

	    //Test valid path that leads to directory // Doesn't the cat also operate on folders? -D
		// String [] cmd2 = {"cat", "/A"};
		// 		assertEquals("path isn't ended with a file's name", String.format(Command.notFoundMessage, "/A", "file or directory"), tCat.runCommand(cmd2, tJShell));
  		
		//Valid path, but file does not exist
		String [] cmd3 = {"cat", "/A/test.txt"};
		assertTrue("file doesn't exit", tCat.runCommand(cmd3, tJShell).equals(String.format(Command.notFoundMessage,"/A/test.txt", "file or directory")));
		
		//Valid path with empty file exists
		String [] cmd4 = {"cat", "/B/empty"};
		assertEquals("empty file","", tCat.runCommand(cmd4, tJShell));
		
		//Valid path containing file with content exists
		String [] cmd5 = {"cat", "/B/withcontents"};
		assertEquals("file with contents", "contents for test", tCat.runCommand(cmd5, tJShell));

		//Invalid arguments
		String [] cmd6 = {"cat", "invalid", "arguments"};
		String errormsg = String.format(Command.notFoundMessage,"invalid", "file or directory");
		errormsg += "\n" + String.format(Command.notFoundMessage,"arguments", "file or directory");
		assertTrue("invalid arguments", tCat.runCommand(cmd6, tJShell).equals(errormsg));
		
		//No path provided, but file exists in root directory
		tJShell.setWorkDir(folder2);
		String [] cmd7 = {"cat", "empty"};
		assertTrue("failed for searching a file by file name",tCat.runCommand(cmd7, tJShell).equals(""));
		
		//Print contents from file1 and file2 on separate lines
		String [] cmd8 = {"cat", "empty", "withcontents"};
		assertEquals("failed for returning contents from multiple files",
					"\n"+"contents for test",
					tCat.runCommand(cmd8, tJShell));
		
		//Invalid arguments
		String [] cmd9 = {"cat"};
		assertTrue("invalid arguments", tCat.runCommand(cmd9, tJShell).equals(Command.invalidArgsMessage));
	}

	/**
	 * Test helper function IsCorrectPath method in Cat
	 * 
	 */
	public void testIsCorrectPath(){
		
		//Incorrect number of argument parameters
		String [] cmd1 = {"cat"};
			assertEquals(false, tCat.isCorrectPath(cmd1, tJShell.getWorkDir()));
			
		//Invalid arguments
		String [] cmd2 = {"cat", "invalid", "arguments"};
		assertEquals(false, tCat.isCorrectPath(cmd2, tJShell.getWorkDir()));
			
			
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testCat.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
		
}



