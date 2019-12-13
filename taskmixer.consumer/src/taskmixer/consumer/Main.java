package taskmixer.consumer;


import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "txc", mixinStandardHelpOptions = true, version = "txc 1.0", description = "Receive and execute commands from a RabbitMQ queue.")
public class Main implements Callable<Integer>  {

	@Option(names = {"-u", "--username"}, description = "RabbitMQ username", required = true)
	private String username;
	
	@Option(names = {"-p", "--password"}, description = "RabbitMQ password", required = true)
	private String password;
	
	@Option(names = {"-i", "--ip"}, description = "RabbitMQ IP address", required = true)
	private String ip;
	
	
	public static void main(String... args) {
        new CommandLine(new Main()).execute(args);
    }


	@Override
	public Integer call() throws Exception {

		new Consumer(username, password, ip);		
		
		return 0;
	
	}




}
