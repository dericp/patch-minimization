package edu.washington.bugisolation.input;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;

import java.util.List;

/**
 * An interface that defines the functions needed for the delta debugging
 * process.
 */
public interface DDInput {

    public enum InputType {
        DIFFS, HUNKS, LINES
    }

    /**
     * Gets the type of DDInput that a particular instance is.
     *
     * @return the type of DDInput this is
     */
    public InputType getInputType();

    /**
     * Gets the unified diff of the input.
     *
     * @return the unified diff of the input
     */
    public UnifiedDiff getUnifiedDiff();

    /**
     * The list of circumstances considered by this input.
     *
     * @return the list of circumstances associated with this input
     */
    public List<Integer> getCircumstances();
    
    /**
     * Sets the circumstances to be considered by this input.
     *
     * @param circumstances
     *            the new list of circumstances to be considered
     * @param diffNumber
     *            the relevant diff number, -1 if irrelevant
     * @param hunkNumber
     *            the relevant hunk number, -1 if irrelevant
     */
    public void setCircumstances(List<Integer> circumstances, int diffNumber,
            int hunkNumber);

    /**
     * Gets the relevant hunk number.
     *
     * @return the relevant hunk number, -1 if irrelevant
     */
    public int getHunkNumber();

    /**
     * Gets the relevant diff number.
     *
     * @return the relevant diff number, -1 if irrelevant
     */
    public int getDiffNumber();
    
    /**
     * Removes all irrelevant elements -- those that are not noted by the list
     * of circumstances.
     */
    public void removeElements();
    
    /**
     * Generates a DDInput that is empty for the purpose of the delta
     * debugging algorithm.
     * 
     * @return  an empty input, one that will make no changes to the code
     */
    public DDInput getEmptyInput();
    
    /**
     * Generates a DDInput that is separate copy. This helps ensure that
     * references do not get mixed up.
     * 
     * @return  a copy of the current input
     */
    public DDInput getCopy();
    
    /**
     * Generates a DDInput whose circumstances excluded all the numbers
     * between the start and stop.
     * 
     * @param start
     *            the start index of the section to be removed, inclusive
     * @param stop
     *            the end index of the section to be removed, exclusive
     * @return  the complement to the current input
     */
    public DDInput getComplement(int start, int stop);

}
