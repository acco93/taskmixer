package taskmixer.consumer.daemon;

import taskmixer.common.networking.factory.NetworkFactory;
import taskmixer.common.sharedknowledge.R;

public class TaskDaemon {

	public TaskDaemon(String username, String password, String ip, int workers) {

		CommandExecutor commandExecutor = new CommandExecutor(workers);

		NetworkFactory.registerOnQueue(username, password, ip, R.ROUND_ROBIN_QUEUE, workers,
				(connection, channel, delivery) -> {

					commandExecutor.process(connection, channel, delivery);

				});

		NetworkFactory.registerOnBroadcast(username, password, ip, R.TRANSIENT_BROADCAST, 
				(connection, channel, body) -> {
					
					commandExecutor.process(connection, channel, body);
					
				});
		
	}

}