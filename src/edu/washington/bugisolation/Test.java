package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.washington.bugisolation.util.Diff;
import edu.washington.bugisolation.util.DiffUtils;
import edu.washington.bugisolation.util.Hunk;

public class Test {
    
    /* public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("@@ -1372,22 +1372,11 @@");
        Hunk hunk = new Hunk(list);
        
        System.out.println(hunk.getContextInfo());
        int x = 5;
        --x;
        System.out.println(x);
    } */
    
    /* public static void main(String[] args) {
        String contextInfo = "@@ -1372,22 +1372,11 @@";
        
        System.out.println(contextInfo);
        
        Scanner input = new Scanner(contextInfo).useDelimiter("[^0-9]+");
        System.out.println(in.nextInt());
        System.out.println(in.nextInt());
        System.out.println(in.nextInt());
        System.out.println(in.nextInt());
        
        String temp = contextInfo.substring(4, contextInfo.length() - 3);
        
        String originalInfo = temp.substring(temp.indexOf('-') + 1, temp.indexOf('+'));
        String revisedInfo = temp.substring(temp.indexOf('+') + 1);
        
        int originalLineNumber = Integer.valueOf(originalInfo.substring(0, originalInfo.indexOf(",")));
        int originalHunkSize = Integer.valueOf(originalInfo.substring(originalInfo.indexOf(',') + 1, originalInfo.length()));
        int revisedLineNumber = Integer.valueOf(revisedInfo.substring(0, revisedInfo.indexOf(',')));
        int revisedHunkSize = Integer.valueOf(revisedInfo.substring(revisedInfo.indexOf(',') + 1, revisedInfo.length()));
        
        System.out.println("@@ -" + originalLineNumber + ',' + originalHunkSize + " +" + revisedLineNumber + ',' + revisedHunkSize + " @@");
    } */
    
    /* public static void main(String[] args) {
        Diff diff = new Diff(ProjectInfo.WORKSPACE + "Lang_30.diff");
        DiffUtils diffUtils = new DiffUtils(diff);
        
        for (int i = 0; i < diff.getHunks().size(); ++i) {
            for (int j = 0; j < 5; ++j) {
                diffUtils.removeLineFromHunk(i, j);
            }
        }
        
        diff.exportDiff(ProjectInfo.WORKSPACE + "TEST2.diff");
    } */
    
    /* public static void main(String[] args) {
        Diff diff = new Diff(ProjectInfo.WORKSPACE + "Lang_30.diff");
        
        for (Hunk hunk : diff.getHunks()) {
            for (int i = 0; i < 5; ++i) {
                hunk.removeLine(i);
            }
        }
        diff.exportDiff(ProjectInfo.WORKSPACE + "TEST1.diff");
    } */
    
    public static void main(String[] args) {
        Diff diff = new Diff(ProjectInfo.WORKSPACE + "Lang_30.diff");
        DiffUtils diffUtils = new DiffUtils(diff);
        
        diffUtils.removeHunk(0);
        
        diffUtils.exportUnifiedDiff(ProjectInfo.WORKSPACE + "TEST.diff");
    }
}
