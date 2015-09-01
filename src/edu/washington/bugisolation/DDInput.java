package edu.washington.bugisolation;

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
    
    public DDInput getEmptyInput();
    
    public DDInput getCopy();
    
    public DDInput getComplement(int start, int stop);

}
