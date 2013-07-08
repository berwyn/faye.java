package org.codeweaver.faye;

import com.squareup.otto.Bus;

/**
 * Created with IntelliJ IDEA.
 * User: Berwyn Codeweaver
 * Date: 08/07/13
 * Time: 01:46
 * To change this template use File | Settings | File Templates.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    // Disallow instances
    private BusProvider() { }

    public static Bus getInstance() {
        return BUS;
    }

}
