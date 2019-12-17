package taskmixer.consumer.daemon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import taskmixer.common.log.Logger;
import taskmixer.common.message.StringCommand;
import taskmixer.common.sharedknowledge.R;

public class BroadcastReceiver {


	public BroadcastReceiver(String username, String password, String ip) {

		try {
			
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(ip);

			Connection connection = factory.newConnection();
			
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(R.TRANSIENT_BROADCAST, "fanout");

			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, R.TRANSIENT_BROADCAST, "");
			
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
					
					String message = new String(body, "UTF-8");
					Gson gson = new GsonBuilder().create();
					StringCommand stringCommand = gson.fromJson(message, StringCommand.class);
					
					TaskExecutor.process(stringCommand, channel);
					
				}

			};


			channel.basicConsume(queueName, true, consumer);
			
			
		} catch (IOException | TimeoutException e) {
			
			Logger.getInstance().error(e.getMessage());

		}
		
	}

}
