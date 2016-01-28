package edu.washington.refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;
import edu.washington.refactoring.util.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class contains the functions that automatically refactor code.
 * @author dpang
 *
 */
public class RefactoringRemoval {
    
    public static List<String> removeEmptyLines(String filePath) {
        List<String> fileLines = Utils.fileToLines(filePath);
        UnifiedDiff patch = new UnifiedDiff(fileLines);
        
        for (String s : fileLines) {
            if ((s.startsWith("+") || s.startsWith("-")) && s.substring(1).trim().length() == 0) {
                patch.removeChange(s);
            }
        }
        return patch.exportPatchToLines();
    }
    
    public static void removeComments(UnifiedDiff unifiedDiff) {   
        throw new NotImplementedException();
    }
    
    public static void removeIrrelevantAnnotations() {
        throw new NotImplementedException();
    }
}
