package com.typeahead.async;

/**
 * Simple Asynchronous task executor.
 * @author ronakkhunt
 *
 */
public class AsyncTaskExecutor {
	
	/**
	 * Starts a thread with given object, which implements {@link Runnable}.  
	 * @param object
	 */
	public static void submit(Runnable object) {
		Thread thread = new Thread(object);
		thread.start();
	}
}
