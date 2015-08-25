package edu.washington.bugisolation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;


/**
 * Minimizes and isolates the bug between two versions of a program in the Defects4J database.
 * 
 * @author dpang
 *
 */
public class BugMinimizer {
	
	public static void main(String[] args) throws IOException {
		
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		/* setting project information */
	    String projectName;
	    String projectVersion;
	    Boolean fixedToBuggy;
	    
    	System.out.println("Project name?");
    	projectName = in.readLine();
    	System.out.println(projectName);
    	
    	System.out.println("Project version?");
    	projectVersion = in.readLine();
    	System.out.println(projectVersion);
    		
    	System.out.println("Choose an option:");
    	System.out.println("true  : fixed ---> buggy (min patch that fails)");
    	System.out.println("false : buggy ---> fixed (min patch that passes)");
    	fixedToBuggy = Boolean.parseBoolean(in.readLine());
    	System.out.println(fixedToBuggy);
    	
    	if (projectName == null || projectVersion == null || fixedToBuggy == null) {
	        throw new IOException();
    	}
		
		/* creating the needed objects */
		ProjectInfo d4jPI = new Defects4JPI(projectName, projectVersion, fixedToBuggy);
		Project d4j = new Defects4J(d4jPI);
		
		/* checkout the fixed and buggy versions of the project with D4j */
		d4j.checkout();
		
		/* create a patch between fixed and buggy versions of the project or vice versa */
		d4j.generatePatch();

		/* minimize patch with delta debugging */
		DeltaDebugging dd = new DeltaDebugging(d4jPI, d4j);
		
		UnifiedDiff unifiedDiff = new UnifiedDiff(ProjectInfo.WORKSPACE + d4jPI.getFullProjectName() + ".diff");
		
		// right now, I know there will only one diff in each unified diff, so I can set the path manually
		unifiedDiff.getDiffs().get(0).setFilePaths (
		        d4jPI.getSrcDirectory() + d4jPI.getModifiedPath(".java")
                , d4jPI.getSrcDirectory() + d4jPI.getModifiedPath(".java"));
	    
		unifiedDiff.exportUnifiedDiff(ProjectInfo.WORKSPACE + "defects4j-data/" + d4jPI.getFullProjectName() + "_" + d4jPI.isFixedToBuggy() + ".diff");
		unifiedDiff = dd.minimizeDiffs(unifiedDiff);
		unifiedDiff = dd.minimizeHunks(unifiedDiff);
		unifiedDiff = dd.minimizeLines(unifiedDiff);
	}
}
