package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.bugisolation.DDInput.InputType;
import edu.washington.bugisolation.util.Operations;
import edu.washington.cs.dericp.diffutils.Diff;
import edu.washington.cs.dericp.diffutils.Hunk;
import edu.washington.cs.dericp.diffutils.UnifiedDiff;

/**
 * The DeltaDebugging class allows for the minimization of a diff/patch through the process
 * of the delta debugging algorithm.
 * 
 * @author Deric Hua Pang
 *
 */
public class DeltaDebugging {

    public static final int FAIL       = -1;
    public static final int PASS       = +1;
    public static final int UNRESOLVED = 0;
    
	private ProjectInfo projectInfo;
	private Project project;

	public enum Granularity {
	    LINEAR, EXPONENTIAL
	}
	
	/**
	 * Constructs a new DeltaDebugging object.
	 * 
	 * @param projectInfo  contains the information of the current project
	 * @param project      allows DeltaDebugging to manipulate the project
	 */
    public DeltaDebugging(ProjectInfo projectInfo, Project project) {
    	this.projectInfo = projectInfo;
    	this.project = project;
    }
    
    /**
     * Splits a list in a number of sublists.
     * 
     * @param <T>           the type of list that is being split
     * @param list			a list of elements
     * @param numSubsets	the number of subsets that the list will be split into
     * @return
     */
    private static <T> List<List<T>> split(List<T> list, int numSubsets) {
        List<List<T>> subSets = new LinkedList<List<T>>();
        int start = 0;

        for (int i = 0; i < numSubsets; i++) {
            List<T> subSet = 
                list.subList(start, start + (list.size() - start) / (numSubsets - i));
            subSets.add(subSet);
            start += subSet.size();
        }

        return subSets;
    }
    
    /**
     * Substracts a group of elements from a list.
     * 
     * @param circumstances     the list to be modified
     * @param stop1             the start index of the section to be removed, inclusive
     * @param stop2             the end index of the section to be removed, exclusive
     * @return                  a list that excludes the elements between the start and stop
     *                          indices, inclusive and exclusive, respectively
     */
    private static <T> List<T> minusIndices(List<T> circumstances, int stop1, int stop2) {
        List<T> ret = new ArrayList<T>();
        ret.addAll(circumstances.subList(0, stop1));
        ret.addAll(circumstances.subList(stop2, circumstances.size()));
        return ret;
    }   
    
