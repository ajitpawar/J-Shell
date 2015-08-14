package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.*;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import src.commands.*;
import src.shell.*;

public class testGrep {

	private static JShell shell;
	private static List<String> list;
	private static Command grepCmd = Executor.getCommand("grep");
	
	@Before
	public void setUp(){
		
		shell = new JShell();
		list = new ArrayList<String>();
		
		list.add("mkdir a a/a1 a/a2 a/a1/a1A a/a1/a1B a/a1/a1A/xyz a/a1/a1B/xyz");
		list.add("mkdir b b/b1 b/b2 b/b1/b1A b/b1/b1B b/b1/b1A/xyz b/b1/b1B/xyz");
		
		list.add("echo \"inside of 1\" > a/info");
		list.add("echo \"inside of 01\" > a/a1/info");
		list.add("echo \"inside of 02\" > a/a2/info");
		list.add("echo \"inside of 001\" > a/a1/a1A/info");
		list.add("echo \"inside of 002\" > a/a1/a1B/info");
		list.add("echo \"inside of xyz in left\" > a/a1/a1A/xyz/info");
		list.add("echo \"inside of xyz in left MULTILINE\" >> a/a1/a1A/xyz/info");
		list.add("echo \"inside of xyz in right\" > a/a1/a1B/xyz/info");
		list.add("echo \"inside of xyz in right MULTILINE\" >> a/a1/a1B/xyz/info");
		
		list.add("echo \"inside of 1\" > b/info");
		list.add("echo \"inside of 01\" > b/b1/info");
		list.add("echo \"inside of 02\" > b/b2/info");
		list.add("echo \"inside of left\" > b/b1/b1A/info");
		list.add("echo \"inside of 002\" > b/b1/b1B/info");
		list.add("echo \"inside of 002 MULTILINE\" >> b/b1/b1B/info");
		list.add("echo \"inside of xyz in 0001\" > b/b1/b1A/xyz/info");
		list.add("echo \"inside of xyz in 0001 MULTILINE\" >> b/b1/b1A/xyz/info");
		list.add("echo \"inside of xyz in right\" > b/b1/b1B/xyz/info");
				
		list.add("echo \"content of fileA 001\" > fileA");
		list.add("echo \"zzzz zzzz\" >> fileA");
		list.add("echo \"content of fileB 002\" > fileB");
		list.add("echo \"yyyy yyyy\" >> fileB");
		list.add("echo \"content of fileC 003\" > fileC");
		
		for(String s: list) {
			String cmdName = s.split("\\s+")[0];
			Command cmd = Executor.getCommand(cmdName);
			String result = cmd.runCommand(shell.parseString(s), shell);
			
			if(!result.isEmpty())
				fail(result);
		}	
	}
	
	@Test
	public void testGrepNonRecursive() {
		
		// Test for all words in all files
		String regx = "grep [0-9] fileA fileB fileC";
		String result = grepCmd.runCommand(shell.parseString(regx), shell);
		
		String expected = 
				"/fileA: content of fileA 001\n" +
				"/fileB: content of fileB 002\n" +
				"/fileC: content of fileC 003\n";
		
		Assert.assertEquals(expected, result);
		
	}
	
	@Test
	public void testGrepRecursiveWords() {
		
		// Test for all words in all files
		String regx = "grep -R [a-z] /";
		String result = grepCmd.runCommand(shell.parseString(regx), shell);

		String expected = 
				"/a/a1/a1A/info: inside of 001\n"
				+ "/a/a1/a1A/xyz/info: inside of xyz in left\n"
				+ "/a/a1/a1A/xyz/info: inside of xyz in left MULTILINE\n"
				+ "/a/a1/a1B/info: inside of 002\n"
				+ "/a/a1/a1B/xyz/info: inside of xyz in right\n"
				+ "/a/a1/a1B/xyz/info: inside of xyz in right MULTILINE\n"
				+ "/a/a1/info: inside of 01\n"
				+ "/a/a2/info: inside of 02\n"
				+ "/a/info: inside of 1\n"
				+ "/b/b1/b1A/info: inside of left\n"
				+ "/b/b1/b1A/xyz/info: inside of xyz in 0001\n"
				+ "/b/b1/b1A/xyz/info: inside of xyz in 0001 MULTILINE\n"
				+ "/b/b1/b1B/info: inside of 002\n"
				+ "/b/b1/b1B/info: inside of 002 MULTILINE\n"
				+ "/b/b1/b1B/xyz/info: inside of xyz in right\n"
				+ "/b/b1/info: inside of 01\n"
				+ "/b/b2/info: inside of 02\n"
				+ "/b/info: inside of 1\n"
				+ "/fileA: content of fileA 001\n"
				+ "/fileA: zzzz zzzz\n"
				+ "/fileB: content of fileB 002\n"
				+ "/fileB: yyyy yyyy\n"
				+ "/fileC: content of fileC 003\n";
		
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void testGrepRecursiveDigits() {
		
		// Test for all words in all files
		String regx = "grep -R [0-9] /";
		String result = grepCmd.runCommand(shell.parseString(regx), shell);
		
		String expected = 
				"/a/a1/a1A/info: inside of 001\n"
				+ "/a/a1/a1B/info: inside of 002\n"
				+ "/a/a1/info: inside of 01\n"
				+ "/a/a2/info: inside of 02\n"
				+ "/a/info: inside of 1\n"
				+ "/b/b1/b1A/xyz/info: inside of xyz in 0001\n"
				+ "/b/b1/b1A/xyz/info: inside of xyz in 0001 MULTILINE\n"
				+ "/b/b1/b1B/info: inside of 002\n"
				+ "/b/b1/b1B/info: inside of 002 MULTILINE\n"
				+ "/b/b1/info: inside of 01\n"
				+ "/b/b2/info: inside of 02\n"
				+ "/b/info: inside of 1\n"
				+ "/fileA: content of fileA 001\n"
				+ "/fileB: content of fileB 002\n"
				+ "/fileC: content of fileC 003\n";
		
		Assert.assertEquals(expected, result);
	}
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(testGrep.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

}
