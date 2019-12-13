package taskmixer.consumer;

import taskmixer.common.concept.ActiveEntity;
import taskmixer.consumer.daemon.TasksReceiver;

public class Consumer {

	
	private TasksReceiver tasksReceiver;

	public Consumer(String username, String password, String ip) {
		
		tasksReceiver = new TasksReceiver(username, password, ip);
		tasksReceiver.start();		
		
	}

	
}
