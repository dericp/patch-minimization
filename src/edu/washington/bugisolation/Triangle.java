package edu.washington.bugisolation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import difflib.DiffUtils;
import difflib.Patch;
import edu.washington.bugisolation.util.Operations;

public class Triangle implements Project {
	
    public void generateDiff() {
        
    }
    
	private final String ANT = "/opt/local/share/java/apache-ant/bin/ant ";
	
	/* a projectInfo that stores all the information of the project */
	private ProjectInfo projectInfo;
	
	public Triangle(ProjectInfo proj) {
		this.projectInfo = proj;
	}
	
	private int gitOperation(String command, String directory) {
		return Operations.commandLine("git " + command, directory);
	}
	
	public void checkout() {
	    checkoutFixed();
	    checkoutBuggy();
	}
	
	private void checkoutFixed() {
		System.out.println("Checking out fixed version");
		Operations.commandLine (
				"cp -a /Users/dpang/Documents/UW/Research/TriangleFiles/Triangle_"
				+ projectInfo.getProjectVersion() + "_fixed " + ProjectInfo.WORKSPACE
				+ projectInfo.getFixedName()
				, ProjectInfo.WORKSPACE);
		gitOperation("tag TRI_" + projectInfo.getFixedName(), ProjectInfo.WORKSPACE + projectInfo.getFixedName());
	}

	private void checkoutBuggy() {
		System.out.println("Checking out buggy version");
		Operations.commandLine (
				"cp -a /Users/dpang/Documents/UW/Research/TriangleFiles/Triangle_"
				+ projectInfo.getProjectVersion() + "_buggy " + ProjectInfo.WORKSPACE
				+ projectInfo.getBuggyName()
				, ProjectInfo.WORKSPACE);
		gitOperation (
				"tag TRI_" + projectInfo.getBuggyName(), ProjectInfo.WORKSPACE
				+ projectInfo.getBuggyName());
	}
	
	public int compileModified() {
		if (projectInfo.isFixedToBuggy()) {
			return Operations.commandLine( 
					ANT + "compile", ProjectInfo.WORKSPACE + projectInfo.getFixedName());
		} else {
			return Operations.commandLine (
					ANT + "compile", ProjectInfo.WORKSPACE + projectInfo.getBuggyName());
		}
	}

	public int test() {
		if (projectInfo.isFixedToBuggy()) {
			return Operations.commandLine (
					ANT + "test", ProjectInfo.WORKSPACE+ projectInfo.getFixedName());
		} else {
			return Operations.commandLine (
					ANT + "test", ProjectInfo.WORKSPACE + projectInfo.getBuggyName());
		}
	}

	/**
	 * Gets to the tests from a file that is in the ant test report format.
	 * 
	 * @param filePath		a String, the path of the file on the machine
	 * @return				a List of Strings, containing the information of the file at
	 * 						the designated file-path
	 */
	private List<String> getTests(String filePath) {
        List<String> lines = Operations.fileToLines(filePath);
        List<String> result = new LinkedList<String>();
        Iterator<String> iter = lines.listIterator();
        while(iter.hasNext()) {
        	String line = iter.next();
        	if (line.startsWith("---")) {
        		result.add(line);
        		result.add(iter.next());
        	}
        }
        return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#getFailingTests()
	 */
	@Override
	public List<String> getFailingTests() {
		System.out.println("Getting failing tests");
		
		if (projectInfo.isFixedToBuggy()) {
			return getTests (
					ProjectInfo.WORKSPACE + projectInfo.getFixedName() + "/.failing_tests");
		} else {
			return getTests (
					ProjectInfo.WORKSPACE + projectInfo.getBuggyName() + "/.failing_tests");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#getPatch()
	 */
	@Override
	public Patch<String> generatePatch() {
		System.out.println("Generating patch between the two project versions");
		
		List<String> fixedFile = Operations.fileToLines (
				ProjectInfo.WORKSPACE + projectInfo.getFixedName() 
				+ '/' + projectInfo.getModifiedPath(".java"));
		List<String> buggyFile = Operations.fileToLines (
				ProjectInfo.WORKSPACE + projectInfo.getBuggyName()
				+ '/' + projectInfo.getModifiedPath(".java"));
		projectInfo.setFixedFile(fixedFile);
		projectInfo.setBuggyFile(buggyFile);
			
		if (projectInfo.isFixedToBuggy()) {
			return DiffUtils.diff(fixedFile, buggyFile);
		} else {
			return DiffUtils.diff(buggyFile, fixedFile);
		}
	}
	
	public int applyPatch() {
		System.out.println("Applying the current patch");
		
		if (projectInfo.isFixedToBuggy()) {
			return gitOperation (
					"apply " + ProjectInfo.WORKSPACE + projectInfo.getFullProjectName()
					+ ".diff", ProjectInfo.WORKSPACE + projectInfo.getFixedName());

		} else {
			return gitOperation (
					"apply " + ProjectInfo.WORKSPACE + projectInfo.getFullProjectName()
					+ ".diff", ProjectInfo.WORKSPACE + projectInfo.getBuggyName());
		}
	}
	
	public void reset() {
		System.out.println("Resetting the project");
		
		if (projectInfo.isFixedToBuggy()) {
			gitOperation (
					"reset --hard TRI_" + projectInfo.getFixedName()
					, ProjectInfo.WORKSPACE + projectInfo.getFixedName());
			gitOperation (
					"clean -xfd", ProjectInfo.WORKSPACE + projectInfo.getFixedName());
		} else {
			gitOperation (
					"reset --hard TRI_" + projectInfo.getBuggyName()
					, ProjectInfo.WORKSPACE + projectInfo.getBuggyName());
			gitOperation (
					"clean -xfd", ProjectInfo.WORKSPACE + projectInfo.getBuggyName());
		}
	}
}
