package edu.washington.bugisolation;

import java.util.List;

/**
 * A project interface that defines the functions needed to work with the bug
 * minimizer.
 */
public interface Project {

    /**
     * Checks out the two versions of the project.
     */
    public void checkout();

    /**
     * Compiles the current project.
     *
     * @return the exit value of the compile process
     */
    public int compileModified();

    /**
     * Runs the test suite on the current project.
     *
     * @return the exit value of the test process
     */
    public int test();

    /**
     * Gets the failing test for the current project.
     *
     * @return the failing tests for the current project
     */
    public List<String> getFailingTests();

    /**
     * Generates the initial patch for the project.
     */
    public void generatePatch();

    /**
     * Applies the generated patch to either the buggy or fixed version,
     * depending on relevance.
     *
     * @return the exit value of the apply process
     */
    public int applyPatch();

    /**
     * Resets the current project to its checkout state.
     */
    public void reset();

}