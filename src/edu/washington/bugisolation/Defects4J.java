package edu.washington.bugisolation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import difflib.*;
import edu.washington.bugisolation.util.Operations;

/**
 * The Defects4j class is used to perform all Defects4J operations required for the
 * bug minimization process.
 * 
 * @author dpang
 * 
 */
public class Defects4J implements Project {
	
	/* A ProjectInfo that stores all the information of the project */
	private ProjectInfo projectInfo;
	
	
	/**
	 * Creates a new Defects4J.
	 * 
	 * @param proj	a ProjectInfo containing the information of the current project
	 */
	public Defects4J(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}
	
	/**
	 * Helper method that runs any Defects4J command.
	 * 
	 * @param command		a String, the Defects4J command that is to be executed,
	 * 						contains a space between each argument
	 * @param directory		a String, the directory in which the command will be executed
	 * @return				an int, denoting the exit value of the process
	 */
	public int d4jOperation(String command, String directory) {
	    return Operations.commandLine (
	            "sh " + ProjectInfo.D4J_LOCATION + "defects4j.sh " + command
	            , directory);
	}
	
	/**
	 * Helper method that runs any git command.
	 * 
	 * @param command		a String, the git command that is to be executed,
	 * 						contains a space between each argument 
	 * @param directory		a String, the directory in which the command will be executed
	 * @return				an int, denoting the exti value of the process
	 */
	private int gitOperation(String command, String directory) {
	    System.out.println(command);
		return Operations.commandLine("git " + command, directory);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#checkout()
	 */
	@Override
	public void checkout() {
		checkoutFixed();
		checkoutBuggy();
		setExcludedTests();
		
		gitOperation("add -A", projectInfo.getFixedDirectory());
		gitOperation (
		        "rm -f " + projectInfo.getFixedDirectory() + "target/classes/"
		        + projectInfo.getModifiedPath("*")
		        , projectInfo.getFixedDirectory());
		gitOperation("commit -mjava", projectInfo.getFixedDirectory());
		gitOperation (
		        "tag D4J_" + projectInfo.getFixedName()  + "_reltests"
		        , projectInfo.getFixedDirectory());
		
		gitOperation("add -A", projectInfo.getBuggyDirectory());
		gitOperation (
		        "rm -f " + projectInfo.getBuggyDirectory()
		        + "target/classes/" + projectInfo.getModifiedPath("*")
		        , projectInfo.getBuggyDirectory());
		gitOperation("commit -mjava", projectInfo.getBuggyDirectory());
		gitOperation (
		        "tag D4J_" + projectInfo.getBuggyName() + "_reltests"
		        , projectInfo.getBuggyDirectory());
	}
	
	private void checkoutFixed() {
		System.out.println("Checking out fixed version of the project");
		d4jOperation (
				"checkout -p " + projectInfo.getProjectName()
				+ " -v " + projectInfo.getProjectVersion()
				+ "f" + " -w " + projectInfo.getFixedName()
				, ProjectInfo.WORKSPACE);
		compileFixed();
	}
	
	private void checkoutBuggy() {
		System.out.println("Checking out buggy version of the project");
		d4jOperation (
				"checkout -p " + projectInfo.getProjectName()
				+ " -v " + projectInfo.getProjectVersion()
				+ "b" + " -w " + projectInfo.getBuggyName()
				, ProjectInfo.WORKSPACE);
		compileBuggy();
	}
	
	private void setExcludedTests() {
	    System.out.println("Setting excluded tests");
	    
		d4jOperation("export -p tests.all -o tests.all", projectInfo.getFixedDirectory());
		d4jOperation("export -p tests.relevant -o tests.relevant", projectInfo.getFixedDirectory());
		
		List<String> testNames = Operations.fileToLines(projectInfo.getFixedDirectory() + "tests.all");
		List<String> relevantTests = Operations.fileToLines(projectInfo.getFixedDirectory() + "tests.relevant");
		
		testNames.removeAll(relevantTests);
		
		Properties props = new Properties();
		try {
		    props.load(new FileInputStream(projectInfo.getFixedDirectory() + "defects4j.build.properties"));
		    String excludedTests = props.getProperty("d4j.tests.exclude");
		    String allExcludedTests = "";
		    if (excludedTests != null) {
		        allExcludedTests += excludedTests;
		        for (String name : testNames) {
		            allExcludedTests += ',' + name.replace('.', '/') + ".java";
		        }
		    } else {
		        if (!testNames.isEmpty()) {
		            allExcludedTests = testNames.get(0).replace('.', '/') + ".java";
		            for (int i = 1; i < testNames.size(); i++) {
		                allExcludedTests += ',' + testNames.get(i).replace('.', '/') + ".java";
		            }
		        }
		    }
		    props.put("d4j.tests.exclude", allExcludedTests);
		    OutputStream fixedD4JProps = new FileOutputStream(projectInfo.getFixedDirectory() + "defects4j.build.properties");
		    OutputStream buggyD4JProps = new FileOutputStream(projectInfo.getBuggyDirectory() + "defects4j.build.properties");
		    
		    props.store(fixedD4JProps, "Excludes irrelevant tests");
		    props.store(buggyD4JProps, "excludes irrelevant tests");
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#getFailingTests()
	 */
	@Override
	public List<String> getFailingTests() {
		System.out.println("Getting failing tests");
		
		return Operations.getTests(projectInfo.getModifiedDirectory() + ".failing_tests");
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#getPatch()
	 */
	@Override
	public Patch<String> generatePatch() {
		System.out.println("Generating patch between the versions of the project");
		
		List<String> fixedFile = Operations.fileToLines (
				projectInfo.getFixedDirectory()
				+ projectInfo.getSrcDirectory() + projectInfo.getModifiedPath(".java"));
		List<String> buggyFile = Operations.fileToLines (
				projectInfo.getBuggyDirectory()
				+ projectInfo.getSrcDirectory() + projectInfo.getModifiedPath(".java"));
		projectInfo.setFixedFile(fixedFile);
		projectInfo.setBuggyFile(buggyFile);
		
		if (projectInfo.isFixedToBuggy()) {
			return DiffUtils.diff(fixedFile, buggyFile);
		} else {
			return DiffUtils.diff(buggyFile, fixedFile);
		}
	}
	
	public void generateDiff() {
	    System.out.println("Generating initial patch");
	    if (projectInfo.isFixedToBuggy()) {
	           Operations.commandLine (
	                    "git diff " + projectInfo.getFixedDirectory()
	                    + projectInfo.getSrcDirectory()
	                    + projectInfo.getModifiedPath(".java")
	                    + " "
	                    + projectInfo.getBuggyDirectory()
	                    + projectInfo.getSrcDirectory()
	                    + projectInfo.getModifiedPath(".java")
	                    , ProjectInfo.WORKSPACE
	                    , ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff");
	    } else {
    	    Operations.commandLine (
    	            "git diff " + projectInfo.getBuggyDirectory()
    	            + projectInfo.getSrcDirectory()
    	            + projectInfo.getModifiedPath(".java")
    	            + " "
    	            + projectInfo.getFixedDirectory()
    	            + projectInfo.getSrcDirectory()
    	            + projectInfo.getModifiedPath(".java")
    	            , ProjectInfo.WORKSPACE
    	            , ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff");
	    }
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#applyPatch()
	 */
	@Override
	public int applyPatch() {
		System.out.println("Applying the current patch");
		
		return gitOperation (
                "apply " + ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff"
                , projectInfo.getModifiedDirectory());
	}
	
	private int compileFixed() {
		return d4jOperation("compile", projectInfo.getFixedDirectory());
	}
	
	private int compileBuggy() {
		return d4jOperation("compile", projectInfo.getBuggyDirectory());
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#compile()
	 */
	@Override
	public int compileModified() {
	    return d4jOperation("compile", projectInfo.getModifiedDirectory());
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#test()
	 */
	@Override
	public int test() {
	    return d4jOperation("test", projectInfo.getModifiedDirectory());
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#reset()
	 */
	@Override
	public void reset() {
        gitOperation (
                "reset --hard " + "D4J_" + projectInfo.getModifiedName() + "_reltests"
                , projectInfo.getModifiedDirectory());
        gitOperation("clean -xfd", projectInfo.getModifiedDirectory());
	}
}