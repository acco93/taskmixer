package taskmixer.core.networking.factory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@FunctionalInterface
public interface ProcessBroadcastCallback {

	void handle(Connection connection, Channel channel, byte[] body);

	
}
