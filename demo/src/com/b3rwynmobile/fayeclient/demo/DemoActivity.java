package com.b3rwynmobile.fayeclient.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeClient;
import com.b3rwynmobile.fayeclient.FayeListener;
import com.b3rwynmobile.fayeclient.FayeService;
import com.b3rwynmobile.fayeclient.models.FayeMessage;

public class DemoActivity extends Activity implements FayeListener,
        ServiceConnection {

	private boolean	     mFayeConnected;
	private FayeBinder	 mBinder;
	private EditText	 mTextBox;
	private ToggleButton	mConnectToggle;
	private Button	     mSendTextButton;
	private Button	     mSendRawButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fayedemo_activity_demo);

		mTextBox = (EditText) findViewById(R.id.message_box);
		mConnectToggle = (ToggleButton) findViewById(R.id.connect_toggle_button);
		mSendTextButton = (Button) findViewById(R.id.send_text_button);
		mSendRawButton = (Button) findViewById(R.id.send_raw_button);

		mConnectToggle.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!mFayeConnected) {
					connect();
				} else {
					disconnect();
				}
			}
		});

		mSendTextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				sendMessage(mTextBox.getText().toString());
			}
		});

		mSendRawButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				sendRawMessage(mTextBox.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fayedemo_activity_demo, menu);
		return true;
	}

	public void connectedToServer(FayeClient faye) {
		mFayeConnected = true;
		mConnectToggle.setChecked(mFayeConnected);
	}

	public void disconnectedFromServer(FayeClient faye) {
		mFayeConnected = false;
		mConnectToggle.setChecked(mFayeConnected);
	}

	public void messageReceived(FayeClient faye, FayeMessage message) {
		Toast.makeText(this, "Faye received message: "
		        + message.getData().toString(), Toast.LENGTH_LONG).show();
	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		mBinder = (FayeBinder) service;
		mBinder.getFayeClient().setFayeListener(this);
		mFayeConnected = mBinder.getFayeClient().isFayeConnected();
		if (!mFayeConnected) connect();
	}

	public void onServiceDisconnected(ComponentName name) {
		mBinder = null;
		mFayeConnected = false;
	}

	public void subscribe(String channel) {
		if (mBinder != null) {
			mBinder.getFayeClient().subscribe(channel);
		} else {
			Toast.makeText(this, "Please connect Faye first", Toast.LENGTH_LONG).show();
		}
	}

	public void sendMessage(String message) {
		if (mFayeConnected) {
			mBinder.getFayeClient().sendTextMessage(message);
		} else {
			Toast.makeText(this,
			        "Please connect Faye before trying to send a message",
			        Toast.LENGTH_LONG).show();
		}
	}

	public void sendRawMessage(String message) {
		if (mFayeConnected) {
			mBinder.getFayeClient().sendRawTextMessage(message);
		} else {
			Toast.makeText(this,
			        "Please connect Faye before attempting to send a message",
			        Toast.LENGTH_LONG).show();
		}
	}

	private void bindFayeService() {
		Intent intent = new Intent();
		intent.setClass(this, FayeService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	public void connect() {
		if (mBinder == null) {
			bindFayeService();
		} else {
			if (!mFayeConnected) mBinder.getFayeService().startFaye();
		}
	}

	public void disconnect() {
		if (mBinder == null) {
			bindFayeService();
		} else {
			if (mFayeConnected) mBinder.getFayeService().stopFaye();
		}
	}
}
