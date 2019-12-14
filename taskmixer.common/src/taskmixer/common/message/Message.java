package taskmixer.common.message;

public class Message {

	private String message;
	private String replyQueue;
	
	public Message(String message, String replyQueue) {
		
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
