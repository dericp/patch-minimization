package edu.washington.refactoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washington.cs.dericp.diffutils.Diff;
import edu.washington.cs.dericp.diffutils.Hunk;
import edu.washington.cs.dericp.diffutils.UnifiedDiff;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class contains the functions that automatically refactor code.
 * @author dpang
 *
 */
public class RefactoringRemoval {
    
    public static List<String> removeWhiteSpace() {
        throw new NotImplementedException();
    }
    
    /* This is definitely broken. There needs to be significant
     * testing done on diff-utils in order to improve confidence
     * in this code.
     */
    public static void removeComments(UnifiedDiff unifiedDiff) {
        List<Diff> diffs = unifiedDiff.getDiffs();
        for (int i = 0; i < diffs.size(); i++) {
            List<Hunk> hunks = diffs.get(i).getHunks();
            for (int j = 0; j < hunks.size(); j++) {
                
                List<String> contextLines = hunks.get(j).getStartContext();
                boolean inCommentBlock = false;
                for (String s : contextLines) {
                    if (s.contains("/*")) {
                        inCommentBlock = true;
                    }
                    if (inCommentBlock && s.contains("*/")) {
                        inCommentBlock = false;
                    }
                }
                List<String> lines = hunks.get(j).getModifiedLines();
                
                for (int k = 0; k < lines.size(); k++) {
                    String line = lines.get(k);
                    if (line.contains("//")) {
                        unifiedDiff.removeChangeFromHunk(i, j, k);
                    } else if (line.contains("/*") || inCommentBlock) {
                        for (int l = k; l < lines.size(); l++) {
                            String line2 = lines.get(l);
                            unifiedDiff.removeChangeFromHunk(i, j, l);
                            if (line2.contains("*/")) {
                                break;
                            }
                        }
                    }
                }
                
            }
        }
        
    }
    
    public static void removeIrrelevantAnnotations() {
        throw new NotImplementedException();
    }
}
