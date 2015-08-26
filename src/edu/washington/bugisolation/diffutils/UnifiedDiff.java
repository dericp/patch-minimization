package edu.washington.bugisolation.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.washington.bugisolation.util.Operations;

public class UnifiedDiff {
    
    // This field changes depending on what signifies a new diff.
    // In some formats, this could be "diff," and in others "---".
    public static final String DIFF_SPLIT = "diff";
    private List<Diff> diffs;
    
    public UnifiedDiff(List<String> unifiedDiffLines) {
        readDiffs(unifiedDiffLines);
    }
    
    public UnifiedDiff(String filePath) {
        this(Operations.fileToLines(filePath)); 
    }
    
    // Special constructor that can create a copy of a unified diff
    public UnifiedDiff(UnifiedDiff other) {
        diffs = new ArrayList<Diff>();
        for (Diff diff : other.diffs) {
            diffs.add(new Diff(diff));
        }
    }
    
    private void readDiffs(List<String> unifiedDiffLines) {
        diffs = new ArrayList<Diff>();
        if (unifiedDiffLines.isEmpty()) {
            throw new IllegalArgumentException("Diff is empty");
        }
        
        Iterator<String> iter = unifiedDiffLines.iterator();
        String line = iter.next();
        
        while (iter.hasNext()) {
            if (line.startsWith(DIFF_SPLIT)) {
                List<String> diffLines = new ArrayList<String>();
                diffLines.add(line);
                
                line = iter.next();
                
                while (!line.startsWith(DIFF_SPLIT) && iter.hasNext()) {
                    diffLines.add(line);
                    line = iter.next();
                }
                
                // if last line of unifiedDiff
                if (!iter.hasNext()) {
                    diffLines.add(line);
                }
                diffs.add(new Diff(diffLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    public List<Diff> getDiffs() {
        return diffs;
    }
    
    public void removeDiff(int diffNumber) {
        diffs.set(diffNumber, null);
    }
    
    // zero based indexing
    public void removeHunk(int diffNumber, int hunkNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk removedHunk = hunks.get(hunkNumber);
        int offset = removedHunk.getOriginalHunkSize() - removedHunk.getRevisedHunkSize();
        for (int i = hunkNumber + 1; i < diffs.get(diffNumber).getHunks().size(); ++i) {
           hunks.get(i).modifyRevisedLineNumber(offset);
        }
        hunks.set(hunkNumber, null);
    }
    
    // zero based indexing
    public void removeChangeFromHunk(int diffNumber, int hunkNumber, int lineNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk modifiedHunk = hunks.get(hunkNumber);
        int result = modifiedHunk.removeLine(lineNumber);
        if (result != 0) {
            for (int i = hunkNumber + 1; i < hunks.size(); i++) {
                Hunk currentHunk = hunks.get(i);
                if (currentHunk != null) {
                    if (result == 1) {
                        hunks.get(i).modifyRevisedLineNumber(-1);
                    }
                    if (result == -1) {
                        hunks.get(i).modifyRevisedLineNumber(1);
                    }
                }
            }
        }
    }
    
    public void exportUnifiedDiff(String filePath) {
        List<String> export = new ArrayList<String>();
        for (Diff diff : diffs) {
            export.addAll(diff.diffToLines());
        }
        Operations.linesToFile(export, filePath);
    }
}
