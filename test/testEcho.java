/**
 * This JUnit 4 class tests Echo class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.Echo;
import src.filesys.*;
import src.shell.*;

public class testEcho {

	Echo tEcho;
	
	/**
	* setUp method to prepare testing
	*/
	@Before
	public void setUp() {

		tEcho = new Echo();

	}

	/**
	 * Test helper function getDirectoryFileArray method in Echo
	 * 
	 */
	@Test
	public void testgetDirectoryFileArray() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		ShellDirectory folder1 = new ShellDirectory("folder1", root);
		ShellDirectory folder2 = new ShellDirectory("folder2", folder1);
		ShellDirectory folder3 = new ShellDirectory("folder3", folder2);

		try {
			folder1.addDirectory(folder2);
			folder2.addDirectory(folder3);
		} catch (Exception e) {
			//
		}

		tJShell.setWorkDir(folder3);
		// Absolute path
		String[] st1 = tEcho.getDirectoryFileArray(
				"/folder1/folder2/folder3/file1", tJShell);
		String[] rs1 = { "/folder1/folder2/folder3", "file1" };
		assertArrayEquals(rs1, st1);
		// Relative path
		String[] st2 = tEcho.getDirectoryFileArray("file1", tJShell);
		String[] rs2 = { "/folder1/folder2/folder3", "file1" };
		try {
			Directory lastDir = (ShellDirectory) DirectoryNavigator.getFile(
					"/folder1/folder2/folder3", folder3);
			assertEquals(lastDir, folder3);
		} catch (DirectoryException e) {

		}

		assertArrayEquals(rs2, st2);
		// Random string path
		String[] st3 = tEcho.getDirectoryFileArray(
				"../folder2/folder3/../file1", tJShell);
		String[] rs3 = { "../folder2/folder3/..", "file1" };
		assertArrayEquals(rs3, st3);

	}

	/**
	 * Test helper function getPathContentsArray method in Echo
	 * 
	 */
	@Test
	public void testgetPathContentsArray() {

		String[] st1 = "echo >> path".split(" ");
		String[] rs1 = { "path", "" };
		String[] st2 = "echo > path".split(" ");
		String[] rs2 = { "path", "" };
		String[] st3 = "echo contents1 > path".split(" ");
		String[] rs3 = { "path", "contents1" };
		String[] st4 = "echo contents1 >> path".split(" ");
		String[] rs4 = { "path", "contents1" };
		String[] st5 = "echo contents1 contents2 >> path".split(" ");
		String[] rs5 = { "path", "contents1 contents2" };
		String[] st6 = "echo contents1 contents2 > path".split(" ");
		String[] rs6 = { "path", "contents1 contents2" };
		String[] st7 = "echo contents1 > path contents2".split(" ");
		String[] rs7 = { "path", "contents1 contents2" };
		String[] st8 = "echo contents1 >> path contents2".split(" ");
		String[] rs8 = { "path", "contents1 contents2" };
		String[] st9 = "echo contents1 >>".split(" ");
		String[] rs9 = { "", "contents1" };

		assertArrayEquals(rs1, tEcho.getPathContentsArray(st1));
		assertArrayEquals(rs2, tEcho.getPathContentsArray(st2));
		assertArrayEquals(rs3, tEcho.getPathContentsArray(st3));
		assertArrayEquals(rs4, tEcho.getPathContentsArray(st4));
		assertArrayEquals(rs5, tEcho.getPathContentsArray(st5));
		assertArrayEquals(rs6, tEcho.getPathContentsArray(st6));
		assertArrayEquals(rs7, tEcho.getPathContentsArray(st7));
		assertArrayEquals(rs8, tEcho.getPathContentsArray(st8));
		assertArrayEquals(rs9, tEcho.getPathContentsArray(st9));

	}

	/**
	 * Test helper function isPlainString method in Echo
	 * 
	 */
	@Test
	public void testisPlainString() {
		String[] st1 = "just plain text".split(" ");
		String[] st2 = "test with the set sign >".split(" ");
		String[] st3 = "test with the append sign >>".split(" ");
		assertEquals(true, tEcho.isPlainString(st1));
		assertEquals(false, tEcho.isPlainString(st2));
		assertEquals(false, tEcho.isPlainString(st3));

	}

	/**
	 * Test main method runCommand function in Echo
	 * @param None
	 */
	@Test
	public void testemptyInput() {
		JShell tJShell = new JShell();
		
		//No arguments provided
		String[] cmd = { "echo" };
		assertTrue("Empty input", tEcho.runCommand(cmd, tJShell).equals(""));
	}

	/**
	 * Test main method runCommand function in Echo 
	 * when user inputs String without > or >>
	 */
	@Test
	public void echoString() {

		// No input besides the command
		JShell tJShell = new JShell();
		String input = "echo This is a plain string";
		String[] cmd = input.split(" ");
		String output = "This is a plain string";
		assertTrue("Plain string", tEcho.runCommand(cmd, tJShell)
				.equals(output));
	}

	/**
	 * Test main method runCommand function in Echo when user wants
	 * append some content to a file by using relative path
	 */
	@Test
	public void appendRelativePath() {

		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		ShellFile file1 = new ShellFile("file1");
		try {
			root.addDirectory(folder1);
			folder1.addFile(file1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", "Something", ">>", "file1" };
		tEcho.runCommand(cmd, tJShell);
		assertTrue("Append Contents by using a relative path", file1.toString()
				.equals("" + "\n" + "Something"));
	}

	/**
	 * Test main method runCommand function in Echo
	 * User sets content using a relative path
	 */
	@Test
	public void setRelativePath() {

		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		try {
			root.addDirectory(folder1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", "Something", ">", "file1" };
		tEcho.runCommand(cmd, tJShell);
		assertTrue("Set Contents by using a relative path", folder1.getFile(
				"file1").toString().equals("Something"));
	}

	/**
	 * Test main method runCommand function in Echo
	 * User appends empty string to file using absolute path
	 */
	@Test
	public void appendEmptyStringAbsolutePath() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		ShellFile file1 = new ShellFile("file1");
		try {
			root.addDirectory(folder1);
			folder1.addFile(file1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", ">>", "/folder1/file1" };
		assertTrue("Append Contents by using a Absolute path", tEcho
				.runCommand(cmd, tJShell).equals(""));
		assertTrue("Append Contents by using a Absolute path", file1.toString()
				.equals(file1.toString()));

	}

	/**
	 * Test main method runCommand function in Echo
	 * User sets content in a file by using absolute path
	 */
	@Test
	public void setContentAbsolutePath() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);

		try {
			root.addDirectory(folder1);
		} catch (DirectoryException e1) {
			// 
		}

		String[] cmd = { "echo", "Something", ">", "/folder1/file1" };
		assertTrue("Set Contents by using a absolute path", tEcho.runCommand(
				cmd, tJShell).equals(""));
		assertTrue("Set Contents by using a absolute path", folder1.getFile(
				"file1").toString().equals("Something"));
	}

	/**
	 * Test main method runCommand function in Echo 
	 * Failed append due to invalid path
	 */
	@Test
	public void apeendwrongPath() {
		JShell tJShell = new JShell();
		String[] cmd = { "echo", ">>", "/A/C/D/E/f11" };
		tEcho.runCommand(cmd, tJShell);
		assertEquals(
				"wrong path to append a file",
				"Error: /A/C/D/E/f11 - No such file or directory exists",
				tEcho.runCommand(cmd, tJShell));
	}

	/**
	 * Test main method runCommand function in Echo
	 * Failed set file attempt due to invalid path
	 */
	@Test
	public void setContentWrongPath() {
		JShell tJShell = new JShell();
		// when there is no given input other than the command
		String[] cmd = { "echo", ">", "/A/B/D/C/E/F/f12" };
		tEcho.runCommand(cmd, tJShell);
		assertEquals(
				"wrong path to append a file",
				"Error: /A/B/D/C/E/F/f12 - No such file or directory exists",
				tEcho.runCommand(cmd, tJShell));
	}

	/**
	 * Test main method runCommand function in Echo
	 * Failed file set due to path containing directory, not file
	 */
	@Test
	public void setContentDirectoryPath() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		try {
			root.addDirectory(folder1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", ">", "/folder1" };
		tEcho.runCommand(cmd, tJShell);
		assertTrue("path ends with the name of directory", tEcho.runCommand(
				cmd, tJShell).equals(
				"Error: /folder1 - No such file or directory exists"));
	}

	/**
	 * Test main method runCommand function in Echo when user wants
	 * append file but user input path doesn't contains the file name (ends with
	 * a directory name)
	 */
	@Test
	public void appendDirectoryPath() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		try {
			root.addDirectory(folder1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", ">>", "/folder1" };
		tEcho.runCommand(cmd, tJShell);
		assertTrue("path ends with the name of directory", tEcho.runCommand(
				cmd, tJShell).equals(
				"Error: /folder1 - No such file or directory exists"));
	}

	/**
	 * Test main method runCommand function in Echo when user wants
	 * append file but user input file doesn't exist
	 */
	@Test
	public void appendNonExistFile() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		ShellFile file1 = new ShellFile("file1");
		try {
			root.addDirectory(folder1);
			folder1.addFile(file1);
		} catch (DirectoryException e1) {

		}
		String[] cmd = { "echo", ">>", "/folder1/file2" };
		tEcho.runCommand(cmd, tJShell);
		assertEquals("Non Exist File(append Contents)", 
					"Error: /folder1/file2 - No such file or directory exists",
					tEcho.runCommand(cmd, tJShell));
	}

	/**
	 * Test main method runCommand function in Echo when user wants set
	 * file but user input file already exists
	 */
	@Test
	public void setContentAlreadyExistFile() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		ShellFile file1 = new ShellFile("file1");
		try {
			root.addDirectory(folder1);
			folder1.addFile(file1);
		} catch (DirectoryException e1) {

		}
		String[] cmd = { "echo", ">", "/folder1/file1" };
		tEcho.runCommand(cmd, tJShell);
		assertTrue("Already Exsit File(Set Contents)", tEcho.runCommand(cmd,
				tJShell).equals("Error: A file with name file1 already exists"));
	}

	/**
	 * Test main method runCommand function in Echo when user input has
	 * invalid sign e.g., >>>
	 */
	@Test
	public void inadequateOperand() {
		JShell tJShell = new JShell();
		String[] cmd = { "echo", ">>>", "/A/C/f3" };
		assertTrue("Invalid operand", tEcho.runCommand(cmd, tJShell).equals(
				"Error: Illegal characters in arguments"));
	}

	/**
	 * Test main method runCommand function in Echo when user wants
	 * append contents by typing echo >> [path] [contents]
	 */
	@Test
	public void argumentDiffLocationAppendContents() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		tJShell.setWorkDir(folder1);
		ShellFile file1 = new ShellFile("file1");
		try {
			root.addDirectory(folder1);
			folder1.addFile(file1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", ">>", "/folder1/file1", "Something" };
		assertTrue("Echo >> file contents should work", tEcho.runCommand(cmd,
				tJShell).equals(""));
		assertTrue("Echo >> file contents should work", folder1
				.getFile("file1").toString().equals("" + "\n" + "Something"));
	}

	/**
	 * Test main method runCommand function in Echo when user wants set
	 * contents by typing echo > [path] [contents]
	 */
	@Test
	public void argumentDiffLocationSetContents() {
		JShell tJShell = new JShell();
		Directory root = tJShell.getWorkDir();
		Directory folder1 = new ShellDirectory("folder1", root);
		try {
			root.addDirectory(folder1);
		} catch (DirectoryException e1) {
			// 
		}
		String[] cmd = { "echo", ">", "/folder1/file1", "Something" };
		assertTrue("Echo >> file contents should work", tEcho.runCommand(cmd,
				tJShell).equals(""));
		assertTrue("Echo >> file contents should work", folder1
				.getFile("file1").toString().equals("Something"));
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testEcho.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
