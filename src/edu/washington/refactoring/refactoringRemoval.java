package edu.washington.refactoring;

import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class contains the functions that automatically refactor code.
 * @author dpang
 *
 */
public class refactoringRemoval {
    
    public static List<String>  removeWhiteSpace(List<String> file) {
        List<String> ret = new ArrayList<String>();
        
        for (String line : file) {
            if (!line.isEmpty()) {
                ret.add(line);
            }
        }
        
        return ret;
    }
    
    public static void removeCommentsDocumntation() {
        throw new NotImplementedException();
    }
    
    public static void removeIrrelevantAnnotations() {
        throw new NotImplementedException();
    }
}
