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
	public static FayeConfigurations shared	= new FayeConfigurations();

	public boolean logEnabled = false;
	public String logTag = "FayeAndroid";

	public static void Log(Object... args) {
		if (shared.logEnabled) {
			String message = new String();
			for (Object o : args)
				message += o + " - ";
			android.util.Log.d(shared.logTag, message);
		}
	}
}
