package taskmixer.common.networking.factory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Envelope;

import taskmixer.common.log.Logger;

/**
 * Simple factory to listen for and send messages.
 * @author acco
 *
 */
public class NetworkFactory {
	
	public static void registerOnQueue(String username, String password, String ip, String queueName, int prefetchCount, ProcessQueueCallback processMessageCallback) {
		
		try {
			
			ConnectionFactory factory = new ConnectionFactory();
			
			factory.setHost(ip);			
			factory.setUsername(username);
			factory.setPassword(password);					
		    
			final Connection connection = factory.newConnection();
		    final Channel channel = connection.createChannel();

			channel.queueDeclare(queueName, true, false, false, null);

		    channel.basicQos(prefetchCount);

		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		    	
				processMessageCallback.handle(connection, channel, delivery);

		    };
		    
		    channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
			

		} catch (IOException | TimeoutException e) {

			Logger.getInstance().error(e.getMessage());
			
		}
		
	}
	
	public static void registerOnBroadcast(String username, String password, String ip, String broadcastName, ProcessBroadcastCallback processMessageCallback) {
		
		try {
			
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(ip);

			Connection connection = factory.newConnection();
			
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(broadcastName, "fanout");

			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, broadcastName, "");
			
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					
					processMessageCallback.handle(connection, channel, body);
					
				}

			};


			channel.basicConsume(queueName, true, consumer);
			
			
		} catch (IOException | TimeoutException e) {
			
			Logger.getInstance().error(e.getMessage());

		}
		
		
	}
	
}
