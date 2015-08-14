package test;

import static org.junit.Assert.*;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import src.shell.*;
import src.commands.*;
import src.filesys.*;

public class testLink {
	
	private JShell shell = new JShell();
	private Command testLinkOject = new Link();
	private HashMap<String, ShellDirectory> linkMap = new HashMap<String, ShellDirectory>();
	private String output = new String();
	
	@Before
	public void setUp(){
		
		setLinkMap();
		shell.setWorkDir(linkMap.get("root"));
	}
	
	//All of them are tests for the runcommand method
	@Test
	public void makeSymblicLinkForinvalidTarget(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("ln A/AChild1 D/NonExistPath"); 
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output ,output.equals(String.format("Error: %s - No such %s exists", "D/NonExistPath", "file or directory" )));

	}
	
		
	
	@Test
	public void alreadyValidPathforSymbolicPath(){
		// when ln path1 path2 and path1 is an already valid path
		// then it should make a symbolic path which has the same name as target's for path2 in path1 directory 
		String[] cmd = shell.parseString("ln A B"); 
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		assertTrue(linkMap.get("A").hasFile("B"));
	}
	

	@Test
	public void invalidSymbolicPath(){
		// when ln path1 path2 and path1 is an already valid path
		// then it should make a symbolic path which has the same name as target's for path2 in path1 directory 
		String[] cmd = shell.parseString("ln A/Z/E file1");
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output.equals(String.format(Command.notFoundMessage, "A/Z/E", "file or directory")));
		//System.out.flush();
		//assertTrue(output.equals(""));
	}

	@Test
	public void samePathforSymbolicAndTarget(){
		// when ln path1 path1 (path1 already exist)
		// then it should make a symbolic link called 'path1' under path1 directory 
		String[] cmd = shell.parseString("ln B/BChild1 B/BChild1"); 
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		assertTrue(linkMap.get("BChild1").hasFile("BChild1"));
	}
	
	@Test
	public void alreadyExistFileforSymbolicPath(){
		// when ln file1 target (file1 already exist)
		// then it should give an error message 
		String[] cmd = shell.parseString("ln file1 B/BChild1"); 
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output.equals(String.format(Command.fileExistsMessage, "file1")));
	}
	
	
	@Test
	public void symbolicLinkForPresentWorkDir(){
		// when ln symblicLink .  
		String[] cmd = shell.parseString("ln symblicForPresentDir ."); 
		output = testLinkOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		assertTrue(linkMap.get("root").hasFile("symblicForPresentDir"));
		ShellShortcut symlink =  (ShellShortcut) linkMap.get("root").getFile("symblicForPresentDir");
		try {
			assertTrue(symlink.getTarget(shell.getWorkDir()) == shell.getWorkDir());
		} catch (DirectoryException e) {
			System.out.print("It is not possible to get target from symbolic Link whose target is the present Working directory");
		}
		}
	
	
	
	
	public void setLinkMap(){
		
		ShellDirectory root = new ShellDirectory();
		
		ShellDirectory dirA = new ShellDirectory("A", root);
		ShellDirectory subDirA = new ShellDirectory("AChild1", dirA);
		
		ShellDirectory dirB = new ShellDirectory("B", root);
		ShellDirectory subDirB1 = new ShellDirectory("BChild1", dirB);
		ShellDirectory subDirB2 = new ShellDirectory("BChild2", dirB);
		
		ShellDirectory dirC = new ShellDirectory("C", root);
		ShellDirectory subDirC1 = new ShellDirectory("CChild1", dirC);
		
		ShellFile file1 = new ShellFile("file1");
		
		try {
			root.addDirectory(dirA);
			root.addDirectory(dirB);
			root.addDirectory(dirC);
			root.addFile(file1);
			dirA.addDirectory(subDirA);
			dirB.addDirectory(subDirB1);
			dirB.addDirectory(subDirB2);
			dirC.addDirectory(subDirC1);
		
		} catch (Exception e) {
			e.printStackTrace();	
		}	
		
		linkMap.put("root", root);
		linkMap.put("A", dirA);
		linkMap.put("B", dirB);
		linkMap.put("C", dirC);
		linkMap.put("AChild1", subDirA);
		linkMap.put("BChild1", subDirB1);
		linkMap.put("BCHild2", subDirB2);
		linkMap.put("CChild1", subDirC1 );
		
	}
}
