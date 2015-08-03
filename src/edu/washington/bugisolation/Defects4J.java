package edu.washington.bugisolation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
		//setExcludedTests();
		copyExcludedTests();
		
		gitOperation("add -A", projectInfo.getFixedDirectory());
		gitOperation (
		        "rm -f " + projectInfo.getFixedDirectory() + "target/classes/"
		        + projectInfo.getModifiedClassPath("*")
		        , projectInfo.getFixedDirectory());
		gitOperation("commit -mjava", projectInfo.getFixedDirectory());
		gitOperation (
		        "tag D4J_" + projectInfo.getFixedName()  + "_reltests"
		        , projectInfo.getFixedDirectory());
		
		gitOperation("add -A", projectInfo.getBuggyDirectory());
		gitOperation (
		        "rm -f " + projectInfo.getBuggyDirectory()
		        + "target/classes/" + projectInfo.getModifiedClassPath("*")
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
	
	/* temporary method */
	private void copyExcludedTests() {
	    List<String> excludedTests = Operations.fileToLines(ProjectInfo.WORKSPACE + "defects4j.build.properties");
	       Operations.linesToFile (
	                excludedTests, projectInfo.getFixedDirectory() + "defects4j.build.properties");
	        Operations.linesToFile (
	                excludedTests, projectInfo.getBuggyDirectory() + "defects4j.build.properties");
	}
	
	private void setExcludedTests() {
		d4jOperation("export -p all.tests -o all.tests", projectInfo.getFixedDirectory());
		
		List<String> testNames = Operations.fileToLines(projectInfo.getFixedDirectory() + "all.tests");
		List<String> testClassPathes =  new LinkedList<String>();
		
		for (int i = 0; i < testNames.size(); ++i) {
			/* add each test name but truncate to just the file-path of the test */
			testClassPathes.add(testNames.get(i).replace(projectInfo.getFixedDirectory() + projectInfo.getTestDirectory(), ""));
		}
		
		/* now remove each irrelevant test */
		for (Iterator<String> iter = testClassPathes.iterator(); iter.hasNext();) {
			String test = iter.next();
			System.out.println("monitoring " + test);
			
			String temp = test.replace('/', '.');
			temp = temp.substring(0, test.indexOf(".java"));
			System.out.println("input: " + temp);
			
			int exitVal;
			exitVal = d4jOperation("monitor.test -t " + temp, projectInfo.getFixedDirectory());
			List<String> loadedClasses;
			loadedClasses = Operations.fileToLines(projectInfo.getFixedDirectory() + "/loaded_classes.src");
			if (exitVal == 0 && loadedClasses.contains(projectInfo.getModifiedClass())) {
				iter.remove();
				System.out.println("This test was relevant");
			}
		}
		
		System.out.println("finished monitoring tests");
		
		List<String> d4jBuildProps = Operations.fileToLines(projectInfo.getFixedDirectory() + "defects4j.build.properties");
		String excludedTests = "";
		
		for (Iterator<String> iter = d4jBuildProps.iterator(); iter.hasNext();) {
		    String line = iter.next();
		    if (line.contains("d4j.tests.exclude=")) {
		        excludedTests = line + ", ";
		        iter.remove();
		        break;
		    }
		}
		
		if (excludedTests.isEmpty()) {
		    excludedTests = "d4j.tests.exclude=";
		}
		
		for (String testPath : testClassPathes) {
		    excludedTests += (testPath + ", ");
		}
		
		d4jBuildProps.add(excludedTests);
		/* for (int i = 0; i < d4jBuildProps.size(); ++i) {
			String temp = d4jBuildProps.get(i);
			if (temp.contains("d4j.tests.exclude=")) {
				for (String testPath : testClassPathes) {
					temp += (", " + testPath);
				}
				d4jBuildProps.remove(i);
				d4jBuildProps.add(i, temp);
				break;
			}
		} */
		
		Operations.linesToFile (
		        d4jBuildProps, projectInfo.getFixedDirectory() + "defects4j.build.properties");
		Operations.linesToFile (
		        d4jBuildProps, projectInfo.getBuggyDirectory() + "defects4j.build.properties");
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#getFailingTests()
	 */
	@Override
	public List<String> getFailingTests() {
		System.out.println("Getting failing tests");
		/*
		if (projectInfo.isFixedToBuggy()) {
			return Operations.getTests(projectInfo.getFixedDirectory() + ".failing_tests");
		} else {
			return Operations.getTests(projectInfo.getBuggyDirectory() + ".failing_tests");
		}
		*/
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
				+ projectInfo.getSrcDirectory() + projectInfo.getModifiedClassPath(".java"));
		List<String> buggyFile = Operations.fileToLines (
				projectInfo.getBuggyDirectory()
				+ projectInfo.getSrcDirectory() + projectInfo.getModifiedClassPath(".java"));
		projectInfo.setFixedFile(fixedFile);
		projectInfo.setBuggyFile(buggyFile);
		
		if (projectInfo.isFixedToBuggy()) {
			return DiffUtils.diff(fixedFile, buggyFile);
		} else {
			return DiffUtils.diff(buggyFile, fixedFile);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#applyPatch()
	 */
	@Override
	public int applyPatch() {
		System.out.println("Applying the current patch");
		
		/*
		if (projectInfo.isFixedToBuggy()) {
			return gitOperation (
					"apply " + ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff"
					, projectInfo.getFixedDirectory());
		} else {
			return gitOperation (
					"apply " + ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff"
					, projectInfo.getBuggyDirectory());
		} */
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
	    /*
		if (projectInfo.isFixedToBuggy()) {
			return compileFixed();
		} else {
			return compileBuggy();
		} */
	    return d4jOperation("compile", projectInfo.getModifiedDirectory());
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#test()
	 */
	@Override
	public int test() {
	    /*
		if (projectInfo.isFixedToBuggy()) {
			return d4jOperation("test", projectInfo.getFixedDirectory());
		} else {
			return d4jOperation("test", projectInfo.getBuggyDirectory());
		} */
	    return d4jOperation("test", projectInfo.getModifiedDirectory());
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.Project#reset()
	 */
	@Override
	public void reset() {
	    /*
		if (projectInfo.isFixedToBuggy()) {
			gitOperation (
					"reset --hard " + "D4J_" + projectInfo.getFixedName() + "_reltests"
					, projectInfo.getFixedDirectory());
			gitOperation("clean -xfd", projectInfo.getFixedDirectory());
		} else {
			gitOperation (
					"reset --hard " + "D4J_" + projectInfo.getBuggyName() + "_reltests"
					, projectInfo.getBuggyDirectory());
			gitOperation("clean -xfd", projectInfo.getBuggyDirectory());
		} */
		
        gitOperation (
                "reset --hard " + "D4J_" + projectInfo.getModifiedName() + "_reltests"
                , projectInfo.getModifiedDirectory());
        gitOperation("clean -xfd", projectInfo.getModifiedDirectory());
		
		
	}
}