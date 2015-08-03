package edu.washington.bugisolation;

import java.util.List;

public interface DDInput<T> {

       public enum Type {
           DELTA, CHUNK
       }
       
       public Type getKind();
       
       public List<T> getCircumstances();
}
