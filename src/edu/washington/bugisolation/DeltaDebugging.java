package edu.washington.bugisolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import difflib.*;
import edu.washington.bugisolation.DDInput.Type;
import edu.washington.bugisolation.util.Operations;

/**
 * The DeltaDebugging class allow the user to utilize a delta debugging algorithm on a
 * List of circumstances.
 * 
 * @author dpang
 *
 */
public class DeltaDebugging {

    public static final int FAIL       = -1;
    public static final int PASS       = +1;
    public static final int UNRESOLVED = 0;
    
	private ProjectInfo projectInfo;
	private Project project;

	public enum Granularity {
	    LINEAR, QUADRATIC
	}
	
	/**
	 * Constructs a new DeltaDebugging object.
	 * 
	 * @param proj		a ProjectInfo, contains the information of the current project
	 * @param d4j		a Defects4J, allows DeltaDebugging to execute Defects4J commands
	 * @param git		a Git, allows DeltaDeugging to execute git commands
	 */
    public DeltaDebugging(ProjectInfo projectInfo, Project project) {
    	this.projectInfo = projectInfo;
    	this.project = project;
    }
    
    /**
     * Splits a list in a number of sublists.
     * 
     * @param <T>
     * @param list			a List of Lists that are the subsets
     * @param numSubsets	an int, the number of subsets that the list will be split into
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

    private static <T> List<T> minusIndices(List<T> circumstances, int stop1, int stop2) {
        List<T> ret = new ArrayList<T>();
        ret.addAll(circumstances.subList(0, stop1));
        ret.addAll(circumstances.subList(stop2, circumstances.size()));
        return ret;
    }
    
    /**
     * Generic test method.
     * 
     * @param circumstances     a List, a configuration that will be tested
     * @return                  an int, denoting whether or not the configuration passed the test	
     */
    @SuppressWarnings("unchecked")
    private <T> int test(DDInput<T> input) {
    	if (input.getKind() == Type.DELTA) {
    	    return testDeltas((List<Delta<String>>) input.getCircumstances());
    	}
    	if (input.getKind() == Type.CHUNK) {
    	    return testChunk (
    	            ((ChunkInput) input).getCircumstances()
    	            , ((ChunkInput) input).isOriginalChunk()
    	            , ((ChunkInput) input).getOtherDeltas()
    	            , ((ChunkInput) input).getOtherChunk()
    	            , (((ChunkInput) input).getChunkStart()));
    	}
    	return UNRESOLVED;
    }
    
    
    /**
     * Returns whether or not a certain configuration of Deltas pass a test.
     * 
     * @param config	a List of Deltas, contains the deltas of a given patch
     * @return			an int, denoting whether or not the configuration passed the test
     */
    private int testDeltas(List<Delta<String>> config) {
    	System.out.println("Testing " + config.size() + " deltas");
	   
    	boolean result = false;
    	
    	if (!config.isEmpty()) {
    		
	    	/* generate patch from deltas */
	    	Patch<String> patch = new Patch<String>();
	    	for (Delta<String> delta : config) {
	    		patch.addDelta(delta);
	    	}
	    	
	    	/* generate unified diff */
	    	List<String> diffLines;
	    	/* if (projectInfo.isFixedToBuggy()) {
	    		diffLines = DiffUtils.generateUnifiedDiff (
	    				projectInfo.getDiffPathA()
	    				, projectInfo.getDiffPathB()
	    				, projectInfo.getFixedFile()
	    				, patch
	    				, 3);
	    	} else {
	    		diffLines = DiffUtils.generateUnifiedDiff (
	    				projectInfo.getDiffPathA()
	    				, projectInfo.getDiffPathB()
	    				, projectInfo.getBuggyFile()
	    				, patch
	    				, 3);
	    	} */
	    	
            diffLines = DiffUtils.generateUnifiedDiff (
                    projectInfo.getDiffPathA()
                    , projectInfo.getDiffPathB()
                    , projectInfo.getModifiedFile()
                    , patch
                    , 3);
	    	
	    	/* save the unified diff */
	    	Operations.linesToFile (
	    			diffLines
	    			, ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff");
	    	
	    	/* apply the patch */
	    	result = project.applyPatch() != 0;
    	}
    	
    	/* if the patch is applicable */
    	if (!result) {
    		
    		/* test if the project compiles */
    		result = project.compileModified() != 0;
    		
    		/* if the project compiles */
	    	if (!result) {
	    		
	    		/* run the necessary tests */
	    		project.test();
	    		List<String> failingTests = project.getFailingTests();
	    		
	    		/* there are two different passing conditions depending on which
	    		 * direction the delta debugging algorithm proceeds */
	    		if (projectInfo.isFixedToBuggy()) {
		    		result = !projectInfo.getTriggerTests().equals(failingTests);
	    		} else {
	    			result = !failingTests.isEmpty();
	    		}
	    	}
    	}
    	
    	/* reset the project back to its checkout state */
    	project.reset();
    	System.out.println("Configuration returned " + result);
    	System.out.println();
		return Operations.boolToInt(result);
    }
    
    /**
     * Returns whether or not a certain configuration of Strings pass a test.
     * 
     * @param config	a List of Strings, contains the lines of a given chunk
     * @return			an int, denoting whether or not the configuration passed the test
     */
    public int testChunk(List<String> chunkLines, boolean originalChunk, List<Delta<String>> otherDeltas, Chunk<String> otherChunk, int chunkStart) {
    	System.out.println("Testing chunk of size " + chunkLines.size());
    	
    	boolean result = false;
    	if (!chunkLines.isEmpty()) {
    		
    		/* create the new delta */
    		Delta<String> minDelta;
    		if (originalChunk) {
    			minDelta = new ChangeDelta<String>(new Chunk<String>(chunkStart, chunkLines), otherChunk);
    		} else {
    			minDelta = new ChangeDelta<String>(otherChunk, new Chunk<String>(chunkStart, chunkLines));
    		}
    		
	    	/* need the minimized list of deltas */
	    	Patch<String> patch = new Patch<String>();
	    	
	    	for (Delta<String> delta : otherDeltas) {
	    		patch.addDelta(delta);
	    	}
	    	
	    	patch.addDelta(minDelta);
	    	
	    	/* generate unified diff */
	    	List<String> diffLines;
	    	/* if (projectInfo.isFixedToBuggy()) {
	    		diffLines = DiffUtils.generateUnifiedDiff (
	    				projectInfo.getDiffPathA()
	    				, projectInfo.getDiffPathB()
	    				, projectInfo.getFixedFile()
	    				, patch
	    				, 3);
	    	} else {
	    		diffLines = DiffUtils.generateUnifiedDiff (
	    				projectInfo.getDiffPathA()
	    				, projectInfo.getDiffPathB()
	    				, projectInfo.getBuggyFile()
	    				, patch
	    				, 3);
	    	} */
            diffLines = DiffUtils.generateUnifiedDiff (
                    projectInfo.getDiffPathA()
                    , projectInfo.getDiffPathB()
                    , projectInfo.getModifiedFile()
                    , patch
                    , 3);
            
	    	/* save the unified diff */
	    	Operations.linesToFile (
	    			diffLines
	    			, ProjectInfo.WORKSPACE + projectInfo.getFullProjectName() + ".diff");
	    	
	    	/* apply the patch */
	    	result = project.applyPatch() != 0;
    	}
    	
    	/* if the patch is applicable */
    	if (!result) {
    		
    		/* test if the project compiles */
    		result = project.compileModified() != 0;
    		
    		/* if the project compiles */
	    	if (!result) {
	    		
	    		/* run the necessary tests */
	    		project.test();
	    		List<String> failingTests = project.getFailingTests();
	    		
	    		/* there are two different passing conditions depending on which
	    		 * direction the delta debugging algorithm proceeds */
	    		if (projectInfo.isFixedToBuggy()) {
		    		result = !projectInfo.getTriggerTests().equals(failingTests);
	    		} else {
	    			result = !failingTests.isEmpty();
	    		}
	    	}
    	}
    	
    	/* reset the project back to its checkout state */
    	project.reset();
    	System.out.println("Configuration returned " + result);
    	System.out.println();
		return Operations.boolToInt(result);
    }
    
    /**
     * Minimizes a list of failing input.
     * @param <T>
     * 
     * @param input	    an input that is to be minimized
     * @return          a List of minimal circumstances that pass test
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> ddmin(DDInput<T> input, Granularity gran) {
        
        System.out.println("***** Running ddmin *****");
        System.out.println(input.getCircumstances().size() + " initial elements");

        List<T> circumstances = input.getCircumstances();
       
        if (input.getKind() == Type.DELTA) {
            assert test(new DeltaInput(new ArrayList<Delta<String>>())) == PASS;
        }
        if (input.getKind() == Type.CHUNK) {
            assert test (
                    new ChunkInput (
                            new ArrayList<String>()
                            , ((ChunkInput) input).isOriginalChunk()
                            , ((ChunkInput) input).getOtherDeltas()
                            , ((ChunkInput) input).getOtherChunk()
                            , ((ChunkInput) input).getChunkStart())) == PASS;
                    
        }
        assert test(input) == FAIL;
        
        int n = 2;
        
        Map<Integer, Integer> indices = new HashMap<Integer, Integer>();
        
        while (circumstances.size() >= 2) {
            
            List<List<T>> subsets = split(circumstances, n);
            assert subsets.size() == n;

            System.out.println("ddmin: testing subsets");

            boolean some_complement_is_failing = false;
            
            loop: for (int i = 0; i < n; i++) {
                         
                int stop1 = i * (circumstances.size() / n);
                int stop2 = stop1 + (circumstances.size() / n);
                
                System.out.println("stop1: " + stop1 + " stop2: " + stop2);
                
                /* checks if the indices have been tested already */
                if (indices.containsKey(stop1) && indices.get(stop1).equals(stop2)) {
                    System.out.println("FOUND REPEATED INDICES");
                    continue loop;
                } else {
                    indices.put(stop1, stop2);
                }
                
                DDInput<T> complement = null;
                
                if (input.getKind() == Type.DELTA) {
                    complement = (DDInput<T>) new DeltaInput((List<Delta<String>>) minusIndices(circumstances, stop1, stop2));
                }
                if (input.getKind() == Type.CHUNK) {
                    complement = (DDInput<T>) new ChunkInput (
                            (List<String>) minusIndices(circumstances, stop1, stop2)
                            , ((ChunkInput) input).isOriginalChunk()
                            , ((ChunkInput) input).getOtherDeltas()
                            , ((ChunkInput) input).getOtherChunk()
                            , ((ChunkInput) input).getChunkStart());
                }

                if (test(complement) == FAIL) {
                    circumstances = complement.getCircumstances();
                    indices.clear();
                    n = Math.max(n - 1, 2);
                    some_complement_is_failing = true;
                    break;
                }
            }

