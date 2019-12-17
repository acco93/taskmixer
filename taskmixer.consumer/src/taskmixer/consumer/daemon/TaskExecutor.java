package taskmixer.consumer.daemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import taskmixer.common.log.Logger;
import taskmixer.common.message.StandardOutputLine;
import taskmixer.common.message.StringCommand;

public class TaskExecutor {

	public static void process(StringCommand message, Channel channel) {

        Logger.getInstance().info("received '" + message.getMessage() + "'");
        try {
        	
        	String[] commands = { "/bin/bash", "-c", message.getMessage() };		        	
            Process process = Runtime.getRuntime().exec(commands);
           
			Gson gson = new GsonBuilder().create();
            
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
            
        }
		
		
	}

}
