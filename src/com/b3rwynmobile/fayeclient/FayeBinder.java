package com.b3rwynmobile.fayeclient;

import android.os.Binder;

/**
 * Binder class to interact with the service
 * 
 * @author Jamison Greeley (atomicrat2552@gmail.com)
 */
public class FayeBinder extends Binder {

	private FayeService	service;
	private FayeClient	faye;

	public FayeBinder() {
		service = null;
		faye = null;
	}

	public FayeBinder(FayeService service, FayeClient client) {
		this.service = service;
		this.faye = client;
	}

	public FayeClient getFayeClient() {
		return faye;
	}

	public void setFayeClient(FayeClient faye) {
		this.faye = faye;
	}

	public FayeService getFayeService() {
		return service;
	}

	public void setFayeService(FayeService service) {
		this.service = service;
	}
}
