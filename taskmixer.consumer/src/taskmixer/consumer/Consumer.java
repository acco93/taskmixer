package taskmixer.consumer;

import taskmixer.consumer.daemon.BroadcastReceiver;
import taskmixer.consumer.daemon.TasksReceiver;

public class Consumer {

	
	public Consumer(String username, String password, String ip) {
		
		new TasksReceiver(username, password, ip);
		new BroadcastReceiver(username, password, ip);
		
	}

	
}
