package taskmixer.common.log;

/**
 * Simple logger interface.
 * @author acco
 *
 */
public interface LoggerInterface {

	void info(String message);
	
	void error(String message);

	void warning(String string);
	
}
