package taskmixer.consumer.daemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.MessageProperties;

import taskmixer.core.log.Logger;
import taskmixer.core.message.StandardOutputLine;
import taskmixer.core.message.StringCommand;

public class CommandExecutor {

	private ExecutorService executor;


	public CommandExecutor(int parallelTasksNum) {

		this.executor = Executors.newFixedThreadPool(parallelTasksNum);

	}


	public void process(Connection connection, Channel channel, Delivery delivery) {

		try {

			String string = new String(delivery.getBody(), "UTF-8");

			Gson gson = new GsonBuilder().create();
			StringCommand message = gson.fromJson(string, StringCommand.class);

			executor.execute(() -> {

				System.out.println(message.getMessage());
				
				try {
					String[] commands = { "/bin/bash", "-c", message.getMessage() };
					Process process = Runtime.getRuntime().exec(commands);

					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) {

						if (message.isProducerWaitingForReply()) {

							StandardOutputLine messageLine = new StandardOutputLine(line, false);

							String json = gson.toJson(messageLine);

							channel.basicPublish("", message.getReplyQueue(), MessageProperties.PERSISTENT_TEXT_PLAIN,
									json.toString().getBytes("UTF-8"));

						}

					}

					process.waitFor();

					channel.basicPublish("", message.getReplyQueue(), MessageProperties.PERSISTENT_TEXT_PLAIN,
							gson.toJson(new StandardOutputLine("", true)).toString().getBytes("UTF-8"));

					
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					
				} catch (Exception e) {

					Logger.getInstance().error(e.getMessage());

				}

			});

		} catch (Exception e) {

			Logger.getInstance().error(e.getMessage());

		}

	}

	public void process(Connection connection, Channel channel, byte[] body) {
		
		try {
		
		String json = new String(body, "UTF-8");
		Gson gson = new GsonBuilder().create();
		StringCommand message = gson.fromJson(json, StringCommand.class);
		
		
		executor.execute(() -> {

			System.out.println(message.getMessage());
			
			try {
				String[] commands = { "/bin/bash", "-c", message.getMessage() };
				Process process = Runtime.getRuntime().exec(commands);

				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((reader.readLine()) != null) { }

				process.waitFor();
				
			} catch (Exception e) {

				Logger.getInstance().error(e.getMessage());

			}

		});
		
		
		
		
		} catch (Exception e) {
			
			Logger.getInstance().error(e.getMessage());


			
		}
		
	}

}
