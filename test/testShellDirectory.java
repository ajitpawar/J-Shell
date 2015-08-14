package test;

import src.filesys.*;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class testShellDirectory {
	
	ShellDirectory dir;
	ShellDirectory subDir1;
	ShellDirectory subDir2;
	ShellDirectory subDir3;
	
	private String spacing = ShellDirectory.spacing;
	private String DEFAULT_NAME = "New Folder";
	
	@Before
	public void setUp() {
		dir = new ShellDirectory();
		subDir1 = new ShellDirectory("SubDir1", dir);
		subDir2 = new ShellDirectory("SubDir2", dir);
		subDir3 = new ShellDirectory("SubDir3", subDir1);
	}
	
	/**
	 * Test ShellDirectory when
	 * it is in the empty state
	 */
	@Test
	public void testEmptyDirectory() {
		assertTrue("Default name should be according to default name field", dir.getName().equals(DEFAULT_NAME));
		assertEquals("Default parent should be null", dir.getParent(), null);
		assertEquals("Empty directory should have length 0", dir.length(), 0);
		assertTrue("Directory should be true on isDirectory", dir.isDirectory());
		assertEquals("Directory with no parent should have itself as its root", dir.getRoot(), dir);
		assertTrue("An empty directory should have an empty string as result of toString", dir.toString().equals(""));
		assertFalse("Directory should not be true hasFile when file does not exists within this directory", dir.hasFile("Nonexistent"));
	}
	
	/**
	 * Test a Subdirectory of a directory
	 * @throws Exception 
	 */
	@Test
	public void testSubfolder() throws Exception {
		
		// Setup
		dir.addDirectory(subDir1);
		
		// Test
		assertTrue("getName does not retrieve directory name", subDir1.getName().equals("SubDir1"));
		assertEquals("getParent does not retrieve parent directory", subDir1.getParent(), dir);
		assertEquals("Directory should have length 1", dir.length(), 1);
		assertEquals("Directory should have length 0",subDir1.length(), 0);
		assertTrue("Sub-Directory should be true on isDirectory", subDir1.isDirectory());
		assertEquals("getRoot does not retrieve root", subDir1.getRoot(), dir);
		assertTrue("Directory toString does not return the list of directory/file names", dir.toString().equals("SubDir1"));
		assertTrue("Directory has subdirectory, but hasFile(subDirectoryName) is not true", dir.hasFile("SubDir1"));
	}
	
	/**
	 * Test a directory structure of
	 * height 3, i.e. a subdirectory
	 * within a subdirectory
	 * @throws Exception 
	 */
	@Test
	public void testSubfolderWithin() throws Exception {
		
		// Setup
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		
		// Test
		assertTrue("getName does not retrieve directory name", subDir3.getName().equals("SubDir3"));
		assertEquals("getParent does not retrieve parent directory", subDir3.getParent(), subDir1);
		assertEquals("Directory should have length 1", subDir1.length(), 1);
		assertEquals("Directory should have length 0", subDir3.length(), 0);
		assertTrue("Sub-Directory should be true on isDirectory", subDir3.isDirectory());
		assertEquals("getRoot does not retrieve root", subDir3.getRoot(), dir);
		assertTrue("Directory toString does not return the list of directory/file names", dir.toString().equals("SubDir1"));
		assertTrue("Directory toString does not return the list of directory/file names", subDir1.toString().equals("SubDir3"));
		assertTrue("Directory has subdirectory, but hasFile(subDirectoryName) is not true", dir.hasFile("SubDir1"));
		assertFalse("Directory has subdirectory within subdirectory, but hasFile(subDirectoryName) is true for folder in subfolder", dir.hasFile("SubDir3"));
		assertTrue("Directory has subdirectory, but hasFile(subDirectoryName) is not true", subDir1.hasFile("SubDir3"));
	}
	
	/**
	 * Test a directory with multiple items
	 * @throws Exception 
	 */
	@Test
	public void testDirectoryMultiple() throws Exception {
		
		// Setup
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		dir.addDirectory(subDir2);
		
		// Test
		assertTrue("getName does not retrieve directory name", subDir2.getName().equals("SubDir2"));
		assertEquals("getParent does not retrieve parent directory", subDir2.getParent(), dir);
		assertEquals("Directory should have length 2", dir.length(), 2);
		assertEquals("Directory should have length 0",subDir2.length(), 0);
		assertTrue("Sub-Directory should be true on isDirectory", subDir2.isDirectory()); 
		assertEquals("getRoot does not retrieve root", subDir2.getRoot(), dir); 
		assertTrue("Directory toString does not return the list of directory/file names", dir.toString().equals("SubDir1" + spacing + "SubDir2")); 
		assertTrue("Directory has subdirectory, but hasFile(subDirectoryName) is not true", dir.hasFile("SubDir2")); 
	}
	
	/**
	 * Test exception throwing on 
	 * adding directories with same name
	 */
	@Test
	public void testExceptionThrown() throws Exception {
	
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		dir.addDirectory(subDir2);	
		
		// Test
		boolean exceptionThrown = false;
		try {
			dir.addDirectory(subDir2);
		} catch (DirectoryException e) {
			exceptionThrown = true;
		}

		assertTrue("Exception should be thrown if files with pre-existing names are added", exceptionThrown); 
	}
	
	/**
	 * Test setName method, and 
	 * whether toString acts accordingly after
	 * a setName call
	 */
	@Test
	public void testSetName() throws Exception {
		
		// Setup
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		dir.addDirectory(subDir2);
		
		// Test
		subDir2.setName("SubDir2Mod");
		subDir3.setName("SubDir3Mod");
		assertTrue(dir.toString().equals("SubDir1" + spacing + "SubDir2Mod"));
		assertTrue(subDir1.toString().equals("SubDir3Mod"));
		assertTrue(subDir2.getName().equals("SubDir2Mod"));
		assertTrue(subDir3.getName().equals("SubDir3Mod"));
	}
	
	/**
	 * Test removing directories, eps.
	 * whether the newly removed directory has parent null
	 */
	@Test
	public void testRemoveDirectory() throws Exception {
		
		// Setup
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		dir.addDirectory(subDir2);
		
		// Test
		dir.removeDirectory("SubDir1");
		assertEquals("getParent should return null after directories has been removed", subDir1.getParent(), null); 
		assertEquals("Directory should have length 1", dir.length(), 1); 
		assertEquals("getRoot for removed directories should return the removed directory", subDir1.getRoot(), subDir1); 
		assertTrue("Directory toString does not return the list of directory/file names", dir.toString().equals("SubDir2")); 
		assertFalse("hasFile with removed directory's name should be false", dir.hasFile("SubDir1")); 
	}
	
	@Test
	public void testRemove() throws Exception {
		
		// Setup 1
		dir.addDirectory(subDir1);
		subDir1.addDirectory(subDir3);
		dir.addDirectory(subDir2);
		
		// Test 1
		assertTrue("Directory should have added directory", dir.hasFile(subDir1.getName()));
		assertTrue("Directory should have added directory", dir.hasFile(subDir2.getName()));
		
		// Setup 2
		subDir3.remove();
		
		// Test 2
		assertFalse("Directory should not have removed directory", subDir1.hasFile(subDir3.getName()));
		assertTrue("Directory should have added directory", dir.hasFile(subDir1.getName()));
		assertTrue("Directory should have added directory", dir.hasFile(subDir2.getName()));
		
	}

	@Test
	public void testIterator() throws Exception {

		// Setup 1
		int i;
		for (i = 0; i < 10; i++) {
			dir.addDirectory(new ShellDirectory("Directory " + Integer.toString(i + 1)));
		}

		int j = 0;
		for (ShellFile iterDir : dir) {
			j++;
		}

		// Test 1
		assertEquals("Iterator should iterate over all folders and files", 10, j);

		// Setup 2
		for (ShellFile iterDir : dir) {
			iterDir.remove();
		}

		assertTrue("Remove Iteration should empty the directory", dir.isEmpty());

	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testShellDirectory.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}