package edu.washington.bugisolation;

import java.util.List;

import difflib.Chunk;
import difflib.Delta;

public class ChunkInput implements DDInput<String> {
    
    private List<String> chunkLines;
    private boolean originalChunk;
    private List<Delta<String>> otherDeltas;
    Chunk<String> otherChunk;
    int chunkStart;
    
    public ChunkInput(List<String> chunkLines, boolean originalChunk, List<Delta<String>> otherDeltas, Chunk<String> otherChunk, int chunkStart) {
        this.chunkLines = chunkLines;
        this.originalChunk = originalChunk;
        this.otherDeltas = otherDeltas;
        this.otherChunk = otherChunk;
        this.chunkStart = chunkStart;
    }
    
    @Override
    public Type getKind() {
        return Type.CHUNK;
    }
    
    @Override
    public List<String> getCircumstances() {
        return chunkLines;
    }
    
    public boolean isOriginalChunk() {
        return originalChunk;
    }
    
    public List<Delta<String>> getOtherDeltas() {
        return otherDeltas;
    }
    
    public Chunk<String> getOtherChunk() {
        return otherChunk;
    }
    
    public int getChunkStart() {
        return chunkStart;
    }
}
