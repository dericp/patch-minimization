package edu.washington.bugisolation;

import edu.washington.bugisolation.util.Utils;
import edu.washington.cs.dericp.diffutils.UnifiedDiff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An input that allows for the manipulation of lines in a unified diff.
 */
public class LinesInput implements DDInput {

    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;

    /**
     * Constructs a LinesInput where all fields other than unifiedDiff are set
     * to their default irrelevant values. Circumstances should be set later by
     * setCircumstances().
     *
     * @param unifiedDiff
     */
    public LinesInput(UnifiedDiff unifiedDiff) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        circumstances = new ArrayList<Integer>();
        removedElements = new HashSet<Integer>();
        diffNumber = -1;
        hunkNumber = -1;
    }

    /**
     * Constructs a new LinesInput.
     *
     * @param unifiedDiff
     *            the unified diff relevant to this input
     * @param circumstances
     *            the list of line numbers relevant to this input
     * @param diffNumber
     *            the diff number relevant to this input
     * @param hunkNumber
     *            the hunk number relevant to this input
     */
    public LinesInput(UnifiedDiff unifiedDiff, List<Integer> circumstances,
            int diffNumber, int hunkNumber) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }

    /**
     * Sets the elements that should be removed by removeElements().
     */
    private void setRemovedElements() {
        removedElements = new HashSet<Integer>();
        for (int i = 0; i < unifiedDiff.getDiffs().get(diffNumber).getHunks()
                .get(hunkNumber).getModifiedLines().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getHunkNumber()
     */
    @Override
    public int getHunkNumber() {
        return hunkNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#setCircumstances(java.util.List,
     * int, int)
     */
    @Override
    public void setCircumstances(List<Integer> circumstances, int diffNumber,
            int hunkNumber) {
        this.circumstances = circumstances;
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getInputType()
     */
    @Override
    public InputType getInputType() {
        return InputType.LINES;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getUnifiedDiff()
     */
    @Override
    public UnifiedDiff getUnifiedDiff() {
        return unifiedDiff;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getCircumstances()
     */
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#removeElements()
     */
    @Override
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeChangeFromHunk(diffNumber, hunkNumber, index);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getDiffNumber()
     */
    @Override
    public int getDiffNumber() {
        // TODO Auto-generated method stub
        return diffNumber;
    }
    
    public DDInput getEmptyInput() {
        DDInput empty = new LinesInput(unifiedDiff, new ArrayList<Integer>(), diffNumber, hunkNumber);
        return empty;
    }
    public DDInput getComplement(int start, int stop) {
        DDInput complement = new LinesInput(unifiedDiff,
                Utils.minusIndices(circumstances, start, stop),
                diffNumber,
                hunkNumber);
        return complement;
    }
    
    public DDInput getCopy() {
        DDInput copy = new LinesInput(unifiedDiff,
                new ArrayList<Integer>(circumstances),
                diffNumber,
                hunkNumber);
        return copy;
    }
}
