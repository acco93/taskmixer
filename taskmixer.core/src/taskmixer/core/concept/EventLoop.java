package taskmixer.core.concept;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * Thread that follows the event loop architecture.
 * 
 * @author acco
 *
 * @param <T>
 *            event type
 */
public abstract class EventLoop<T> extends Thread {

	private BlockingQueue<T> queue;
	private volatile boolean stopped;

	public EventLoop() {
		this.queue = new LinkedBlockingQueue<>();
		this.stopped = false;
	}

	/**
	 * Add an event to the queue.
	 * 
	 * @param event
	 */
	protected void append(T event) {
		try {
			this.queue.put(event);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve an event from the queue. Block if none.
	 * 
	 * @return the event
	 */
	private T get() {
		T event = null;

		try {
			event = this.queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return event;
	}

	@Override
	public void run() {

		while (!stopped) {

			T event = this.get();

			if (stopped) {
				break;
			}

			process(event);
		}

	}

	/**
	 * Process the event.
	 * 
	 * @param event
	 */
	protected abstract void process(T event);

	public void disable() {
		this.stopped = true;
		this.interrupt();

	}

}
