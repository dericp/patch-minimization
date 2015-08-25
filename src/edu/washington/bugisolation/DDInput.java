package edu.washington.bugisolation;

import java.util.List;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;

public interface DDInput {

       public enum Type {
           DIFFS, HUNKS, LINES
       }
       
       public Type getKind();
       
       public UnifiedDiff getUnifiedDiff();
       
       public List<Integer> getCircumstances();
       
       public void setCircumstances(List<Integer> circumstances, int DiffNumber, int hunkNumber);
       //TODO: remove the parameter hunkNumber where it's not needed
       public void removeElements();
       
       public int getHunkNumber();
       
       public int getDiffNumber();
}
