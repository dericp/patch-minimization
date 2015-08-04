package edu.washington.bugisolation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import difflib.*;
import edu.washington.bugisolation.DeltaDebugging.Granularity;
import edu.washington.bugisolation.util.Operations;

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
		Patch<String> patch = d4j.generatePatch();

		/* minimize patch with delta debugging */
		DeltaDebugging dd = new DeltaDebugging(d4jPI, d4j);
	    
		/* get the minimized deltas with ddminDelta */
		DDInput<Delta<String>> input = new DeltaInput(patch.getDeltas());
		List<Delta<String>> minDeltas = dd.ddmin(input, Granularity.QUADRATIC);
	   
		/* minimize each individual delta's chunks */ 
		List<Delta<String>> minChunkDeltas = dd.minimizeChunks(minDeltas, Granularity.LINEAR);
		
		/* minimize the new list of deltas */
		List<Delta<String>> minChunksMinDeltas = dd.ddmin(new DeltaInput(minChunkDeltas), Granularity.QUADRATIC);

		/* create the minimized diff */
		Patch<String> minimizedPatch = new Patch<String>();
		for (Delta<String> delta : minChunksMinDeltas) {
			minimizedPatch.addDelta(delta);
		}
	
		List<String> diffLines;
		
		diffLines = DiffUtils.generateUnifiedDiff (
		        d4jPI.getDiffPathA()
		        , d4jPI.getDiffPathB()
		        , d4jPI.getModifiedFile()
		        , minimizedPatch
		        , 3);
		Operations.linesToFile (
		        diffLines, ProjectInfo.WORKSPACE + "min_diffs/" + d4jPI.getFullProjectName()
		        +  '_' + d4jPI.isFixedToBuggy() + "_min.diff");
	}
}
