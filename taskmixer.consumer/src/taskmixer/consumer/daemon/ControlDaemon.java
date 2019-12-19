package taskmixer.consumer.daemon;

import java.net.InetAddress;
import java.net.UnknownHostException;

import taskmixer.core.log.Logger;
import taskmixer.core.networking.factory.NetworkFactory;
import taskmixer.core.sharedknowledge.R;

public class ControlDaemon {

	public ControlDaemon(String username, String password, String ip) {

		Logger.getInstance().info("started");
		
		int threadsNum = 1;

		CommandExecutor commandExecutor = new CommandExecutor(threadsNum);

		try {
			
			InetAddress inetAddress = InetAddress.getLocalHost();

			String uniqueQueue = R.CTRL_QUEUE_PREFIX + inetAddress.getHostAddress();

			NetworkFactory.registerOnQueue(username, password, ip, uniqueQueue, threadsNum,
					(connection, channel, delivery) -> {

						commandExecutor.process(connection, channel, delivery);

					});

		} catch (UnknownHostException e) {

			Logger.getInstance().error(e.getMessage());

		}

		NetworkFactory.registerOnBroadcast(username, password, ip, R.CTRL_BROADCAST, (connection, channel, body) -> {

			commandExecutor.process(connection, channel, body);

		});

	}

}
