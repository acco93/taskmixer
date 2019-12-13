package taskmixer.consumer.daemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import taskmixer.common.concept.ActiveEntity;
import taskmixer.common.log.Logger;
import taskmixer.common.sharedknowledge.R;

public class TasksReceiver extends ActiveEntity {

	private String momIP;
	private Channel channel;
	private String username;
	private String password;

	public TasksReceiver(String username, String password, String ip) {

		Logger.getInstance().info("started");
		
		this.username = username;
		this.password = password;
		this.momIP = ip;
	
		
	}

	private void setupRabbitMQ() {

		
		try {
		
			ConnectionFactory factory = new ConnectionFactory();
			
			factory.setHost(momIP);			
			factory.setUsername(username);
			factory.setPassword(password);					
		    
			final Connection connection = factory.newConnection();
		    channel = connection.createChannel();

			channel.queueDeclare(R.TASK_QUEUE_NAME, true, false, false, null);
			Logger.getInstance().info("waiting for incoming tasks. To exit press CTRL+C");

		    channel.basicQos(1);

		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String command = new String(delivery.getBody(), "UTF-8");

		        Logger.getInstance().info("received '" + command + "'");
		        try {
		        	
		        	
		            Process process = Runtime.getRuntime().exec(command);
			        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			        while ((reader.readLine()) != null) {}
			        process.waitFor();
			        
		        	
		        	
		        } catch (Exception e) {
					
		        	Logger.getInstance().error(e.getMessage());
		        	
				} finally {
		        	Logger.getInstance().info("done");
		            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		        }
		    };
		    
		    channel.basicConsume(R.TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
			
			

		} catch (IOException | TimeoutException e) {

			Logger.getInstance().error(e.getMessage());
			
		}
		
		
	}

	@Override
	protected void work() {

		
		this.setupRabbitMQ();

		
		
	}




}
