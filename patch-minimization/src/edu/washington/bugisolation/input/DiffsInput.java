package edu.washington.bugisolation.input;

import edu.washington.bugisolation.util.Utils;
import edu.washington.cs.dericp.diffutils.UnifiedDiff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An input that allows for the manipulation of diffs in a unified diff.
 */
public class DiffsInput implements DDInput {

    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;

    /**
     * Constructs a new DiffsInput.
     *
     * @param unifiedDiff
     *            the unified diff relevant to this input
     * @param circumstances
     *            a list of diff numbers relevant to this input
     */
    public DiffsInput(UnifiedDiff unifiedDiff, List<Integer> circumstances) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        setRemovedElements();
    }

    /**
     * Sets the elements that should be removed by removeElements().
     */
    private void setRemovedElements() {
        for (int i = 0; i < unifiedDiff.getDiffs().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.washington.bugisolation.DDInput#getInputType()
     */
    @Override
    public InputType getInputType() {
        return InputType.DIFFS;
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
        // TODO Auto-generated method stub
        return null;
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
     * @see edu.washington.bugisolation.DDInput#removeElements()
     */
    @Override
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeDiff(index);
        }
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
     * @see edu.washington.bugisolation.DDInput#getDiffNumber()
     */
    @Override
    public int getDiffNumber() {
        // TODO Auto-generated method stub
        return diffNumber;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getEmptyInput()
     */
    public DDInput getEmptyInput() {
        DDInput empty = new DiffsInput(unifiedDiff, new ArrayList<Integer>());
        return empty;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getComplement(int, int)
     */
    public DDInput getComplement(int start, int stop) {
        DDInput complement = new DiffsInput(unifiedDiff,
                Utils.minusIndices(circumstances, start, stop));
        return complement;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getCopy()
     */
    public DDInput getCopy() {
        DDInput copy = new DiffsInput(unifiedDiff,
                new ArrayList<Integer>(circumstances));
        return copy;
    }
}
