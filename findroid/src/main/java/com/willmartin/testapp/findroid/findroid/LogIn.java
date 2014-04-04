package com.willmartin.testapp.findroid.findroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LogIn extends ActionBarActivity {

    public final static String HOSTNAME_EXTRA = "com.willmartin.testapp.findroid.hostname_extra";
    public final static String USERNAME_EXTRA = "com.willmartin.testapp.findroid.username_extra";
    public final static String PASSWORD_EXTRA = "com.willmartin.testapp.findroid.passwrod_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MYAPP2", "STARTING APP");
        System.out.println("Starting!");
        setContentView(R.layout.activity_log_in);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void launchSession(View view) {

        String hostText = ((EditText) findViewById(R.id.host)).getText().toString();
        String usernameText = ((EditText) findViewById(R.id.username)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.password)).getText().toString();

        Intent intent = new Intent(this, BrowseActivity.class);
        intent.putExtra(HOSTNAME_EXTRA, hostText);
        intent.putExtra(USERNAME_EXTRA, usernameText);
        intent.putExtra(PASSWORD_EXTRA, passwordText);
        startActivity(intent);
    }
}
