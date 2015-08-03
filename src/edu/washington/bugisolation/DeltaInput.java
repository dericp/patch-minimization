package edu.washington.bugisolation;

import java.util.List;

import difflib.Delta;

public class DeltaInput implements DDInput<Delta<String>> {

    List<Delta<String>> deltas;
    
    public DeltaInput(List<Delta<String>> deltas) {
        this.deltas = deltas;
    }
    
    @Override
    public Type getKind() {
        return Type.DELTA;
    }

    @Override
    public List<Delta<String>> getCircumstances() {
        return deltas;
    }

}
