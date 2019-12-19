package taskmixer.core.concept;

/**
 * Thread wrapper.
 * 
 * @author acco
 *
 */
public abstract class ActiveEntity extends Thread {

	public ActiveEntity() {

	}

	@Override
	public void run() {
		this.work();
	}

	protected abstract void work();

	
}
