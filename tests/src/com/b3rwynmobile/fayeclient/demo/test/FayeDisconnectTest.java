package com.b3rwynmobile.fayeclient.demo.test;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeService;

import junit.framework.Assert;

public class FayeDisconnectTest extends ServiceTestCase<FayeService> {

	public FayeDisconnectTest() {
		super(FayeService.class);
	}

	public FayeDisconnectTest(Class<FayeService> serviceClass) {
		super(serviceClass);
	}

	public void testDisconnect() {
		// Arrange
		FayeBinder binder = (FayeBinder) bindService(new Intent(getContext(),
				FayeService.class));

		// Act
		if (!binder.getFayeClient().isFayeConnected()
				&& !binder.getFayeClient().isSocketConnected()) {
			binder.getFayeClient().connect();
		}
		binder.getFayeClient().disconnect();

		// Assert
		Assert.assertFalse(binder.getFayeClient().isFayeConnected());
		Assert.assertFalse(binder.getFayeClient().isSocketConnected());
		Assert.assertNull(binder.getFayeClient().getClientId());
	}

}
