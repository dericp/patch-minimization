package edu.washington.bugisolation;

import java.util.LinkedList;
import java.util.List;

import edu.washington.bugisolation.util.Operations;

public class TrianglePI implements ProjectInfo {
	
	/* basic project information */
	private String projectName;
	private String projectVersion;
	private boolean fixedToBuggy;
	private String fullProjectName;
	private String fixedName;
	private String buggyName;
	
	/* the directory of the source code */
	private String srcDirectory;
	
	/* the path of the modified class in src */
	private String modifiedClass;
	
	/* the lines of the fixed modified file */
	private List<String> fixedFile;
	
	/* the lines of the buggy modified file */
	private List<String> buggyFile;
	
	/* the file paths to be written in the diffs */
	private String diffPathA;
	private String diffPathB;
	
	public TrianglePI(String projectName, String projectVersion, boolean buggyToFixed) {
		
		this.projectName = projectName;
		this.fixedName = projectName;
		this.buggyName = projectName;
		
		this.projectVersion = projectVersion;
		this.fixedName += "_" + projectVersion + "_fixed";
		this.buggyName += "_" + projectVersion + "_buggy";
		
		this.fixedToBuggy = buggyToFixed;
		
		fullProjectName = projectName + '_' + projectVersion;
    			
		fixedFile = new LinkedList<String>();
		buggyFile = new LinkedList<String>();
		
		setSrcDirectory();
		
		/* obtains the modified class path in src in the correct format */
		modifiedClass = srcDirectory + "Triangle";
		
		/* sets the filepaths to be written in the diff */
		diffPathA = "a/" + modifiedClass + ".java";
		diffPathB = "b/" + modifiedClass + ".java";
	}
	
	/**
	 * Sets the directory containing the source code of the project.
	 * 
	 * @return	an int, denoting the exit value of the process
	 */
	private void setSrcDirectory() {
		srcDirectory = "src/triangle/";
	}
	
	public String getProjectName() {
		return projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public boolean isFixedToBuggy() {
		return fixedToBuggy;
	}
	
	public String getFullProjectName() {
		return fullProjectName;
	}

	public String getFixedName() {
		return fixedName;
	}
	
	public String getFixedDirectory() {
		return ProjectInfo.WORKSPACE + fixedName + '/';
	}

	public String getBuggyName() {
		return buggyName;
	}
	
	public String getBuggyDirectory() {
	    return ProjectInfo.WORKSPACE + buggyName + '/';
	}

	public List<String> getTriggerTests() {
		return Operations.getTests (
				"/Users/dpang/Documents/UW/Research/TriangleFiles/" 
				+ getBuggyName() + "/trigger_tests");
	}
	
	public String getSrcDirectory() {
		return srcDirectory;
	}
	
	public String getTestDirectory() {
		return null;
	}
	
	public String getModifiedClass() {
		return null;
	}
	
	public String getModifiedClassPath(String extension) {
		return modifiedClass + extension;
	}

	public List<String> getFixedFile() {
		return fixedFile;
	}
	
	public List<String> getBuggyFile() {
		return buggyFile;
	}
	
	public String getDiffPathA() {
		return diffPathA;
	}

	public String getDiffPathB() {
		return diffPathB;
	}

	public void setFixedFile(List<String> file) {
		fixedFile = file;
	}

	public void setBuggyFile(List<String> file) {
		buggyFile = file;
	}

    @Override
    public String getModifiedDirectory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getModifiedName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getModifiedFile() {
        if (fixedToBuggy) {
            return getFixedFile();
        } else {
            return getBuggyFile();
        }
    }
}
