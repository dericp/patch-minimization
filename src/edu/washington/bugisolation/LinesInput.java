package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.bugisolation.util.Diff;
import edu.washington.bugisolation.util.DiffUtils;

public class LinesInput implements DDInput {
    
    private DiffUtils diffUtils;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    private int hunkNumber;
    
    public LinesInput(DiffUtils diffUtils) {
        this.diffUtils = new DiffUtils(new Diff(diffUtils.getDiff()));
        circumstances = new ArrayList<Integer>();
        removedElements = new HashSet<Integer>();
        hunkNumber = 0;
    }
    
    public LinesInput(DiffUtils diffUtils, List<Integer> circumstances, int hunkNumber) {
        this.diffUtils = new DiffUtils(new Diff(diffUtils.getDiff()));
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        setRemovedElements();
    }
    
    public int getHunkNumber() {
        return hunkNumber;
    }
    
    public void setCircumstances(List<Integer> circumstances, int hunkNumber) {
        this.circumstances = circumstances;
        this.hunkNumber = hunkNumber;
        setRemovedElements();
    }
    
    private void setRemovedElements() {
        removedElements = new HashSet<Integer>();
        for (int i = 0; i < diffUtils.getDiff().getHunks().get(hunkNumber).getModifiedLines().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }
    
    @Override
    public Type getKind() {
        return Type.LINES;
    }
    
    public DiffUtils getDiffUtils() {
        return diffUtils;
    }
    
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }

    @Override
    public void removeElements() {
        for (int index : removedElements) {
            diffUtils.removeChangeFromHunk(hunkNumber, index);
        }
    }
}
