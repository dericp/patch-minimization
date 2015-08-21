package edu.washington.bugisolation;

import java.util.List;

import edu.washington.bugisolation.util.DiffUtils;

public interface DDInput {

       public enum Type {
           HUNKS, LINES
       }
       
       public Type getKind();
       
       public DiffUtils getDiffUtils();
       
       public List<Integer> getCircumstances();
       
       public void setCircumstances(List<Integer> circumstances, int hunkNumber);
       //TODO: remove the parameter hunkNumber where it's not needed
       public void removeElements();
       
       public int getHunkNumber();
}
