package com.b3rwynmobile.fayeclient.config;

import android.util.Log;

/**
 * @author Ademar Alves de Oliveira
 * @Apr 23, 2013
 * @email ademar111190@gmail.com
 */
public class FayeConfigurations {

	/**
	 * Singleton access
	 */
	public static FayeConfigurations	shared	             = new FayeConfigurations();

	public String	                 FAYE_URL	             = "ws://your.url:80/mount";
	public String	                 FAYE_INITIAL_CHANNEL	 = "/your_channel";
	public String	                 FAYE_AUTH_TOKEN	     = "";

	public boolean	                 logEnabled	             = false;
	public boolean	                 logMethodTrackerEnabled	= false;
	public boolean	                 logExceptionsEnabled	 = false;
	public String	                 logTag	                 = "FayeAndroid";
	public String	                 logMethodTrackerTag	 = "FayeAndroidMethodTracer";
	public String	                 logExceptionTag	     = "FayeAndroidException";

	public static void log(Object... args) {
		if (shared.logEnabled) {
			String message = new String();
			for (Object o : args)
				message += o + " - ";
			android.util.Log.d(shared.logTag, message);
		}
	}

	public static void tracker(Object instance, Object... params) {
		if (shared.logMethodTrackerEnabled) {
			String s = "";
			int i = 1;
			for (Object string : params) {
				s += i + ":" + string + " , ";
				i++;
			}
			android.util.Log.d(shared.logMethodTrackerTag, "Method: "
			        + Thread.currentThread().getStackTrace()[4] + " instance: "
			        + (instance == null ? "Null" : instance) + " params: " + s);
		}
	}

	public static void logException(Exception e) {
		if (shared.logEnabled) {
			Log.e(shared.logExceptionTag, e.toString());
		}
	}
}
