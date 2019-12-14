package taskmixer.producer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import taskmixer.common.message.Message;
import taskmixer.common.sharedknowledge.R;


@Command(name = "txp", mixinStandardHelpOptions = true, version = "txp 1.0", description = "Sends a command to a RabbitMQ queue.")
public class Producer implements Callable<Integer>  {

	@Option(names = {"-u", "--username"}, description = "RabbitMQ username", required = true)
	private String username;
	
	@Option(names = {"-p", "--password"}, description = "RabbitMQ password", required = true)
	private String password;
	
	@Option(names = {"-i", "--ip"}, description = "RabbitMQ IP address", required = true)
	private String ip;
	
	@Option(names = {"-c", "--command"}, description = "Command to send", required = true)
	private String command;
		
	@Option(names = {"-w", "--wait"}, description = "Wait for reply, if any") 
	boolean waitForReply;
	
	public static void main(String... args) {
        new CommandLine(new Producer()).execute(args);
    }

	@Override
	public Integer call() throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		
		factory.setHost(ip);			
		factory.setUsername(username);
		factory.setPassword(password);					
	   
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
        channel.queueDeclare(R.TASK_QUEUE_NAME, true, false, false, null);

        String replyQueue = channel.queueDeclare().getQueue();
        
        Message message = new Message(command, waitForReply ? replyQueue : "");
        
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(message);
        
        channel.basicPublish("", R.TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, json.getBytes("UTF-8"));
        
        if (waitForReply) {
        		
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String result = new String(delivery.getBody(), "UTF-8");
                System.out.println(result);
                
                try {
                	channel.queueDelete(replyQueue);
					channel.close();
					connection.close();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            };
            channel.basicConsume(replyQueue, true, deliverCallback, consumerTag -> { });
        	
        } else {
        	
        	channel.close();
			connection.close();

        	
        }
                
				
		return 0;
	}

}
