package taskmixer.consumer.daemon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import taskmixer.common.concept.ActiveEntity;
import taskmixer.common.log.Logger;
import taskmixer.common.message.StandardOutputLine;
import taskmixer.common.message.StringCommand;
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
		    	
		    	String string = new String(delivery.getBody(), "UTF-8");

				Gson gson = new GsonBuilder().create();
				StringCommand message = gson.fromJson(string, StringCommand.class);

		   
		        Logger.getInstance().info("received '" + message.getMessage() + "'");
		        try {
		        	
		        	String[] commands = { "/bin/bash", "-c", message.getMessage() };		        	
		            Process process = Runtime.getRuntime().exec(commands);
		           
		            
			        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			        String line;
					while ((line = reader.readLine()) != null) {
						
						if(message.isProducerWaitingForReply()) {
							
							StandardOutputLine messageLine = new StandardOutputLine(line, false);
							String json = gson.toJson(messageLine);
							channel.basicPublish("", message.getReplyQueue(), MessageProperties.PERSISTENT_TEXT_PLAIN, json.toString().getBytes("UTF-8"));
							
						}
						
					}
			        process.waitFor();
			        
			        channel.basicPublish("", message.getReplyQueue(), MessageProperties.PERSISTENT_TEXT_PLAIN, gson.toJson(new StandardOutputLine("", true)).toString().getBytes("UTF-8"));


		        	
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
