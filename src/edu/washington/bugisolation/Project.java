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
    public abstract void checkout();

    /**
     * Compiles the current project.
     *
     * @return the exit value of the compile process
     */
    public abstract int compileModified();

    /**
     * Runs the test suite on the current project.
     *
     * @return the exit value of the test process
     */
    public abstract int test();

    /**
     * Gets the failing test for the current project.
     *
     * @return the failing tests for the current project
     */
    public abstract List<String> getFailingTests();

    /**
     * Generates the initial patch for the project.
     */
    public abstract void generatePatch();

    /**
     * Applies the generated patch to either the buggy or fixed version,
     * depending on relevance.
     *
     * @return the exit value of the apply process
     */
    public abstract int applyPatch();

    /**
     * Resets the current project to its checkout state.
     */
    public abstract void reset();

}