    /**
     * A test that determines whether a given input passes or fails.
     * 
     * @param input     an input on which the test will be run
     * @return          -1 if the input fails, +1 if the input passes, and 0
     *                  if the test is inconclusive
     */
    private int test(DDInput input) {
    	System.out.println("Testing " + input.getCircumstances().size() + " elements");
    	
    	boolean result = false;
    	
    	if (!input.getCircumstances().isEmpty()) {
    		
	    	// remove the irrelevant hunks
    	    input.removeElements();
    	    
    	    // generate the patch
    	    input.getUnifiedDiff().exportUnifiedDiff(ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff");
    	    
	    	result = project.applyPatch() != 0;
    	}
    	
    	// if the patch is applicable
    	if (!result) {
    		
    		// test if the project compiles
    		result = project.compileModified() != 0;
    		
    		// if the project compiles
	    	if (!result) {
	    		
	    		// run the necessary tests
	    		project.test();
	    		List<String> failingTests = project.getFailingTests();
	    		
	    		// there are two different passing conditions depending on which
	    		// direction the delta debugging algorithm runs
	    		if (projectInfo.isFixedToBuggy()) {
		    		result = !projectInfo.getTriggerTests().equals(failingTests);
	    		} else {
	    			result = !failingTests.isEmpty();
	    		}
	    	}
    	}
    	
    	// reset the project back to its checkout state
    	project.reset();
    	System.out.println("Configuration returned " + result);
    	System.out.println();
		return Operations.boolToInt(result);
    }
    
    /**
     * Minimizes a list of failing input.
     * 
     * @param input     the initial input that causes test() to fail
     * @param gran      the granularity increase of the ddmin algorithm
     * @return          the relevant circumstances of the circumstances that
     *                  was passed in with input
     */
    public List<Integer> ddmin(DDInput input, Granularity gran) {
        
        System.out.println("***** Running ddmin *****");
        System.out.println("minimizing " + input.getInputType());
        System.out.println(input.getCircumstances().size() + " initial elements");
        
        List<Integer> circumstances = input.getCircumstances();
       
        if (input.getInputType() == InputType.DIFFS) {
            assert test(new DiffsInput(input.getUnifiedDiff(), new ArrayList<Integer>())) == PASS;
        }
        if (input.getInputType() == InputType.HUNKS) {
            assert test(new HunksInput(input.getUnifiedDiff(), new ArrayList<Integer>(), input.getDiffNumber())) == PASS;
        }
        if (input.getInputType() == InputType.LINES) {
            assert test(new LinesInput(input.getUnifiedDiff(), new ArrayList<Integer>(), input.getDiffNumber(), input.getHunkNumber())) == PASS;        
        }
        
        assert test(input) == FAIL;
        
        int granularity = 2;
        
        Map<Integer, Integer> indices = new HashMap<Integer, Integer>();
        
        while (circumstances.size() >= 2) {
            
            List<List<Integer>> subsets = split(circumstances, granularity);
            assert subsets.size() == granularity;

            System.out.println("ddmin: testing subsets");

            boolean some_complement_is_failing = false;
            
            loop: for (int i = 0; i < granularity; i++) {
                         
                int stop1 = i * (circumstances.size() / granularity);
                int stop2 = stop1 + (circumstances.size() / granularity);
                
                System.out.println("stop1: " + stop1 + " stop2: " + stop2);
                
                /* checks if the indices have been tested already */
                if (indices.containsKey(stop1) && indices.get(stop1).equals(stop2)) {
                    System.out.println("FOUND REPEATED INDICES");
                    continue loop;
                } else {
                    indices.put(stop1, stop2);
                }
                
                DDInput complement = null;
                
                if (input.getInputType() == InputType.DIFFS) {
                    complement = new DiffsInput(input.getUnifiedDiff(), minusIndices(circumstances, stop1, stop2));
                }
                if (input.getInputType() == InputType.HUNKS) {
                    complement = new HunksInput(input.getUnifiedDiff(), minusIndices(circumstances, stop1, stop2), input.getDiffNumber());
                }
                if (input.getInputType() == InputType.LINES) {
                    complement = new LinesInput(input.getUnifiedDiff(), minusIndices(circumstances, stop1, stop2), input.getDiffNumber(), input.getHunkNumber());
                }

                if (test(complement) == FAIL) {
                    circumstances = complement.getCircumstances();
                    indices.clear();
                    granularity = Math.max(granularity - 1, 2);
                    some_complement_is_failing = true;
                    break;
                }
            }

            if (!some_complement_is_failing) {
                if (granularity == circumstances.size()) {
                    break;
                }

                System.out.println("ddmin: increasing granularity");
                int newGran = circumstances.size();
                if (gran == Granularity.LINEAR) {
                    newGran = ++granularity;
                }
                if (gran == Granularity.EXPONENTIAL) {
                    newGran = granularity * 2;
                }
                granularity = Math.min(newGran, circumstances.size());
                System.out.println("granularity is now " + granularity);
            }
        }
        
        System.out.println(circumstances.size() + " resulting elements");
        return circumstances;
    }
    
    /**
     * Minimizes the diffs of a unified diff.
     * 
     * @param unifiedDiff   the initial unified diff
     * @return              a unified diff minimized by delta debugging to only include
     *                      the diffs that cause test() to fail
     */
    public UnifiedDiff minimizeDiffs(UnifiedDiff unifiedDiff) {
        System.out.println("minimizing diffs");
        if (unifiedDiff.getDiffs().size() > 1) {
            List<Integer> circumstances = new ArrayList<Integer>();
            for (int i = 0; i < unifiedDiff.getDiffs().size(); ++i) {
                circumstances.add(i);
            }
            
            DDInput minimizedPatch = new DiffsInput(unifiedDiff, circumstances);

            minimizedPatch = new DiffsInput(unifiedDiff, ddmin(minimizedPatch, Granularity.LINEAR));
            
            minimizedPatch.removeElements();
            minimizedPatch.getUnifiedDiff().exportUnifiedDiff(ProjectInfo.WORKSPACE + "defects4j-data/" + projectInfo.getFullProjectName() + "_" + Boolean.toString(projectInfo.isFixedToBuggy()) + "_minDiffs.diff");
            return minimizedPatch.getUnifiedDiff();
        } else {
            System.out.println("only one diff, no need to minimize");
            unifiedDiff.exportUnifiedDiff(ProjectInfo.WORKSPACE + "defects4j-data/" + projectInfo.getFullProjectName() + "_" + Boolean.toString(projectInfo.isFixedToBuggy()) + "_minDiffs.diff");
            return unifiedDiff;
        }
    }
    
    /**
     * Minimizes the hunks of each diff in a unified diff.
     * 
     * @param unifiedDiff   the initial unified diff
     * @return              a unified diff where each individual diff only includes
     *                      the hunks that cause test() to fail
     */
    public UnifiedDiff minimizeHunks(UnifiedDiff unifiedDiff) {
        System.out.println("minimizing hunks");
        
        // priming the loop
        DDInput minimizedPatch = new HunksInput(unifiedDiff);
        
        for (int i = 0; i < unifiedDiff.getDiffs().size(); i++) {
            Diff currentDiff = unifiedDiff.getDiffs().get(i);
            
            // if the current diff has more than one hunk
            if (currentDiff.getHunks().size() > 1) {
                if (currentDiff.getHunks().get(i) != null) {
                    List<Integer> circumstances = new ArrayList<Integer>();
                    for (int j = 0; j < currentDiff.getHunks().size(); ++j) {
                        circumstances.add(j);
                    }
                    minimizedPatch.setCircumstances(circumstances, i, -1);
                    DDInput hunksInput = new HunksInput(unifiedDiff, ddmin(minimizedPatch, Granularity.EXPONENTIAL), i);
                    hunksInput.removeElements();
                    minimizedPatch = hunksInput;
                }
            } else {
                System.out.println("only one hunk, no need to minimize");
            }
        }
        minimizedPatch.getUnifiedDiff().exportUnifiedDiff(ProjectInfo.WORKSPACE + "defects4j-data/" + projectInfo.getFullProjectName() + "_" + Boolean.toString(projectInfo.isFixedToBuggy()) + "_minHunks.diff");
        return minimizedPatch.getUnifiedDiff();
    }
    
    /**
     * Minimizes the lines of the hunks of each diff.
     * 
     * @param unifiedDiff   the initial unified diff
     * @return              a unified diff where each diff's hunks only include lines that
     *                      cause test() to fail
     */
    public UnifiedDiff minimizeLines(UnifiedDiff unifiedDiff) {
        System.out.println("minimizing lines");
        
        // priming the loop
        DDInput minimizedPatch = new LinesInput(unifiedDiff);
        
        for (int i = 0; i < unifiedDiff.getDiffs().size(); ++i) {    
            Diff currentDiff = unifiedDiff.getDiffs().get(i);
            
            // j denotes the hunk number
            for (int j = 0; j < currentDiff.getHunks().size(); j++) {
                System.out.println("minimzing diff " + i + " and hunk " + j);
                Hunk currentHunk = currentDiff.getHunks().get(j);
                
                if (currentHunk == null) {
                    System.out.println("this hunk has been removed");
                } else {
                    List<Integer> circumstances = new ArrayList<Integer>();
                    for (int k = 0; k < currentHunk.getModifiedLines().size(); ++k) {
                        circumstances.add(k);
                    }
                    minimizedPatch.setCircumstances(circumstances, i, j);
                    DDInput linesInput = new LinesInput(unifiedDiff, ddmin(minimizedPatch, Granularity.LINEAR), i, j);
                    linesInput.removeElements();
                    minimizedPatch = linesInput;
                }
            }
        }
        minimizedPatch.getUnifiedDiff().exportUnifiedDiff(ProjectInfo.WORKSPACE + "defects4j-data/" + projectInfo.getFullProjectName() + "_" + Boolean.toString(projectInfo.isFixedToBuggy()) + "_minLines.diff");
        return minimizedPatch.getUnifiedDiff();
    }
}


