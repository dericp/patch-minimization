package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;

public class HunksInput implements DDInput {

    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;
    
    public HunksInput(UnifiedDiff unifiedDiff) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        circumstances = new ArrayList<Integer>();
        removedElements = new HashSet<Integer>();
        diffNumber = -1;
        hunkNumber = -1;
    }
    
    public HunksInput(UnifiedDiff unifiedDiff, List<Integer> circumstances, int diffNumber) {
        // this.diffUtils = diffUtils;
        // ^ that doesn't work because it means that every HunksInput will be modifying the same object
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        setRemovedElements();
        this.diffNumber = diffNumber;
    }
    
    @Override
    public Type getKind() {
        return Type.HUNKS;
    }
    
    public UnifiedDiff getUnifiedDiff() {
        return unifiedDiff;
    }
    
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }
    
    public void setCircumstances(List<Integer> circumstances, int diffNumber, int hunkNumber) {
        this.circumstances = circumstances;
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }
    
    private void setRemovedElements() {
        for (int i = 0; i < unifiedDiff.getDiffs().get(diffNumber).getHunks().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }
    
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeHunk(diffNumber, index);
        }
    }
    
    public int getDiffNumber() {
        return diffNumber;
    }

    @Override
    public int getHunkNumber() {
        // TODO Auto-generated method stub
        return hunkNumber;
    }

}
