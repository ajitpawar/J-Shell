package test;

import static org.junit.Assert.*;
import org.junit.Test;
import src.shell.*;
import src.commands.*;
import src.filesys.*;

public class testGet {

	private JShell shell = new JShell();
	private Command testGetOject = new Get();
	private String output = new String();
	private Directory presentWorkDir = shell.getWorkDir(); 
	
	//All of them are tests for Get.runcommand  
	@Test
	public void standardFileTxt(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("get http://www.cs.cmu.edu/~spok/grimmtmp/073.txt"); 
		output = testGetOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		assertTrue(presentWorkDir.hasFile("073.txt"));

	}
	
	@Test
	public void standardFileHtml(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("get http://www.ub.edu/gilcub/SIMPLE/simple.html"); 
		output = testGetOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		assertTrue(presentWorkDir.hasFile("simple.html"));

	}
	
	@Test
	public void getGoogleHtmil(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("get https://www.google.ca/"); 
		output = testGetOject.runCommand(cmd, shell);
		assertTrue(output.equals(""));
		Command ls = new ListFiles();
		String [] cmdlist = {"ls"};
		String output= ls.runCommand(cmdlist, shell);
		assertTrue(output,presentWorkDir.hasFile("www.google.ca"));

	}
	
	
	@Test
	public void nonExistLink(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("get https://www.does.not.exist.link.in.this.world.com"); 
		output = testGetOject.runCommand(cmd, shell);
		assertTrue(output ,output.equals("No subject alternative DNS name matching https://www.does.not.exist.link.in.this.world.com found."));

	}
	
	@Test
	public void invalidNumberOfArgument(){
		// ln should be available to make an symbolic link for an non-exist path
		String[] cmd = shell.parseString("get link1 link2"); 
		output = testGetOject.runCommand(cmd, shell);
		assertTrue(output.equals("Error: Invalid number of arguments"));

	}
}
