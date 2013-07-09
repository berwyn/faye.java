package org.codeweaver.faye;

import com.google.common.eventbus.EventBus;

/**
 * Created with IntelliJ IDEA. User: Berwyn Codeweaver Date: 08/07/13 Time:
 * 01:46 To change this template use File | Settings | File Templates.
 */
public class BusProvider {

	private static final EventBus	BUS	= new EventBus();

	// Disallow instances
	private BusProvider() {
	}

	public static EventBus getInstance() {
		return BUS;
	}

}
