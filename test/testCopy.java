/**
 * This JUnit 4 class tests all methods of Copy class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.HashMap;

import src.commands.Copy;
import src.filesys.*;
import src.shell.*;


public class testCopy {

	private HashMap<String,ShellDirectory> directoryMap = getDirectoryMap();
	private JShell shell = new JShell();
	private Copy c = new Copy();
	private Directory root = directoryMap.get("mDir");
		
	/**
	 * Test cloneFile method of Copy
	 */
	@Test
	public void test_cloneFile() {

		//Clone basic file
		ShellFile original = new ShellFile("clone");
		original.setContents("I am a clone.");
		ShellFile clone = c.cloneFile(original);
		
		//Clone empty file
		ShellFile blank = new ShellFile();
		ShellFile clone2 = c.cloneFile(blank);

		
		assertEquals("Clone doesn't have same name", "clone", clone.getName());
		assertEquals("Clone doesn't have same contents", "I am a clone.", clone.toString());
		assertEquals("Blank clone doesn't have same name", "New_File", clone2.getName());
		assertEquals("blank clone doesn't have same contents", "", clone2.toString());		

	}

	/**
	 * Test cloneDirectory method in Copy
	 * @throws Exception
	 * @throws Exception
	 */
	@Test
	public void test_cloneDirectory() throws Exception{
		
		shell.setWorkDir(root);
		
		//Clone sub-directory containing one file and one empty folder
		ShellDirectory original = (ShellDirectory)shell.getWorkDir().getFile("Folder1");
		ShellDirectory clone = c.cloneDirectory(original);
		assertEquals("Clone has wrong name.", "Folder1", clone.getName());
		assertEquals("Clone has wrong contents.", "Folder2\nmix", clone.toString());
		assertEquals("Clone's file parentDir incorrect.", "Folder1", clone.getFile("mix").getParent().getName());
		assertEquals("Clone's subfolder's parentDir incorrect.", "Folder1", clone.getFile("Folder2").getParent().getName());
		assertNull("Clone has parentDir(it shouldn't).", clone.getParent());
		
		//Clone sub-directory containing one empty folder
		ShellDirectory original1 = (ShellDirectory)shell.getWorkDir().getFile("Folder 6");
		ShellDirectory clone1 = c.cloneDirectory(original1);
		assertEquals("Clone has wrong name.", "Folder 6", clone1.getName());
		assertEquals("Clone has wrong contents.", "Folder1", clone1.toString());
		assertEquals("Clone's subfolder's parentDir incorrect.", "Folder 6", clone1.getFile("Folder1").getParent().getName());
		assertNull("Clone has parentDir(it shouldn't).", clone1.getParent());
		
		//Clone sub-directory containing one file
		shell.setWorkDir(directoryMap.get("subDir2B"));
		ShellDirectory original2 = (ShellDirectory)shell.getWorkDir().getFile("Folder5");
		ShellDirectory clone2 = c.cloneDirectory(original2);
		assertEquals("Clone has wrong name.", "Folder5", clone2.getName());
		assertEquals("Clone has wrong contents.", "New_File", clone2.toString());
		assertEquals("Clone's file parentDir incorrect.", "Folder5", clone2.getFile("New_File").getParent().getName());
		assertNull("Clone has parentDir(it shouldn't).", clone2.getParent());
		
		//Clone entire root directory
		ShellDirectory clone3 = c.cloneDirectory(directoryMap.get("mDir"));
		assertEquals("Clone has wrong name.", "New Folder", clone3.getName());
		assertEquals("Clone has wrong contents.", "Folder 6\nFolder1\nFolder3\nroot file", clone3.toString());
		assertEquals("Clone's file parentDir incorrect.", "New Folder", clone3.getFile("root file").getParent().getName());
		assertEquals("Clone's subfolder's parentDir incorrect.", "New Folder", clone3.getFile("Folder 6").getParent().getName());
		assertNull("Clone has parentDir(it shouldn't).", clone3.getParent());
		
		//Ensure folder created using deep recursion cloned properly
		Directory recurse_clone = (ShellDirectory)DirectoryNavigator.getFile("/Folder3/Folder4/Folder5/", directoryMap.get("mDir"));
		assertEquals("Clone has wrong name.", "Folder5", recurse_clone.getName());
		assertEquals("Clone has wrong contents.", "New_File", recurse_clone.toString());
		assertEquals("Clone's file has wrong name.", "New_File", recurse_clone.getFile("New_File").getName());
		assertEquals("Clone's file has wrong parentDir.", "Folder5", recurse_clone.getFile("New_File").getParent().getName());
		assertEquals("Clone has wrong parentDir.", "Folder4", recurse_clone.getParent().getName());
		

	}
	
	/**
	 * Test runCommand method in Copy
	 * @throws Exception
	 */
	@Test
	public void test_runCommand() throws Exception{
		
		shell.setWorkDir(root);
		
		//Basic copy: root folder to another one
		String[] commandArgs = "cp /Folder1 /Folder3".split(" ");
		c.runCommand(commandArgs, shell);
		assertEquals("Failed to copy root folder to another root folder","Folder1\nFolder4", shell.getWorkDir().getFile("Folder3").toString());

		//Basic copy: root folder to sub-directory
		String[] commandArgs1 = "cp /Folder1 /Folder3/Folder4".split(" ");
		c.runCommand(commandArgs1, shell);
		ShellDirectory clone = (ShellDirectory)DirectoryNavigator.getFile("/Folder3/Folder4/Folder1/", root);
		assertEquals("Failed to copy root folder to sub-directory","Folder1", clone.getName());
		
		//Complex copy: sub-directory with folders+files to sub-directory
		String[] commandArgs2 = "cp /Folder3/Folder4/ /Folder1/Folder2/".split(" ");
		c.runCommand(commandArgs2, shell);
		ShellDirectory clone1 = (ShellDirectory)DirectoryNavigator.getFile("/Folder1/Folder2/Folder4/", root);
		ShellDirectory Folder5 = (ShellDirectory)DirectoryNavigator.getFile("/Folder1/Folder2/Folder4/Folder5", root);
		assertEquals("Failed to sub-folder and contents to sub-directory","Folder4", clone1.getName());
		assertEquals("Copied folder missing contents","Folder1\nFolder5", clone1.toString());
		assertEquals("Copied sup-directory missing content","New_File", Folder5.toString());
		
		//User shouldn't copy entire root
		String[] commandArgs3 = "cp / /Folder1/Folder2/".split(" ");	
		assertEquals("Able to copy entire root","Error: Cannot copy or move root directory", c.runCommand(commandArgs3, shell));
		
		//Basic copy: root folder with space in name to sub-directory
		String[] commandArgs4 = shell.parseString("cp \"/Folder 6\" /Folder3/Folder4");
		c.runCommand(commandArgs4, shell);
		ShellDirectory clone4 = (ShellDirectory)DirectoryNavigator.getFile("/Folder3/Folder4/Folder 6/", shell.getWorkDir());
		assertEquals("Failed to copy root folder to sub-directory","Folder 6", clone4.getName());
		
		//Basic copy: root file with space in name to sub-directory
		String[] commandArgs5 = shell.parseString("cp \"/root file\" /Folder3/Folder4");
		c.runCommand(commandArgs5, shell);
		ShellFile clone5 = DirectoryNavigator.getFile("/Folder3/Folder4/root file/", shell.getWorkDir());
		assertEquals("Failed to copy root folder to sub-directory","root file", clone5.getName());
	}
	
	/**
	 * Helper function that creates HashMap of
	 * directories for main tests
	 * @return HashMap getDirectoryMap that contains all
	 * folders,files used for testing
	 */
	public HashMap<String, ShellDirectory> getDirectoryMap(){
		
		//		 Create following folder structures for testing:
		//		/Folder1/Folder2/
		//              /mix/
		//		/Folder3/Folder4/Folder5/New_File
		//      /root file
		//      /Folder 6
		
		HashMap <String, ShellDirectory> directoryMap = new HashMap<String, ShellDirectory>();	
		
		ShellDirectory mDir = new ShellDirectory();
		
		ShellDirectory subDir1A = new ShellDirectory("Folder1", mDir);
		ShellDirectory subDir2A = new ShellDirectory("Folder2", subDir1A);
		
		ShellDirectory subDir1B = new ShellDirectory("Folder3", mDir);
		ShellDirectory subDir2B = new ShellDirectory("Folder4", subDir1B);
		ShellDirectory subDir3B = new ShellDirectory("Folder5", subDir2B);
		
		ShellDirectory subDir1C = new ShellDirectory("Folder 6", mDir);
		ShellDirectory subDir2C = new ShellDirectory("Folder1", subDir1C);
		
		ShellFile file1 = new ShellFile();
		ShellFile file2 = new ShellFile("root file");
		ShellFile file3 = new ShellFile("mix");
		
		try {
			mDir.addDirectory(subDir1A);
			mDir.addDirectory(subDir1B);
			mDir.addDirectory(subDir1C);
			mDir.addFile(file2);
			subDir1A.addDirectory(subDir2A);
			subDir1A.addFile(file3);
			subDir1B.addDirectory(subDir2B);
			subDir1C.addDirectory(subDir2C);
			subDir2B.addDirectory(subDir3B);
			subDir3B.addFile(file1);
		} catch (Exception e) {
			e.printStackTrace();	
		}	
		
		directoryMap.put("mDir", mDir);
		directoryMap.put("subDir1A", subDir1A);
		directoryMap.put("subDir1B", subDir1B);
		directoryMap.put("subDir1C", subDir1B);
		directoryMap.put("subDir2A", subDir2A);
		directoryMap.put("subDir2B", subDir2B);
		directoryMap.put("subDir3B", subDir3B);
		
		return directoryMap;
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testCopy.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
	
}
