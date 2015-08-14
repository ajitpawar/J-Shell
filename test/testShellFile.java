// import static org.junit.Assert.*;
// 
// import org.junit.Test;
// 
// public class testShellFile {
// 
// 	@Test
// 	public void test1() {
// 		ShellFile tShellFile = new ShellFile();
// 		
// 		assertEquals ("Test Case 1: Shellfile name not set to default.","New_File" ,tShellFile.getName());
// 		assertEquals ("Test Case 2: Empty shellfile contains data.", "", tShellFile.toString());
// 		
// 		//Test 3
// 		String contents = tShellFile.toString() + "\n";
// 		tShellFile.appendContents("");
// 		assertEquals("Test Case 3: Appending empty string doesn't add new blank line to content.", contents, tShellFile.toString());
// 	}
// 
// }
package test;

import src.filesys.*;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class testShellFile {
	
	ShellFile file1;
	ShellFile file2;
	
	@Before
	public void setUp() {
		file1 = new ShellFile();
		file2 = new ShellFile("File2");
	}
	
	@Test
	public void testNames() {
		assertEquals("Shellfile name should be default","New_File" ,file1.getName());
		assertEquals ("Shellfile getName does not retrieve name","File2", file2.getName());
		assertFalse("ShellFile should not be a directory", file1.isDirectory());
	}
	
	@Test
	public void testContents() {
		
		// Test 1
		assertEquals("Contents should be empty with empty file", file1.toString(), "");
		assertEquals("Contents should be empty with empty file", file2.toString(), "");
		
		// Setup 2
		file1.setContents("File Contents");
		file2.setContents("");
		
		// Test 2
		assertEquals("Contents should not be empty", file1.toString(), "File Contents");
		assertEquals("Contents should be empty with empty file", file2.toString(), "");
	}
	
	@Test
	public void testAppend() {
		
		// Setup 1
		file1.setContents("Line 1");
		file1.appendContents("Line 2");
		
		// Test 1
		assertEquals("Append Contents should append with new line", "Line 1\nLine 2", file1.toString());
		
		// Setup 2
		file1.appendContents("");
		assertEquals("Only a new line break should be added when appending the empty string", "Line 1\nLine 2\n", file1.toString());
	}
	
	@Test
	public void testRemove() throws Exception {
		
		// Setup 1
		ShellDirectory dir1 = new ShellDirectory();
		dir1.addFile(file1);
		
		// Test 1
		assertEquals("Directory should have added file", "New_File", dir1.toString());
		assertEquals("Directory should have added file", 1, dir1.length());
		
		// Setup 2
		file1.remove();
		
		// Test 2
		assertEquals("Directory should not have removed file", "", dir1.toString());
		assertEquals("Directory should not have removed file", 0, dir1.length());
	}
	
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testShellFile.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}