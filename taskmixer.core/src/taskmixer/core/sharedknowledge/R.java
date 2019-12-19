package taskmixer.core.sharedknowledge;

public class R {

	public static final String ROUND_ROBIN_QUEUE = "task_queue";
	public static final String TRANSIENT_BROADCAST = "transient_broadcast";
	
	
	/**
	 * CTRL queues can be used from consumers to receive control commands.
	 * Commands from CTRL queues are executed concurrently with commands from TASK queues.
	 * Commands from CTRL queues are executed in a FIFO order with commands from CTRL queues.
	 */
	
	/*
	 * Broadcast used to send and receive control commands
	 */
	public static final String CTRL_BROADCAST = "ctrl_broadcast";
	
	/*
	 * Control queue prefix. Each consumer will create a unique queue by appending its IP address to this prefix.
	 */
	public static final String CTRL_QUEUE_PREFIX = "ctrl_queue";
}
