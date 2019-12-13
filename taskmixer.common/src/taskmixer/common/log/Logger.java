package taskmixer.common.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger implements LoggerInterface {

	private static LoggerInterface logger = new Logger();
	
	private SimpleDateFormat dateFormat;

	private Logger() {
		
		String datePattern = "hh:mm aaaa";
		
		dateFormat = new SimpleDateFormat(datePattern, new Locale("en", "EN"));
		
	}
	
	public static LoggerInterface getInstance() {
		return logger;
	}
	
	private String getDate() {
		return dateFormat.format(new Date());
	}

	private String getClassName() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		return stackTraceElements[3].getClassName();
	}
	
	@Override
	public void info(String message) {
		String className = this.getClassName();
		String date = this.getDate();
		System.out.println("["+date+"] ["+className+"] " + message);
		
	}

	@Override
	public void error(String message) {
		String className = this.getClassName();
		String date = this.getDate();
		System.err.println("["+date+"] ["+className+"] " + message);
	}

}
