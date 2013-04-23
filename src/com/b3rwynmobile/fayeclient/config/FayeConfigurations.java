package com.b3rwynmobile.fayeclient.config;

/**
 * @author Ademar Alves de Oliveira
 * @Apr 23, 2013
 * @email ademar111190@gmail.com
 */
public class FayeConfigurations {

	/**
	 * Singleton access
	 */
	public static FayeConfigurations	shared	          = new FayeConfigurations();

	public boolean	                 logEnabled	          = false;
	public boolean	                 methodTrackerEnabled	= false;
	public String	                 logTag	              = "FayeAndroid";
	public String	                 methodTrackerTag	  = "FayeAndroidMethodTracer";

	public static void Log(Object... args) {
		if (shared.logEnabled) {
			String message = new String();
			for (Object o : args)
				message += o + " - ";
			android.util.Log.d(shared.logTag, message);
		}
	}

	public static void log(Object instance, Object... params) {
		if (shared.methodTrackerEnabled) {
			String s = "";
			int i = 1;
			for (Object string : params) {
				s += i + ":" + string + " , ";
				i++;
			}
			android.util.Log.d(shared.methodTrackerTag, "Method: "
			        + Thread.currentThread().getStackTrace()[4] + " instance: "
			        + (instance == null ? "Null" : instance) + " params: " + s);
		}
	}
}
