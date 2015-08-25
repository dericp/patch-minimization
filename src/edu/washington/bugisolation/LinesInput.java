package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;

public class LinesInput implements DDInput {
    
    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;
    
    public LinesInput(UnifiedDiff unifiedDiff) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        circumstances = new ArrayList<Integer>();
        removedElements = new HashSet<Integer>();
        diffNumber = -1;
        hunkNumber = -1;
    }
    
    public LinesInput(UnifiedDiff unifiedDiff, List<Integer> circumstances, int diffNumber, int hunkNumber) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        setRemovedElements();
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
    }
    
    public int getHunkNumber() {
        return hunkNumber;
    }
    
    public void setCircumstances(List<Integer> circumstances, int diffNumber, int hunkNumber) {
        this.circumstances = circumstances;
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }
    
    private void setRemovedElements() {
        removedElements = new HashSet<Integer>();
        for (int i = 0; i < unifiedDiff.getDiffs().get(diffNumber).getHunks().get(hunkNumber).getModifiedLines().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }
    
    @Override
    public Type getKind() {
        return Type.LINES;
    }
    
    public UnifiedDiff getUnifiedDiff() {
        return unifiedDiff;
    }
    
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }

    @Override
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeChangeFromHunk(diffNumber, hunkNumber, index);
        }
    }

    @Override
    public int getDiffNumber() {
        // TODO Auto-generated method stub
        return diffNumber;
    }
}
