package com.b3rwynmobile.fayeclient.demo.test;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.b3rwynmobile.fayeclient.FayeBinder;
import com.b3rwynmobile.fayeclient.FayeService;

import junit.framework.Assert;

public class FayeServiceTests extends ServiceTestCase<FayeService> {

	public FayeServiceTests() {
		super(FayeService.class);
	}

	public FayeServiceTests(Class<FayeService> serviceClass) {
		super(serviceClass);
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

}
