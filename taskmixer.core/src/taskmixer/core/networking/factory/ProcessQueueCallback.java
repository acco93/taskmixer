package taskmixer.core.networking.factory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;

@FunctionalInterface
public interface ProcessQueueCallback {

	void handle(Connection connection, Channel channel, Delivery delivery);

	
}
