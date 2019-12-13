package taskmixer.producer;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import taskmixer.common.log.Logger;
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
		
	
	public static void main(String... args) {
        int exitCode = new CommandLine(new Producer()).execute(args);
        System.exit(exitCode);
    }

	@Override
	public Integer call() throws Exception {

		try {
			
			ConnectionFactory factory = new ConnectionFactory();
		
			factory.setHost(ip);			
			factory.setUsername(username);
			factory.setPassword(password);					
		   
			
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			
	        channel.queueDeclare(R.TASK_QUEUE_NAME, true, false, false, null);
			
	        channel.basicPublish("", R.TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, command.getBytes("UTF-8"));
	       
		} catch (IOException | TimeoutException e) {
			Logger.getInstance().error(e.getMessage());
		}
		
		return 0;
	}

}
