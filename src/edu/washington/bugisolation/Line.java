package edu.washington.bugisolation;

public class Line {
	private String content;
	private int lineNumber;
	
	public Line(String content, int lineNumber) {
		this.content = content;
		this.lineNumber = lineNumber;
	}
	
	@Override
	public String toString() {
		return content;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (!(obj instanceof Line)) {
			return false;
		}
		Line other = (Line) obj;
		return this.lineNumber == other.lineNumber && this.content.equals(other.content);
	}
	
	@Override
	public int hashCode() {
		return content.hashCode() * 13 + lineNumber;
	}
}
