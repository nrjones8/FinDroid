package com.willmartin.testapp.findroid.findroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends ActionBarActivity {

    public final static String HOSTNAME_EXTRA = "com.willmartin.testapp.findroid.hostname_extra";
    public final static String USERNAME_EXTRA = "com.willmartin.testapp.findroid.username_extra";
    public final static String PASSWORD_EXTRA = "com.willmartin.testapp.findroid.passwrod_extra";
    public final static String BROWSE_ERROR_EXTRA = "com.willmartin.testapp.findroid.from_browse_extra";
    public final static int BROWSE_REQUEST = 65432;
    public final static int BROWSE_ERROR = 999999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MYAPP2", "STARTING APP");
        System.out.println("Starting!");
        setContentView(R.layout.activity_log_in);


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String savedUsername = sharedPref.getString(getString(R.string.prefs_username_key), null);
        if (savedUsername != null){
            ((EditText) findViewById(R.id.username)).setText(savedUsername);
        }
        String savedHostname = sharedPref.getString(getString(R.string.prefs_hostname_key), null);
        if (savedHostname != null){
            ((EditText) findViewById(R.id.host)).setText(savedHostname);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.v("WILLTEST","In on restart");
        Intent intent = getIntent();
        boolean isError = intent.getBooleanExtra(BROWSE_ERROR_EXTRA, false);
        if (isError) {
            Toast.makeText(getApplicationContext(), "Connection Error: please re-log in",
                    Toast.LENGTH_LONG).show();
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.v("MYAPP", "In onSaveInstanceState");
        if (outState != null){
            Log.v("MYAPP", "Saving state!");
            String hostText = ((EditText) findViewById(R.id.host)).getText().toString();
            String usernameText = ((EditText) findViewById(R.id.username)).getText().toString();

            outState.putString("username", usernameText);
            outState.putString("hostname", hostText);
        }
    }

    private void savePrefs(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String hostText = ((EditText) findViewById(R.id.host)).getText().toString();
        String usernameText = ((EditText) findViewById(R.id.username)).getText().toString();

        editor.putString(getString(R.string.prefs_hostname_key), hostText);
        editor.putString(getString(R.string.prefs_username_key), usernameText);

        editor.commit();
    }

    public void launchSession(View view) {

        String hostText = ((EditText) findViewById(R.id.host)).getText().toString();
        String usernameText = ((EditText) findViewById(R.id.username)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.password)).getText().toString();

        Intent intent = new Intent(this, BrowseActivity.class);
        intent.putExtra(HOSTNAME_EXTRA, hostText);
        intent.putExtra(USERNAME_EXTRA, usernameText);
        intent.putExtra(PASSWORD_EXTRA, passwordText);

        savePrefs();

        startActivityForResult(intent, BROWSE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Log.v("WILLTEST", "ON ACTIVITY RESULT!");
        if (requestCode == BROWSE_REQUEST && resultCode == BROWSE_ERROR) {
            Toast.makeText(getApplicationContext(), "Connection Error, please log in again",
                Toast.LENGTH_SHORT).show();
        }
    }
}
