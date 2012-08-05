package com.b3rwynmobile.fayeclient.demo.test;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeClient;
import com.b3rwynmobile.fayeclient.FayeService;

import junit.framework.Assert;

import java.text.MessageFormat;

public class FayeConnectivityTests extends ServiceTestCase<FayeService> {

	private static final String	TAG	= "Faye Connectivity Test";

	public FayeConnectivityTests() {
		super(FayeService.class);
	}

	public FayeConnectivityTests(Class<FayeService> serviceClass) {
		super(serviceClass);
	}
	
	public void setUp() {
		FayeService service = getService();
		if(service != null) {
			service.stopFaye();
			service.stopSelf();
		}
	}

	public void testBinder() {
		// Arrange
		FayeBinder binder;

		// Act
		binder = (FayeBinder) bindService(new Intent(getContext(),
				FayeService.class));

		// Assert
		Assert.assertNotNull(binder);
		Assert.assertNotNull(binder.getFayeClient());
	}

	public void testConnect() {
		// Arrange
		FayeBinder binder = (FayeBinder) bindService(new Intent(getContext(),
				FayeService.class));
		FayeClient client = binder.getFayeClient();

		// Act
		client.connect();
		while (!(client.isFayeConnected() && binder.getFayeClient()
				.isSocketConnected())) {
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

	public void testDisconnect() {
		// Arrange
		FayeBinder binder = (FayeBinder) bindService(new Intent(getContext(),
				FayeService.class));

		// Act
		binder.getFayeClient().connect();
		binder.getFayeClient().disconnect();

		// Assert
		Assert.assertNull(binder.getFayeClient().getClientId());
	}

}
