package edu.washington.bugisolation;

import java.util.List;

public interface ProjectInfo {

	public static final String D4J_LOCATION = "/Users/dpang/workspace/defects4j-github/";
	public static final String WORKSPACE = "/Users/dpang/workspace/";

	/**
	 * Gets the name of the current project.
	 * 
	 * @return	a String, the name of the current project
	 */
	public abstract String getProjectName();

	/**
	 * Gets the version of the current project.
	 * 
	 * @return	a String, the version number of the current project
	 */
	public abstract String getProjectVersion();
	
	/**
	 * Returns whether or not the minimization process is from buggy to fixed versions.
	 * 
	 * @return	a boolean, whether the minimization process is from buggy to fixed
	 */
	public abstract boolean isFixedToBuggy();
	
	/**
	 * Gets the full name of the current project.
	 * 
	 * @return	a String, contains the project name and version separated by an underscore
	 */
	public abstract String getFullProjectName();

	/**
	 * Gets the full fixed name of the current project.
	 * 
	 * @return	a String, the full project name followed by _fixed
	 */
	public abstract String getFixedName();
	
	public abstract String getFixedDirectory();

	/**
	 * Gets the full buggy name of the current project.
	 * 
	 * @return	a String, the full project name followed by _buggy
	 */
	public abstract String getBuggyName();
	
	public abstract String getBuggyDirectory();
	
	public abstract String getModifiedDirectory();
	
	public abstract String getModifiedName();

	/**
	 * Gets the A diff file-path.
	 * 
	 * @return	a String, the A diff file-path
	 */
	public abstract String getDiffPathA();

	/**
	 * Gets the B diff file-path.
	 * 
	 * @return	a String, the B diff file-path
	 */
	public abstract String getDiffPathB();

	/**
	 * Sets the trigger tests for the current project.
	 * 
	 * @param file	a File, containing information about the trigger tests
	 */
	//public abstract void setTriggerTests(List<String> file);

	/**
	 * Gets the trigger tests for the current project.
	 * 
	 * @return	a List of Strings, the trigger tests for the current project
	 */
	public abstract List<String> getTriggerTests();
	
	/**
	 * Gets the source directory for the current project.
	 * 
	 * @return	a String, the source directory of the project
	 */
	public abstract String getSrcDirectory();
	
	/**
	 * Gets the directory containing the test files of the current project.
	 * 
	 * @return	a String, the directory containing the test files
	 */
	public abstract String getTestDirectory();

	/**
	 * Gets the fixed version of the modified file for the current project.
	 * 
	 * @return	a List of Strings that represent the modified file
	 */
	public abstract List<String> getFixedFile();

	/**
	 * Gets the buggy version of the modified file for the current project.
	 * 
	 * @return	a List of Strings the represent the modified file
	 */
	public abstract List<String> getBuggyFile();
	
	public abstract List<String> getModifiedFile();

	/**
	 * Gets the modified class name.
	 * 
	 * @return	a String, the name of the modified class
	 */
	public abstract String getModifiedFullyQualifiedName();
	
	/**
	 * Gets the file-path in the src directory of the modified class
	 * 
	 * @param extension 	a String, denoting the extension of the file
	 * @return				a String that is the file-path of the modified class
	 */
	public abstract String getModifiedPath(String extension);

	/**
	 * Sets the fixed file for the current project.
	 * 
	 * @param file	a File that is the fixed modified file
	 */
	public abstract void setFixedFile(List<String> file);

	/**
	 * Sets the buggy file for the current project.
	 * 
	 * @param file	a File that is the buggy modified file
	 */
	public abstract void setBuggyFile(List<String> file);

}