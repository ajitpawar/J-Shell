/**
 * This JUnit 4 class tests ShellShorcut class
 * @version 1.0
 */
package test;

import java.util.HashMap;

import org.junit.*;

import static org.junit.Assert.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.Command;
import src.commands.Echo;
import src.commands.Link;
import src.filesys.*;
import src.shell.*;

public class testShellShortcut {
	
	
	private JShell shell = new JShell();
	private String output = new String();
	private HashMap<String, ShellDirectory> linkMap = new HashMap<String, ShellDirectory>();
	
	@Before
	public void setUp(){
		
		setLinkMap();
		shell.setWorkDir(linkMap.get("root"));
	}
	
	//when the target is valid
	@Test
	public void testgetTarget(){ 
		ShellDirectory pwd = (ShellDirectory)shell.getWorkDir();
		ShellShortcut sc = new ShellShortcut("shortcut", "B" , shell);
		try {
			assertTrue(sc.getTarget(pwd).equals(linkMap.get("B")));
		} catch (DirectoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}

	//when the target is valid
	@Test
	public void testgetTargetPath(){
		ShellShortcut sc = new ShellShortcut("shortcut", "A/AChild1" , shell);
		assertTrue(sc.getTargetPath().equals("A/AChild1"));	
	}
	// when the target is invalid
	
	@Test
	public void testisShortCut(){
		ShellShortcut sc = new ShellShortcut("shortcut", "A/AChild1" , shell);
		assertTrue(sc.isShortCut() == true);
		ShellFile sf = new ShellFile();
		assertTrue(sf.isShortCut() == false);
	}
	//change the instance variable 
	@Test
	public void testnullifyLink(){
		ShellDirectory pwd = (ShellDirectory)shell.getWorkDir();
		ShellShortcut sc = new ShellShortcut("shortcut", "B" , shell);
		// when the target is moved or deleted nullifyLink should be done by rm and mv command 
		pwd.removeDirectory("B");
		sc.nullifyLink();
		try {
			sc.getTarget(pwd);
		} catch (DirectoryException e) {
			// TODO Auto-generated catch block
			assertTrue("it should have thrown DirectoryExcption",1 ==1);
		}

		
	}
	
	public void setLinkMap(){
		
		ShellDirectory root = new ShellDirectory();
		
		ShellDirectory dirA = new ShellDirectory("A", root);
		ShellDirectory subDirA = new ShellDirectory("AChild1", dirA);
		
		ShellDirectory dirB = new ShellDirectory("B", root);
		ShellDirectory dirC = new ShellDirectory("C", root);

		
		try {
			root.addDirectory(dirA);
			root.addDirectory(dirB);
			root.addDirectory(dirC);
			dirA.addDirectory(subDirA);

		
		} catch (Exception e) {
			e.printStackTrace();	
		}	
		
		linkMap.put("root", root);
		linkMap.put("A", dirA);
		linkMap.put("B", dirB);
		linkMap.put("C", dirC);
		linkMap.put("AChild1", subDirA);

		
	}



}
