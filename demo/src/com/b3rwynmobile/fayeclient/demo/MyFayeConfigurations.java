package com.b3rwynmobile.fayeclient.demo;

import com.b3rwynmobile.fayeclient.config.FayeConfigurations;

public class MyFayeConfigurations extends FayeConfigurations {

    public MyFayeConfigurations() {

	/*
	 * Need override to work, here you put your url initial channel and if
	 * need the auth token
	 */
	FAYE_URL = "ws://example.com:00/mount";
	FAYE_INITIAL_CHANNEL = "/some_channel";
	FAYE_AUTH_TOKEN = "";

	/*
	 * Override to debug, it will show faye logs with logcat tag
	 * "FayeAndroid" default is false
	 */
	logEnabled = true;

	/*
	 * Override to see faye method called with logcat tag
	 * "FayeAndroidMethodTracer" default is false
	 */
	logMethodTrackerEnabled = true;

	/*
	 * Override to see faye exceptions use the logcat tag
	 * "FayeAndroidException" default is false
	 */
	logExceptionsEnabled = true;
    }

}
