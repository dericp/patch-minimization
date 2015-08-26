package edu.washington.bugisolation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * The ProjectInfo class stores all the information about the current project.
 *
 * @author dpang
 *
 */
public class Defects4JPI implements ProjectInfo {

    // basic project information
    private String projectName;
    private String projectVersion;
    private boolean fixedToBuggy;

    // the lines of the fixed modified file
    private List<String> fixedFile;
    // the lines of the buggy modified file
    private List<String> buggyFile;

    /**
     * Creates a new Defects4jPI.
     *
     * @param projectName
     *            the name of the project
     * @param projectVersion
     *            the version number of the project
     * @param fixedTobuggy
     *            whether or not the minimization is on the fixed or buggy
     *            version of the project
     */
    public Defects4JPI(String projectName, String projectVersion,
            boolean fixedToBuggy) {

        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.fixedToBuggy = fixedToBuggy;

        fixedFile = new LinkedList<String>();
        buggyFile = new LinkedList<String>();
    }

    /**
     * Gets a property from the defects4j property file included in checked out
     * project.
     *
     * @param property
     *            the string that denotes the property to be returned
     * @return the value of the property
     */
    private String getD4JProperty(String property) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(getFixedDirectory()
                    + "defects4j.build.properties"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props.getProperty(property);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getProjectName()
     */
    @Override
    public String getProjectName() {
        return projectName;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getProjectVersion()
     */
    @Override
    public String getProjectVersion() {
        return projectVersion;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#isBuggyToFixed()
     */
    @Override
    public boolean isFixedToBuggy() {
        return fixedToBuggy;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getFullProjectName()
     */
    @Override
    public String getFullProjectName() {
        return projectName + '_' + projectVersion;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getFixedName()
     */
    @Override
    public String getFixedName() {
        return getFullProjectName() + "_fixed";
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getFixedDirectory()
     */
    @Override
    public String getFixedDirectory() {
        return ProjectInfo.WORKSPACE + getFixedName() + '/';
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getBuggyName()
     */
    @Override
    public String getBuggyName() {
        return getFullProjectName() + "_buggy";
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getBuggyDirectory()
     */
    @Override
    public String getBuggyDirectory() {
        return ProjectInfo.WORKSPACE + getBuggyName() + '/';
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getModifiedDirectory()
     */
    @Override
    public String getRelevantDirectory() {
        if (fixedToBuggy) {
            return getFixedDirectory();
        } else {
            return getBuggyDirectory();
        }
    }

    @Override
    public String getRelevantName() {
        if (fixedToBuggy) {
            return getFixedName();
        } else {
            return getBuggyName();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getDiffPathA()
     */
    @Override
    public String getDiffPathA() {
        return "a/" + getSrcDirectory() + getRelevantFilePath(".java");
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getDiffPathB()
     */
    @Override
    public String getDiffPathB() {
        return "b/" + getSrcDirectory() + getRelevantFilePath(".java");
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getTriggerTests()
     */
    @Override
    public List<String> getTriggerTests() {
        String triggerTests = getD4JProperty("d4j.tests.trigger");
        return Arrays.asList(triggerTests.split(","));
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getSrcDirectory()
     */
    @Override
    public String getSrcDirectory() {
        return getD4JProperty("d4j.dir.src.classes") + '/';
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getTestDirectory()
     */
    @Override
    public String getTestDirectory() {
        return getD4JProperty("d4j.dir.src.tests") + '/';
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getFixedFile()
     */
    @Override
    public List<String> getFixedFile() {
        return fixedFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getBuggyFile()
     */
    @Override
    public List<String> getBuggyFile() {
        return buggyFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getModifiedFile()
     */
    @Override
    public List<String> getRelevantFile() {
        if (fixedToBuggy) {
            return fixedFile;
        } else {
            return buggyFile;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#setFixedFile(java.util.List)
     */
    @Override
    public void setFixedFile(List<String> file) {
        fixedFile = file;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#setBuggyFile(java.util.List)
     */
    @Override
    public void setBuggyFile(List<String> file) {
        buggyFile = file;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getModifiedClass()
     */
    @Override
    public String getRelevantFullyQualifiedName() {
        return getD4JProperty("d4j.classes.modified");
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.ProjectInfo#getModifiedClass()
     */
    @Override
    public String getRelevantFilePath(String extension) {
        return getRelevantFullyQualifiedName().replace('.', '/') + extension;
    }
}