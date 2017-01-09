package com.typeahead.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple Asynchronous task executor.
 * @author ronakkhunt
 *
 */
public class AsyncTaskExecutor {
	public static ExecutorService executor;
	static {
		executor = Executors.newFixedThreadPool(100);
	}
	
	/**
	 * Starts a thread with given object, which implements {@link Runnable}.  
	 * @param object
	 */
	public static void submit(Runnable object) {
		Thread thread = new Thread(object);
		thread.start();
	}
	
	public static void submitToExcutor(Runnable object) {
		executor.submit(object);
	}
	
}
