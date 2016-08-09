package com.typeahead.time;

import java.util.Calendar;

public class TimeUtil {
	public static long startTime;
	
	public static void start() {
		startTime = Calendar.getInstance().getTimeInMillis();
	}
	
	public static void end() {
		System.out.println("Time taken: "+(Calendar.getInstance().getTimeInMillis() - startTime));
	}
}
