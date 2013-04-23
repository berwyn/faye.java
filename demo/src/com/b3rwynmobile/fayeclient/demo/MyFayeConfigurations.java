package com.b3rwynmobile.fayeclient.demo;

import com.b3rwynmobile.fayeclient.config.FayeConfigurations;

public class MyFayeConfigurations extends FayeConfigurations {

    /**
     * Need override to work
     */
    public String FAYE_URL = "ws://your.url:80/mount";
    public String FAYE_INITIAL_CHANNEL = "/your_channel";
    public String FAYE_AUTH_TOKEN = "";

    /**
     * Override to debug
     */
    public boolean logEnabled = true;
    public boolean logMethodTrackerEnabled = true;
    public boolean logExceptionsEnabled = true;

}
