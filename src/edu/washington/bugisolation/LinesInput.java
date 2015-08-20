package edu.washington.bugisolation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.washington.bugisolation.util.Diff;
import edu.washington.bugisolation.util.DiffUtils;

public class LinesInput implements DDInput {
    
    private DiffUtils diffUtils;
    private List<Integer> circumstances;
    private Set<Integer> removedElements;
    
    public LinesInput(DiffUtils diffUtils, List<Integer> circumstances) {
        this.diffUtils = new DiffUtils(new Diff(diffUtils.getDiff()));
        this.circumstances = circumstances;
        removedElements = new HashSet<Integer>();
        for (int i = 0; i < diffUtils.getDiff().getHunks().size(); ++i) {
            circumstances.add(i);
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
    public Set<Integer> getRemovedElements() {
        return removedElements;
    }

    @Override
    public void removeElements() {

    }
}
