package edu.washington.bugisolation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;

public class DiffsInput implements DDInput {
    
    private UnifiedDiff unifiedDiff;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int diffNumber;
    private int hunkNumber;
    
    public DiffsInput(UnifiedDiff unifiedDiff, List<Integer> circumstances) {
        this.unifiedDiff = new UnifiedDiff(unifiedDiff);
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        setRemovedElements();
    }
    
    public Type getKind() {
        return Type.DIFFS;
    }
    
    @Override
    public UnifiedDiff getUnifiedDiff() {
        return unifiedDiff;
    }

    @Override
    public List<Integer> getCircumstances() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCircumstances(List<Integer> circumstances, int diffNumber, int hunkNumber) {
        this.diffNumber = diffNumber;
        this.hunkNumber = hunkNumber;
    }

    @Override
    public void removeElements() {
        for (int index : removedElements) {
            unifiedDiff.removeDiff(index);
        }
    }

    @Override
    public int getHunkNumber() {
        return hunkNumber;
    }
    
    private void setRemovedElements() {
        for (int i = 0; i < unifiedDiff.getDiffs().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }

    @Override
    public int getDiffNumber() {
        // TODO Auto-generated method stub
        return diffNumber;
    }
}
