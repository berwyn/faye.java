package com.b3rwynmobile.fayeclient.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeClient;
import com.b3rwynmobile.fayeclient.FayeListener;
import com.b3rwynmobile.fayeclient.FayeService;
import com.b3rwynmobile.fayeclient.config.FayeConfigurations;
import com.b3rwynmobile.fayeclient.models.FayeMessage;

public class DemoActivity extends Activity implements FayeListener,
	ServiceConnection {

    /**
     * a easy form to controller the connection status
     */
    private enum FayeConnectionStatus {
	FAYE_CS_CONNECTED, FAYE_CS_DESCONNECTED, FAYE_CS_CONNECTING, FAYE_CS_DESCONNECTING;
    }

    /*
     * Instance variables
     */

    private FayeConnectionStatus mFayeConnected = FayeConnectionStatus.FAYE_CS_DESCONNECTED;
    private FayeBinder mBinder;
    private EditText mTextBox;
    private ToggleButton mConnectToggle;
    private Button mSendTextButton;
    private Button mSendRawButton;
    private ProgressBar mProgressBar;
    private TextView mDataReceivedTextView;

    /*
     * life cycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.fayedemo_activity_demo);

	// You need set the configurations before use the faye client
	FayeConfigurations.shared = new MyFayeConfigurations();

	mTextBox = (EditText) findViewById(R.id.message_box);
	mConnectToggle = (ToggleButton) findViewById(R.id.connect_toggle_button);
	mSendTextButton = (Button) findViewById(R.id.send_text_button);
	mSendRawButton = (Button) findViewById(R.id.send_raw_button);
	mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_wait_connection);
	mDataReceivedTextView = (TextView) findViewById(R.id.txt_messages_receiveds);
    }

    /*
     * onClicks handlers
     */

    public void onToggleButtonClick(View v) {
	switch (mFayeConnected) {
	case FAYE_CS_DESCONNECTED:
	    connect();
	    break;
	case FAYE_CS_CONNECTED:
	    disconnect();
	    break;
	default:
	    MyFayeConfigurations.log("wait faye connection proccess");
	}
    }

    public void onTextButtonClick(View v) {
	if (mFayeConnected == FayeConnectionStatus.FAYE_CS_CONNECTED)
	    mBinder.getFayeClient().sendTextMessage(
		    mTextBox.getText().toString());
	else
	    MyFayeConfigurations.log("Connect Faye before to send a message");
    }

    public void onRawButtonClick(View v) {
	if (mFayeConnected == FayeConnectionStatus.FAYE_CS_CONNECTED)
	    mBinder.getFayeClient().sendRawTextMessage(
		    mTextBox.getText().toString());
	else
	    MyFayeConfigurations.log("Connect Faye before to send a message");
    }

    public void onClearButtonClick(View v) {
	mDataReceivedTextView.setText("");
    }

    /*
     * layout behavior methods
     */

    public void setmFayeConnected(FayeConnectionStatus mFayeConnected) {
	switch (mFayeConnected) {
	case FAYE_CS_DESCONNECTED:
	case FAYE_CS_CONNECTED:
	    mConnectToggle.setVisibility(View.VISIBLE);
	    mProgressBar.setVisibility(View.INVISIBLE);
	    mSendRawButton.setEnabled(true);
	    mSendTextButton.setEnabled(true);
	    mConnectToggle
		    .setChecked(mFayeConnected == FayeConnectionStatus.FAYE_CS_CONNECTED);
	    break;
	default:
	    mConnectToggle.setVisibility(View.INVISIBLE);
	    mProgressBar.setVisibility(View.VISIBLE);
	    mSendRawButton.setEnabled(false);
	    mSendTextButton.setEnabled(false);
	}
	this.mFayeConnected = mFayeConnected;
    }

    /*
     * custom methods
     */

    public void connect() {
	if (mBinder == null) {
	    bindFayeService();
	} else if (mFayeConnected == FayeConnectionStatus.FAYE_CS_DESCONNECTED) {
	    setmFayeConnected(FayeConnectionStatus.FAYE_CS_CONNECTING);
	    mBinder.getFayeService().startFaye();
	}
    }

    public void disconnect() {
	if (mBinder == null) {
	    bindFayeService();
	} else if (mFayeConnected == FayeConnectionStatus.FAYE_CS_CONNECTED) {
	    setmFayeConnected(FayeConnectionStatus.FAYE_CS_DESCONNECTING);
	    mBinder.getFayeService().stopFaye();
	}
    }

    private void bindFayeService() {
	Intent intent = new Intent();
	intent.setClass(this, FayeService.class);
	bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * example to subscribe a channel
     * 
     * @param channel
     *            example: /channel_to_connect
     */
    public void subscribe(String channel) {
	if (mBinder != null)
	    mBinder.getFayeClient().subscribe(channel);
	else
	    MyFayeConfigurations.log("Connect Faye before subscribe a channel");
    }

    /*
     * Faye Listener
     */

    public void connectedToServer(FayeClient faye) {
	setmFayeConnected(FayeConnectionStatus.FAYE_CS_CONNECTED);
    }

    public void disconnectedFromServer(FayeClient faye) {
	setmFayeConnected(FayeConnectionStatus.FAYE_CS_DESCONNECTED);
    }

    public void messageReceived(FayeClient faye, FayeMessage message) {
	MyFayeConfigurations.log("Faye received message", message);

	// Example of get message
	if (message != null && message.getData() != null)
	    mDataReceivedTextView.setText(mDataReceivedTextView.getText()
		    + "\n" + message.getData().toString());
    }

    /*
     * Service Connection Listener
     */

    public void onServiceConnected(ComponentName name, IBinder service) {
	mBinder = (FayeBinder) service;
	mBinder.getFayeClient().setFayeListener(this);
	if (mBinder.getFayeClient().isFayeConnected())
	    setmFayeConnected(FayeConnectionStatus.FAYE_CS_CONNECTED);
	else
	    connect();
    }

    public void onServiceDisconnected(ComponentName name) {
	mBinder = null;
	setmFayeConnected(FayeConnectionStatus.FAYE_CS_DESCONNECTED);
    }

}
