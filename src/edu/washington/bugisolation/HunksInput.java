package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.bugisolation.diffutils.UnifiedDiff;

/**
 * An input that allows for the manipulation of hunks in a unified diff.
 * 
 * @author Deric Hua Pang
 *
 */
public class HunksInput implements DDInput {

    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;
    
    /**
     * Creates a new HunksInput where all fields other than unifiedDiff are set to their default
     * irrelevant values. Circumstances should be set later by setCircumstances().
     * 
     * @param unifiedDiff   the unified diff relevant to this input
     */
    public HunksInput(UnifiedDiff unifiedDiff) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        circumstances = new ArrayList<Integer>();
        removedElements = new HashSet<Integer>();
        diffNumber = -1;
        hunkNumber = -1;
    }
    
    /**
     * Creates a new HunksInput.
     * 
     * @param unifiedDiff       the unified diff relevant to this input
     * @param circumstances     a list of hunk numbers relevant to this input
     * @param diffNumber        the diff number relevant to this input
     */
    public HunksInput(UnifiedDiff unifiedDiff, List<Integer> circumstances, int diffNumber) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        setRemovedElements();
        this.diffNumber = diffNumber;
    }
    
    /**
     * Sets the elements that should be removed by removeElements().
     */
    private void setRemovedElements() {
        for (int i = 0; i < unifiedDiff.getDiffs().get(diffNumber).getHunks().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getInputType()
     */
    @Override
    public InputType getInputType() {
        return InputType.HUNKS;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getUnifiedDiff()
     */
    @Override
    public UnifiedDiff getUnifiedDiff() {
        return unifiedDiff;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getCircumstances()
     */
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#setCircumstances(java.util.List, int, int)
     */
    @Override
    public void setCircumstances(List<Integer> circumstances, int diffNumber, int hunkNumber) {
        this.circumstances = circumstances;
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#removeElements()
     */
    @Override
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeHunk(diffNumber, index);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getDiffNumber()
     */
    @Override
    public int getDiffNumber() {
        return diffNumber;
    }

    /*
     * (non-Javadoc)
     * @see edu.washington.bugisolation.DDInput#getHunkNumber()
     */
    @Override
    public int getHunkNumber() {
        return hunkNumber;
    }

}
