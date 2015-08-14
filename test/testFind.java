package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.util.HashMap;
import src.filesys.*;
import src.shell.JShell;
import src.commands.*;


public class testFind {

	private HashMap<String,ShellDirectory> map = getDirectoryMap();
	private Find f = new Find();
	private JShell shell = new JShell();

	@Before
	public void setup() {
		shell.setWorkDir(map.get("mDir"));
		Command.errors = "";
		Command.result = "";
	}
	
	@Test
	public void testRunCommand0() {
				
		//Test0 - Find file in subdirectory
		String[] cmd = shell.parseString("find \"mix\" Folder1/"); 
		assertEquals("","/Folder1/mix/",f.runCommand(cmd, shell));				
	}
	
	@Test
	public void testRunCommand1(){
		
		//Test1 - Find file with space in name in root directory
		String[] cmd = shell.parseString("find \"root file\" /");
		assertEquals("","/root file/", f.runCommand(cmd, shell));
	}

	@Test
	public void testRunCommand2(){
		
		//Test2 - Find folder in subdirectory
		String[] cmd = shell.parseString("find \"Folder5\" /"); 
		assertEquals("","/Folder3/Folder4/Folder5/",f.runCommand(cmd, shell));
	}

	@Test
	public void testRunCommand3(){
		
		//Test3 - Find folder in root with space in name
		String[] cmd = shell.parseString("find \"Folder 6\" /"); 
		assertEquals("", "/Folder 6/", f.runCommand(cmd, shell));
	}
	
	@Test
	public void testRunCommand4(){
		
		//Test4 - Find folder and file in subdirectory
		String[] cmd = shell.parseString("find \"[a-z]+\" /Folder1"); 
		assertEquals("","/Folder1/\n/Folder1/Folder2/\n/Folder1/mix/",f.runCommand(cmd, shell));
	}

	@Test
	public void testRunCommand5(){
		
		//Test5 - Find everything in directory structure
		String[] cmd = shell.parseString("find \"[a-z]+\" /"); 

		String line1 = "/Folder 6/";
		String line2 = "/Folder 6/Folder1/";
		String line3 = "/Folder1/";
		String line4 = "/Folder1/Folder2/";
		String line5 = "/Folder1/mix/";
		String line6 = "/Folder3/";
		String line7 = "/Folder3/Folder4/";
		String line8 = "/Folder3/Folder4/Folder5/";
		String line9 = "/Folder3/Folder4/Folder5/New_File/";
		String line10 = "/root file/";
		
		String all = line1+"\n"+line2+"\n"+line3+"\n"+line4+"\n"+line5+"\n";
		all = all+line6+"\n"+line7+"\n"+line8+"\n"+line9+"\n"+line10;
		
		assertEquals("Not finding all content",all,f.runCommand(cmd, shell));
	}

	@Test
	public void testRunCommand6() throws DirectoryException {
				
		//Test0 - Find file in subdirectory
		String[] cmd = shell.parseString("find \"mix\" Folder1/ > file");
		f.runCommand(cmd, shell);
		Cat c = new Cat();
		String[] cmd2 = shell.parseString("cat file");
		assertEquals("","/Folder1/mix/",c.runCommand(cmd2, shell));				
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
		
		ShellDirectory mDir = new ShellDirectory("/");
		
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
		Result result = JUnitCore.runClasses(testFind.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.getTrace());
		}
	}
}
