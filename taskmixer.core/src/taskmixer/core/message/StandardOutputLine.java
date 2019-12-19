package taskmixer.core.message;

public class StandardOutputLine {

	private String line;
	private boolean endOfContent;

	public StandardOutputLine(String line, boolean endOfContent) {
		
		this.line = line;
		this.endOfContent = endOfContent;
		
	}

	public String getLine() {
		return line;
	}

	public boolean isEndOfContent() {
		return endOfContent;
	}
	
}
