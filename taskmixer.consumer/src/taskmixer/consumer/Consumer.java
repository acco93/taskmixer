package taskmixer.consumer;

import taskmixer.consumer.daemon.ControlDaemon;
import taskmixer.consumer.daemon.TaskDaemon;

public class Consumer {

	
	public Consumer(String username, String password, String ip, int workers) {
		
		new TaskDaemon(username, password, ip, workers);	
		new ControlDaemon(username, password, ip);
		
	}

	
}
