package edu.washington.bugisolation;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.washington.bugisolation.util.Operations;

/**
 * The ProjectInfo class stores all the information about the current project.
 * 
 * @author dpang
 * 
 */
public class Defects4JPI implements ProjectInfo {
	
	/* basic project information */
	private String projectName;
	private String projectVersion;
	private boolean fixedToBuggy;
	
	/* the lines of the fixed modified file */
	private List<String> fixedFile;
	
	/* the lines of the buggy modified file */
	private List<String> buggyFile;
	
	/**
	 * Creates a new Defects4jPI.
	 * 
	 * @param projectName		a String, the name of the project
	 * @param projectVersion	a String, the version number of the project
	 */
	public Defects4JPI(String projectName, String projectVersion, boolean fixedToBuggy) {
		
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.fixedToBuggy = fixedToBuggy;
		
		fixedFile = new LinkedList<String>();
		buggyFile = new LinkedList<String>();
	}
	
	/**
	 * Sets the directory containing the source code of the project.
	 * 
	 * @return	an int, denoting the exit value of the process
	 */
	private String getDirectory(String property) {
		String propFile = WORKSPACE + getFullProjectName() +  "_Directory_Layout";
		Operations.commandLine (
				"perl " + WORKSPACE + "defects4j/framework/util/get_directories.pl"
				+ " -p " + projectName + " -v " + projectVersion
				, WORKSPACE + "defects4j/framework/util/"
				, propFile);
		
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(propFile));
			return props.getProperty(property) + '/';
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getProjectName()
	 */
	@Override
	public String getProjectName() {
		return projectName;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getProjectVersion()
	 */
	@Override
	public String getProjectVersion() {
		return projectVersion;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#isBuggyToFixed()
	 */
	@Override
	public boolean isFixedToBuggy() {
		return fixedToBuggy;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getFullProjectName()
	 */
	@Override
	public String getFullProjectName() {
		return projectName + '_' + projectVersion;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getFixedName()
	 */
	public String getFixedName() {
		return getFullProjectName() + "_fixed";
	}
	
	public String getFixedDirectory() {
		return ProjectInfo.WORKSPACE + getFixedName() + '/';
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getBuggyName()
	 */
	@Override
	public String getBuggyName() {
		return getFullProjectName() + "_buggy";
	}
	
	public String getBuggyDirectory() {
		return ProjectInfo.WORKSPACE + getBuggyName() + '/';
	}
	
	public String getModifiedDirectory() {
	    if (fixedToBuggy) {
	        return getFixedDirectory();
	    } else {
	        return getBuggyDirectory();
	    }
	}
	
	public String getModifiedName() {
	    if (fixedToBuggy) {
	        return getFixedName();
	    } else {
	        return getBuggyName();
	    }
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getDiffPathA()
	 */
	@Override
	public String getDiffPathA() {
		return "a/" + getSrcDirectory() + getModifiedClassPath(".java");
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getDiffPathB()
	 */
	public String getDiffPathB() {
		return "b/" + getSrcDirectory() + getModifiedClassPath(".java");
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getTriggerTests()
	 */
	@Override
	public List<String> getTriggerTests() {
		return Operations.getTests(ProjectInfo.D4J_LOCATION + "framework/projects/"
					+ getProjectName() + "/trigger_tests/" + getProjectVersion());
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getSrcDirectory()
	 */
	@Override
	public String getSrcDirectory() {
		return getDirectory("src");
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getTestDirectory()
	 */
	@Override
	public String getTestDirectory() {
		return getDirectory("test");
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getFixedFile()
	 */
	@Override
	public List<String> getFixedFile() {
		return fixedFile;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getBuggyFile()
	 */
	@Override
	public List<String> getBuggyFile() {
		return buggyFile;
	}
	
	public List<String> getModifiedFile() {
	    if (fixedToBuggy) {
	        return fixedFile;
	    } else {
	        return buggyFile;
	    }
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#setFixedFile(java.util.List)
	 */
	@Override
	public void setFixedFile(List<String> file) {
		fixedFile = file;
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#setBuggyFile(java.util.List)
	 */
	@Override
	public void setBuggyFile(List<String> file) {
		buggyFile = file;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getModifiedClass()
	 */
	@Override
	public String getModifiedClass() {
	    return Operations.fileToLines (
	            D4J_LOCATION + "framework/projects/" + projectName + "/modified_classes/"
	            + projectVersion + ".src").get(0);
	}
	
	/* (non-Javadoc)
	 * @see edu.washington.bugisolation.ProjectInfo#getModifiedClass()
	 */
	@Override
	public String getModifiedClassPath(String extension) {
		return getModifiedClass().replace('.', '/') + extension;
	}	
}