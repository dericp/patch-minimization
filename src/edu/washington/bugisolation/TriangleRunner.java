package edu.washington.bugisolation;

import java.util.List;
import java.util.Scanner;

import difflib.*;
import edu.washington.bugisolation.DeltaDebugging.Granularity;
import edu.washington.bugisolation.util.Operations;

public class TriangleRunner {
	
	public static void main(String[] args) {
		
		/* this scanner gets the project information from the user */
		Scanner console = new Scanner(System.in);
		
		System.out.println("Project name? ");
		String projectName = console.next();
		
		System.out.println("Project version? ");
		String projectVersion = console.next();
		
		System.out.println("Choose an option:");
		System.out.println("true  : fixed ---> buggy (min patch that fails)");
		System.out.println("false : buggy ---> fixed (min patch that passes)");
		boolean buggyToFixed = Boolean.parseBoolean(console.next());
		console.close();
		
		/* creating the needed objects */
		System.out.println("Creating ProjectInfo");
		ProjectInfo proj = new TrianglePI(projectName, projectVersion, buggyToFixed);
		System.out.println("Creating Triangle");
		Project tri = new Triangle(proj);
		
		/* checkout the fixed and buggy versions of the project with D4j */
		tri.checkout();
		
		/* create a patch between fixed and buggy versions of the project or vice versa */
	    Patch<String> patch = tri.generatePatch();

		/* minimize patch with delta debugging */
	    DeltaDebugging dd = new DeltaDebugging(proj, tri);
	    
	    /* get the minimized deltas with ddminDelta; */
	    //List<Delta<String>> minDeltas = dd.ddmin(new HunksInput(patch.getDeltas()), Granularity.EXPONENTIAL);
	    
	    /* minimize each individual delta */
	  //  List<Delta<String>> minChunkDeltas = dd.minimizeChunks(minDeltas, Granularity.LINEAR); 
	    
	    /* create the minimized diff */
	    Patch<String> minimizedPatch = new Patch<String>();
	 
	    /*for (Delta<String> delta : minChunkDeltas) {
	    	minimizedPatch.addDelta(delta);
	    }
	    
	    List<String> diffLines;
	    
	    if (proj.isFixedToBuggy()) {
	    	diffLines = DiffUtils.generateUnifiedDiff (
	    			proj.getDiffPathA()
	    			, proj.getDiffPathB()
	    			, proj.getFixedFile()
	    			, minimizedPatch
	    			, 3);
	    } else {
	    	diffLines = DiffUtils.generateUnifiedDiff (
	    			proj.getDiffPathA()
	    			, proj.getDiffPathB()
	    			, proj.getBuggyFile()
	    			, minimizedPatch
	    			, 3);
	    } 
	    
    	Operations.linesToFile (
    			diffLines, ProjectInfo.WORKSPACE + proj.getProjectName()
    			+ '_' + proj.getProjectVersion() + '_' + "min.diff");
	} */

	/* private static List<Line> convertToLines(List<String> list) {
		List<Line> result = new ArrayList<Line>(list.size());
		for (int i = 0; i < list.size(); ++i) {
			Line line = new Line(list.get(i), i);
			result.add(line);
		}
		return result; */
	} 
}