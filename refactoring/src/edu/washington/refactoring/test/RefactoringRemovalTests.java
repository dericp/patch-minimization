package edu.washington.refactoring.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;
import edu.washington.refactoring.RefactoringRemoval;

public class RefactoringRemovalTests {
    
    @Test
    public void testRemoveComments() {
        UnifiedDiff unifiedDiff = new UnifiedDiff("/Users/dpang/workspace/defects4j-automatic-refactoring/src/edu/washington/refactoring/test/TestDiff.test");
        RefactoringRemoval.removeComments(unifiedDiff);
        unifiedDiff.exportUnifiedDiff("/Users/dpang/workspace/defects4j-automatic-refactoring/src/edu/washington/refactoring/test/test.output");
    }

}