            if (!some_complement_is_failing) {
                if (n == circumstances.size()) {
                    break;
                }

                System.out.println("ddmin: increasing granularity");
                System.out.println("granularity is now " + n);
                int newGran = circumstances.size();
                if (gran == Granularity.LINEAR) {
                    newGran = ++n;
                }
                if (gran == Granularity.QUADRATIC) {
                    newGran = n * 2;
                }
                n = Math.min(newGran, circumstances.size());
            }
        }
        
        System.out.println(circumstances.size() + " resulting elements");
        return circumstances;
    }
    
    /**
     * Minimizes each individual delta that is passed in.
     * 
     * @param minDeltas		a List of Deltas, each will be have its chunks analyzed
     * 						and minimized
     * @return				a List of Deltas, each Delta containing the minimal
     * 						chunk lines to fail testChunk
     */
    public List<Delta<String>> minimizeChunks(List<Delta<String>> minDeltas, Granularity gran) {
	    List<Delta<String>> minChunkDeltas = new ArrayList<Delta<String>>();
	    
	    for (Delta<String> delta : minDeltas) {
	    	
	    	/* create a new list of the other deltas */
	    	List<Delta<String>> otherDeltas = new LinkedList<Delta<String>>(minDeltas);
	    	otherDeltas.remove(delta);
	    	
	    	Delta<String> minChunkDelta;
	    	
	    	List<String> minOriginalChunkLines = new ArrayList<String>();
	    	List<String> minRevisedChunkLines = new ArrayList<String>();
	    	
	    	if (!delta.getOriginal().getLines().isEmpty()) {
	    	    System.out.println("YAYYYYY minimizing original chunk");
		    	minOriginalChunkLines = ddmin (
	                    new ChunkInput (
	                            delta.getOriginal().getLines()
	                            , true
	                            , otherDeltas
	                            , delta.getRevised()
	                            , delta.getOriginal().getPosition())
	                            , gran);
	    	}
	    	if (!delta.getRevised().getLines().isEmpty()) {
	            System.out.println("YAYYYYY minimizing revised chunk");
                minRevisedChunkLines = ddmin (
                        new ChunkInput (
                                delta.getRevised().getLines()
                                , false
                                , otherDeltas
                                , delta.getOriginal()
                                , delta.getRevised().getPosition())
                                , gran);
	    	}
	    	minChunkDelta = 
	    			new ChangeDelta<String> (
	    					new Chunk<String> (
	    							delta.getOriginal().getPosition(), minOriginalChunkLines)
	    					, new Chunk<String> (
	    							delta.getRevised().getPosition(), minRevisedChunkLines));
	    	
	    	minChunkDeltas.add(minChunkDelta);
	    }
	    return minChunkDeltas;
    }
}


