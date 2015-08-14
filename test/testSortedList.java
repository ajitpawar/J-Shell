package test;

import src.filesys.*;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class testSortedList {

	SortedList fileList;
	ShellFile file;
	ShellDirectory dir;
	
	@Before
	public void setUp() {
		fileList = new SortedList();
		file = new ShellFile("File1");
		dir = new ShellDirectory("Dir1", null);
	}
	
	/**
	 * Test the status of a newly created empty list
	 */
	@Test
	public void testEmptyList() {
		assertEquals("Length of list when empty is not 0", fileList.length(), 0);
		assertTrue("List not isEmpty when length is 0", fileList.isEmpty()); //;
	}
	
	/**
	 * Test the functionality of adding files to the list
	 */
	@Test
	public void testAddFiles() throws Exception {
		
		// Setup
		fileList.addFile(file);
		fileList.addFile(dir);
		
		// Test
		assertEquals("Length of list with 2 files is not 2", fileList.length(), 2); //;
		assertFalse("List isEmpty when length > 0", fileList.isEmpty()); //;
		assertTrue("List not hasFile when file does exist", fileList.hasFile("File1"));
		assertTrue("List not hasFile when directory does exist", fileList.hasFile("Dir1")); //;
		assertFalse("List hasFile even though file does not exist", fileList.hasFile("Nonexistent File")); //;
	
	}
	
	/**
	 * Test the functionality of removing files
	 * with removeFile() on both ShellFiles and Directories
	 */
	@Test
	public void testRemoveFiles() throws Exception {
		
		// Setup
		fileList.addFile(file);
		fileList.addFile(dir);
		fileList.removeFile("Nonexistent File");
		fileList.removeFile("File1");
		fileList.removeFile("Dir1");
		
		// Test
		assertEquals("Length of list when empty is not 0", fileList.length(), 0); //;
		assertTrue("List not isEmpty when length is 0", fileList.isEmpty()); //;
		assertEquals("Removed file shoud have null as parent", file.getParent(),null); //;
	}
	
	/**
	 * Test the functionality of fetching files with
	 * getFile() on both ShellFiles and Directories
	 */
	@Test
	public void testGetFiles() throws Exception {
		
		// Setup
		fileList.addFile(file);
		fileList.addFile(dir);
		ShellFile fetchedFile = fileList.getFile("File1");
		ShellFile fetchedDir = fileList.getFile("Dir1");
		ShellFile fetchedNo = fileList.getFile("Nonexistent File");
		
		// Test
		assertEquals("Fetched file is not correct with getFile", fetchedFile, file); //;
		assertEquals("Fetched dir is not correct with getFile", fetchedDir, dir); //;
		assertEquals("Fetched is not null when file does not exist", fetchedNo, null); //;
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testSortedList.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}