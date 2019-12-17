package taskmixer.consumer.daemon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import taskmixer.common.log.Logger;
import taskmixer.common.message.StringCommand;
import taskmixer.common.sharedknowledge.R;

public class TasksReceiver {

	public TasksReceiver(String username, String password, String ip) {

		Logger.getInstance().info("started");

		this.setupRabbitMQ(username, password, ip);
	}

	private void setupRabbitMQ(String username, String password, String momIP) {

		
		try {
		
			ConnectionFactory factory = new ConnectionFactory();
			
			factory.setHost(momIP);			
			factory.setUsername(username);
			factory.setPassword(password);					
		    
			final Connection connection = factory.newConnection();
		    final Channel channel = connection.createChannel();

			channel.queueDeclare(R.ROUND_ROBIN_QUEUE, true, false, false, null);
			Logger.getInstance().info("waiting for incoming tasks. To exit press CTRL+C");

		    channel.basicQos(1);

		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		    	
		    	String string = new String(delivery.getBody(), "UTF-8");

				Gson gson = new GsonBuilder().create();
				StringCommand message = gson.fromJson(string, StringCommand.class);

				TaskExecutor.process(message, channel);
				
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				

		    };
		    
		    channel.basicConsume(R.ROUND_ROBIN_QUEUE, false, deliverCallback, consumerTag -> { });
			

		} catch (IOException | TimeoutException e) {

			Logger.getInstance().error(e.getMessage());
			
		}
		
		
	}

}
