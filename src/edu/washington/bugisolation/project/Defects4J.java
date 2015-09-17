package edu.washington.bugisolation.project;

import edu.washington.bugisolation.util.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * The Defects4j class is used to perform all Defects4J operations required for
 * the bug minimization process.
 */
public class Defects4J implements Project {

    // this ProjectInfo stores all the information about the project
    private ProjectInfo projectInfo;

    /**
     * Creates a new Defects4J.
     *
     * @param proj
     *            a ProjectInfo containing the information of the current
     *            project
     */
    public Defects4J(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    /**
     * Helper method that runs any Defects4J command.
     *
     * @param command
     *            the Defects4J command that is to be executed, contains a space
     *            between each argument
     * @param directory
     *            the directory in which the command will be executed
     * @return an int, denoting the exit value of the process
     */
    public int d4jOperation(String command, String directory) {
        return Utils.commandLine(ProjectInfo.D4J_LOCATION + " " + command,
                directory);
    }

    /**
     * Helper method that runs any git command.
     *
     * @param command
     *            the git command that is to be executed, contains a space
     *            between each argument
     * @param directory
     *            the directory in which the command will be executed
     * @return an int, denoting the exti value of the process
     */
    private int gitOperation(String command, String directory) {
        System.out.println(command);
        return Utils.commandLine("git " + command, directory);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#checkout()
     */
    @Override
    public void checkout() {
        checkoutFixed();
        checkoutBuggy();
        setExcludedTests();

        gitOperation("add -A", projectInfo.getFixedDirectory());
        gitOperation("rm -f " + projectInfo.getFixedDirectory()
                + "target/classes/" + projectInfo.getRelevantFilePath("*"),
                projectInfo.getFixedDirectory());
        gitOperation("commit -mjava", projectInfo.getFixedDirectory());
        gitOperation("tag D4J_" + projectInfo.getFixedName() + "_reltests",
                projectInfo.getFixedDirectory());

        gitOperation("add -A", projectInfo.getBuggyDirectory());
        gitOperation("rm -f " + projectInfo.getBuggyDirectory()
                + "target/classes/" + projectInfo.getRelevantFilePath("*"),
                projectInfo.getBuggyDirectory());
        gitOperation("commit -mjava", projectInfo.getBuggyDirectory());
        gitOperation("tag D4J_" + projectInfo.getBuggyName() + "_reltests",
                projectInfo.getBuggyDirectory());
    }
    
    /**
     * Checks out the fixed version of the project.
     */
    private void checkoutFixed() {
        System.out.println("Checking out fixed version of the project");
        d4jOperation(
                "checkout -p " + projectInfo.getProjectName() + " -v "
                        + projectInfo.getProjectVersion() + "f" + " -w "
                        + projectInfo.getFixedName(), ProjectInfo.WORKSPACE);
        compileFixed();
    }
    
    /**
     * Checks out the buggy version of the project.
     */
    private void checkoutBuggy() {
        System.out.println("Checking out buggy version of the project");
        d4jOperation(
                "checkout -p " + projectInfo.getProjectName() + " -v "
                + projectInfo.getProjectVersion() + "f" + " -w "
                + projectInfo.getBuggyName(), ProjectInfo.WORKSPACE);
        gitOperation(
                "checkout " + "D4J_" + projectInfo.getFullProjectName()
                + "_PRE_FIX_REVISION" + " -- " + projectInfo.getSrcDirectory(),
                projectInfo.getBuggyDirectory());
        compileBuggy();
    }

    private void setExcludedTests() {
        System.out.println("Setting excluded tests");

        d4jOperation("export -p tests.all -o tests.all",
                projectInfo.getFixedDirectory());
        d4jOperation("export -p tests.relevant -o tests.relevant",
                projectInfo.getFixedDirectory());

        List<String> testNames = Utils.fileToLines(projectInfo
                .getFixedDirectory() + "tests.all");
        List<String> relevantTests = Utils.fileToLines(projectInfo
                .getFixedDirectory() + "tests.relevant");

        testNames.removeAll(relevantTests);

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(projectInfo.getFixedDirectory()
                    + "defects4j.build.properties"));
            String excludedTests = props.getProperty("d4j.tests.exclude");
            String allExcludedTests = "";
            if (excludedTests != null) {
                allExcludedTests += excludedTests;
                for (String name : testNames) {
                    allExcludedTests += ',' + name.replace('.', '/') + ".java";
                }
            } else {
                if (!testNames.isEmpty()) {
                    allExcludedTests = testNames.get(0).replace('.', '/')
                            + ".java";
                    for (int i = 1; i < testNames.size(); i++) {
                        allExcludedTests += ','
                                + testNames.get(i).replace('.', '/') + ".java";
                    }
                }
            }
            props.put("d4j.tests.exclude", allExcludedTests);
            OutputStream fixedD4JProps = new FileOutputStream(
                    projectInfo.getFixedDirectory()
                    + "defects4j.build.properties");
            OutputStream buggyD4JProps = new FileOutputStream(
                    projectInfo.getBuggyDirectory()
                    + "defects4j.build.properties");

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

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#getFailingTests()
     */
    @Override
    public List<String> getFailingTests() {
        System.out.println("Getting failing tests");

        return Utils.getTests(projectInfo.getRelevantDirectory()
                + ".failing_tests");
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#generatePatch()
     */
    @Override
    public void generatePatch() {
        System.out.println("Generating initial patch");
        if (projectInfo.isFixedToBuggy()) {
            Utils.commandLine(
                    "git diff " + projectInfo.getFixedDirectory()
                    + projectInfo.getSrcDirectory()
                    + projectInfo.getRelevantFilePath(".java") + " "
                    + projectInfo.getBuggyDirectory()
                    + projectInfo.getSrcDirectory()
                    + projectInfo.getRelevantFilePath(".java"),
                    ProjectInfo.WORKSPACE,
                    ProjectInfo.WORKSPACE + projectInfo.getFullProjectName()
                    + ".diff");
        } else {
            Utils.commandLine(
                    "git diff " + projectInfo.getBuggyDirectory()
                    + projectInfo.getSrcDirectory()
                    + projectInfo.getRelevantFilePath(".java") + " "
                    + projectInfo.getFixedDirectory()
                    + projectInfo.getSrcDirectory()
                    + projectInfo.getRelevantFilePath(".java"),
                    ProjectInfo.WORKSPACE,
                    ProjectInfo.WORKSPACE + projectInfo.getFullProjectName()
                    + ".diff");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#applyPatch()
     */
    @Override
    public int applyPatch() {
        System.out.println("Applying the current patch");

        return gitOperation(
                "apply " + ProjectInfo.WORKSPACE
                + projectInfo.getFullProjectName() + ".diff",
                projectInfo.getRelevantDirectory());
    }

    private int compileFixed() {
        return d4jOperation("compile", projectInfo.getFixedDirectory());
    }

    private int compileBuggy() {
        return d4jOperation("compile", projectInfo.getBuggyDirectory());
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#compile()
     */
    @Override
    public int compileModified() {
        return d4jOperation("compile", projectInfo.getRelevantDirectory());
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#test()
     */
    @Override
    public int test() {
        return d4jOperation("test", projectInfo.getRelevantDirectory());
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.Project#reset()
     */
    @Override
    public void reset() {
        gitOperation("reset --hard " + "D4J_" + projectInfo.getRelevantName()
                + "_reltests", projectInfo.getRelevantDirectory());
        gitOperation("clean -xfd", projectInfo.getRelevantDirectory());
    }
}