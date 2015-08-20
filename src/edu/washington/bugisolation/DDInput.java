package edu.washington.bugisolation;

import java.util.List;
import java.util.Set;

import edu.washington.bugisolation.util.DiffUtils;

public interface DDInput {

       public enum Type {
           HUNKS, LINES
       }
       
       public Type getKind();
       
       public DiffUtils getDiffUtils();
       
       public List<Integer> getCircumstances();
       
       public Set<Integer> getRemovedElements();
       
       public void removeElements();
}
