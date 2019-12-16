package taskmixer.common.message;

public class StringCommand {

	private String message;
	private String replyQueue;
	
	public StringCommand(String message, String replyQueue) {
		
		this.message = message;
		this.replyQueue = replyQueue;
		
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getReplyQueue() {
		return replyQueue;
	}
	
	public boolean isProducerWaitingForReply () {
		return !replyQueue.isEmpty();
	}
	
}
