/* package edu.washington.bugisolation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Diff {
    
    private String filePathA;
    private String filePathB;
    private List<Hunk> hunks;
    
    public Diff(Diff otherDiff) {
        filePathA = otherDiff.filePathA;
        filePathB = otherDiff.filePathB;
        hunks = new ArrayList<Hunk>();
        for (Hunk hunk : otherDiff.getHunks()) {
            if (hunk != null) {
                Hunk temp = new Hunk(hunk.getHunkLines());
                hunks.add(temp);
            }
        }
    }
    public Diff(List<String> diffLines) {
        this(diffLines, null, null);
    }
    
    public Diff(List<String> diffLines, String filePathA, String filePathB) {
        hunks = new ArrayList<Hunk>();
        readHunks(diffLines);
        if (filePathA == null || filePathB == null) {
            setFilePaths(diffLines);
        } else {
            this.filePathA = "--- a/" + filePathA;
            this.filePathB = "+++ b/" + filePathB;
        }
    }
    
    public Diff(String diffFilePath) {
        this(Operations.fileToLines(diffFilePath));
    }
    
    public Diff(String diffFilePath, String filePathA, String filePathB) {
        this(Operations.fileToLines(diffFilePath), filePathA, filePathB);
    }
    
    private void setFilePaths(List<String> diffLines) {
        for (Iterator<String> iter = diffLines.iterator(); iter.hasNext();) {
            String line = iter.next();
            if (line.startsWith("---")) {
                filePathA = line;
                filePathB = iter.next();
                break;
            }
        }
    }
    
    private void readHunks(List<String> diffLines) {
        if (diffLines.isEmpty()) {
            throw new IllegalArgumentException("Diff is empty");
        }
        
        Iterator<String> iter = diffLines.iterator();
        String line = iter.next();
        
        while (iter.hasNext()) {
            if (line.startsWith("@@")) {
                List<String> hunkLines = new ArrayList<String>();
                hunkLines.add(line);
                
                line = iter.next();
                
                while (!line.startsWith("@@") && iter.hasNext()) {
                    hunkLines.add(line);
                    line = iter.next();
                }
                
                // if last line of Hunk
                if (!iter.hasNext()) {
                    hunkLines.add(line);
                }
                hunks.add(new Hunk(hunkLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    public void exportDiff(String filepath) {
        List<String> export = new ArrayList<String>();
        export.add(filePathA);
        export.add(filePathB);
        for (Hunk hunk : hunks) {
            if (hunk != null) {
                export.addAll(hunk.getHunkLines());
            }
        }
        Operations.linesToFile(export, filepath);
    }
    
    public List<Hunk> getHunks() {
        return hunks;
    }
} */
