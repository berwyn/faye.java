package com.b3rwynmobile.fayeclient.demo.test;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeClient;
import com.b3rwynmobile.fayeclient.FayeService;

import junit.framework.Assert;

import java.text.MessageFormat;

public class FayeConnectionTest extends ServiceTestCase<FayeService> {

	private static String	TAG	= "Faye Connection Test";

	public FayeConnectionTest() {
		super(FayeService.class);
	}

	public FayeConnectionTest(Class<FayeService> serviceClass) {
		super(serviceClass);
	}

	public void testConnect() {
		// Arrange
		FayeBinder binder = (FayeBinder) bindService(new Intent(getContext(),
				FayeService.class));
		FayeClient client = binder.getFayeClient();

		// Act
		client.connect();
		while (!(client.isFayeConnected() && client.isSocketConnected())) {
			try {
				Log.d(TAG,
						MessageFormat
								.format("Connection status: \n Socket status: {0}\n Faye status: {1}",
										client.isSocketConnected() ? "Connected"
												: "Not connected",
										client.isFayeConnected() ? "Connected"
												: "Not connected"));
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Assert
		Assert.assertTrue(client.isSocketConnected());
		Assert.assertTrue(client.isFayeConnected());
		Assert.assertNotNull(client.getClientId());
	}
	
	@Override
	protected void tearDown() throws Exception {
		getService().stopFaye();
		getService().stopSelf();
		
		super.tearDown();
	}

}
