package edu.washington.bugisolation.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* At some point, should implement a way to cleanly remove hunks that correctly updates context information */

public class Diff {
    
    private String filePathA;
    private String filePathB;
    private List<Hunk> hunks;
    
    public Diff(String filePathA, String filePathB, List<Hunk> hunks) {
        this.filePathA = filePathA;
        this.filePathB = filePathB;
        this.hunks = hunks;
    }
    
    public Diff(List<String> diffLines) {
        setFilePaths(diffLines);
        hunks = new ArrayList<Hunk>();
        readHunks(diffLines);
    }
    
    public Diff(String diffFilePath) {
        this(Operations.fileToLines(diffFilePath));
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
            export.addAll(hunk.getHunkLines());
        }
        Operations.linesToFile(export, filepath);
    }
    
    public List<Hunk> getHunks() {
        return hunks;
    }
}
