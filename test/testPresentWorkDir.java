/**
 * This JUnit 4 class tests PresentWorkDir class
 * @version 1.0
 */
package test;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import src.commands.PresentWorkDir;
import src.filesys.*;
import src.shell.*;


public class testPresentWorkDir {
	JShell tJShell;
	PresentWorkDir tPwd;
	
	/**
	* setUp method to prepare testing
	*/
	@Before 
	public void setUp(){
		tJShell = new JShell();
		 tPwd = new PresentWorkDir(); 
	};
	
	/**
	* Test main method runCommand function in PresentWorkDir 
	*/
	@Test
	public void testrunCommnad(){
		
		//When the present working directory is the root
		String [] rCmd0 = {"pwd"};
		assertTrue("sould return a path for the root", tPwd.runCommand(rCmd0, tJShell).equals("/"));
		
		//When there is unnecessary arguments
		String [] rCmd1 = {"pwd", "unnecessary", "argument", "s"};
		String path = "/";
		String errormsg = "pwd: ignoring non-option arguments" + "\n";
		assertTrue("sould return a path with an error message", tPwd.runCommand(rCmd1, tJShell).equals(errormsg + path));
		
		//When the present working directory is not the root and there is no unnecessary argument.
		String path2 = "/A/B";
		Directory root = tJShell.getWorkDir();
		ShellDirectory folder1= new ShellDirectory("A", root); 
		ShellDirectory folder2 = new ShellDirectory("B", folder1);
		try {
			root.addDirectory(folder1);
			tJShell.setWorkDir(folder2);
		} catch (DirectoryException e) {
			// 
		}

		String [] rCmd2 = {"pwd"};
		assertTrue("sould return the present working directory", tPwd.runCommand(rCmd2, tJShell).equals(path2));
		
		//when the present working directory is not the root and there is unnecessary arguments
		String [] rCmd3 = {"pwd", "arg1", "arg2", "arg3"};
		assertTrue("sould return the present working directory without any unnecessary arguments",
				tPwd.runCommand(rCmd3, tJShell).equals(errormsg + path2));
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testPresentWorkDir.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}
}


