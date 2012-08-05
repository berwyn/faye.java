package com.b3rwynmobile.fayeclient.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class DemoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fayedemo_activity_demo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fayedemo_activity_demo, menu);
        return true;
    }
}
