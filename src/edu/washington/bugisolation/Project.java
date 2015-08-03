package edu.washington.bugisolation;

import java.util.List;

import difflib.Patch;

public interface Project {

    /**
     * Checks out the two versions of the project.
     */
	public abstract void checkout();
	
	/**
	 * Compiles the current project.
	 * 
	 * @return		an int, denoting the exit value of the compile process
	 */
	public abstract int compileModified();

	/**
	 * Runs the test suite on the current project.
	 * 
	 * @return		an int, denoting the exit value of the test process
	 */
	public abstract int test();

	/**
	 * Gets the failing test for the current project.
	 * 
	 * @return		a List of Strings, containing information about the failing tests for
	 * 				the current project
	 */
	public abstract List<String> getFailingTests();

	/**
	 * Generates the patch between the buggy and fixed versions or vice versa of the
	 * of the current project.
	 * 
	 * @return		a Patch between the buggy and fixed versions or vice versa
	 * 				of the current project
	 */
	public abstract Patch<String> generatePatch();

	/**
	 * Applies the generated patch to either the buggy or fixed version, depending on relevance.
	 * 
	 * @return		an int, denoting the exit value of the apply process
	 */
	public abstract int applyPatch();

	/**
	 * Resets the current project to its checkout state.
	 */
	public abstract void reset();

}