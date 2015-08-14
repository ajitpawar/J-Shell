/**
 * This JUnit 4 class tests all methods of DirectoryNavigator class
 * @version 1.0
 */
package test;

import src.filesys.*;
import src.shell.*;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.HashMap;

public class testDirectoryNavigator {
	
	private static HashMap<String,ShellDirectory> map = new HashMap<String,ShellDirectory>();
	
	/**
	 * Create folder structure for testing
	 */
	@Before
	public void setUp() {
		
		try {
		// Create folder structure: /Folder1/Folder2
		ShellDirectory d0 = new ShellDirectory();
		ShellDirectory d1 = new ShellDirectory("Folder1", d0);	// /Folder1
		ShellDirectory d2 = new ShellDirectory("Folder2", d1);	// /Folder1/Folder2
		d0.addDirectory(d1);
		d1.addDirectory(d2);
	
		ShellFile file0 = new ShellFile("file0");
		ShellFile file1 = new ShellFile("file1");
		ShellFile file2 = new ShellFile("file2");
		d0.addFile(file0);		// "/file0"
		d1.addFile(file1);		// "/Folder1/file1"
		d2.addFile(file2);		// "/Folder1/Folder2/file2"

		
		// Create folder structure: /Folder3/Folder4/Folder5
		ShellDirectory d3 = new ShellDirectory("Folder3", d0);
		ShellDirectory d4 = new ShellDirectory("Folder4", d3);
		ShellDirectory d5 = new ShellDirectory("Folder5", d4);
		d0.addDirectory(d3);		
		d3.addDirectory(d4);
		d4.addDirectory(d5);
		
		map.put("d0", d0);
		map.put("d1", d1);
		map.put("d2", d2);
		map.put("d3", d3);
		map.put("d4", d4);
		map.put("d5", d5);
		
		} catch (JShellException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	/**
	 * Test exists method in DirectoryNavigator
	 */
	@Test
	public void testExists() {
		Assert.assertTrue (DirectoryNavigator.exists("/", map.get("d4")));
		Assert.assertTrue (DirectoryNavigator.exists("/Folder1/Folder2/", map.get("d4")));
		Assert.assertTrue (DirectoryNavigator.exists("/Folder3/Folder4/Folder5", map.get("d4")));
		Assert.assertFalse (DirectoryNavigator.exists("/Folder1/Folder3", map.get("d4")));
		Assert.assertTrue (DirectoryNavigator.exists("Folder2/file2", map.get("d1")));			
	}

	/**
	 * Test getAbsolutePath method in DirectoryNavigator
	 */
	
	@Test
	public void testGetAbsolutePath() {
		
		try {
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("/A/B", map.get("d1")), "/A/B/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("/Blah/Blah", map.get("d1")), "/Blah/Blah/");
			
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath(".", map.get("d2")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath(".", map.get("d1")), "/Folder1/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath(".", map.get("d0")), "/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("./Folder2", map.get("d1")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("./", map.get("d2")), "/Folder1/Folder2/");		
						
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("..", map.get("d2")), "/Folder1/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../Folder2", map.get("d2")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../Folder1", map.get("d1")), "/Folder1/");

			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../../Folder1/Folder2", map.get("d2")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../../../Folder1/Folder2", map.get("d2")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../../..", map.get("d5")), "/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../../", map.get("d5")), "/Folder3/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("../", map.get("d5")), "/Folder3/Folder4/");
			
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("Folder2", map.get("d1")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("Folder1", map.get("d0")), "/Folder1/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("Folder1/Folder2", map.get("d0")), "/Folder1/Folder2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("Folder2/", map.get("d1")), "/Folder1/Folder2/");
			
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("./file0", map.get("d0")), "/file0/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("file2", map.get("d2")), "/Folder1/Folder2/file2/");
			Assert.assertEquals (DirectoryNavigator.getAbsolutePath("Folder2/file2", map.get("d1")), "/Folder1/Folder2/file2/");
			
		} catch (JShellException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	
	/**
	 * Test 1 of 3 for getAbsolutePath Exception error in DirectoryNavigator
	 */
	@Test
    public void getAbsoultePathException1() {
		try { DirectoryNavigator.getAbsolutePath("./Folder2", map.get("d2")); }
		catch (JShellException e) { 
			Assert.assertEquals("Error: Folder2 - No such file or directory exists", e.getMessage());
		}
    }

	/**
	 * Test 2 of 3 for getAbsolutePath Exception error in DirectoryNavigator
	 */
	@Test
    public void getAbsoultePathException2() {
		try { DirectoryNavigator.getAbsolutePath("../Folder2", map.get("d1")); }
		catch (JShellException e) { 
			Assert.assertEquals("Error: Folder2 - No such file or directory exists", e.getMessage());
		}
    }
	
	/**
	 * Test 3 of 3 for getAbsolutePath Exception error in DirectoryNavigator
	 */
	@Test
    public void getAbsoultePathException3() {
		try { DirectoryNavigator.getAbsolutePath("Folder2", map.get("d2")); }
		catch (JShellException e) { 
			Assert.assertEquals("Error: Folder2 - No such file or directory exists", e.getMessage());
		}
    }
	
	/**
	 * Test GetFile method in DirectoryNavigator
	 */
	@Test
	public void testGetFile() {
		
		try {
		// Get a folder object
		ShellFile dir1 = DirectoryNavigator.getFile("/Folder3/Folder4/Folder5/", map.get("d5"));
		Assert.assertEquals("Folder5", dir1.getName());
		
		// Get a file object
		ShellFile file1 = DirectoryNavigator.getFile("/Folder1/Folder2/file2/", map.get("d1"));
		Assert.assertEquals("file2", file1.getName());
		
		} catch (JShellException e) {
			Assert.fail(e.getMessage());
		}		
	}
	
	/**
	 * Test getFile Exception error in Directory Navigator
	 */
	@SuppressWarnings("unused")
	@Test
    public void testGetFileException() {
		try { ShellFile dir2 = DirectoryNavigator.getFile("/Folder3/NotExists", map.get("d5")); }
		catch (JShellException e) { 
			Assert.assertEquals("Error: NotExists - No such file or directory exists", e.getMessage());
		}
    }

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testDirectoryNavigator.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
