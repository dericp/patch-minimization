package edu.washington.bugisolation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.bugisolation.util.Diff;
import edu.washington.bugisolation.util.DiffUtils;

public class HunksInput implements DDInput {

    DiffUtils diffUtils;
    List<Integer> circumstances;
    Set<Integer> removedElements;
    
    public HunksInput(DiffUtils diffUtils, List<Integer> circumstances) {
        // this.diffUtils = diffUtils;
        // ^ that doesn't work because it means that every HunksInput will be modifying the same object
        this.diffUtils = new DiffUtils(new Diff(diffUtils.getDiff()));
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        for (int i = 0; i < diffUtils.getDiff().getHunks().size(); ++i) {
            removedElements.add(i);
        }
        removedElements.removeAll(circumstances);
    }
    
    @Override
    public Type getKind() {
        return Type.HUNKS;
    }
    
    public DiffUtils getDiffUtils() {
        return diffUtils;
    }
    
    @Override
    public List<Integer> getCircumstances() {
        return circumstances;
    }
    
    public Set<Integer> getRemovedElements() {
        return removedElements;
    }
    
    public void removeElements() {
        for (int index : removedElements) {
            diffUtils.removeHunk(index);
        }
    }

}
