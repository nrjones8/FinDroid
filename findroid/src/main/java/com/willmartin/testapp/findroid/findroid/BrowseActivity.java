package com.willmartin.testapp.findroid.findroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BrowseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Intent intent = getIntent();

        // In case we fail and need to go back
        final Intent backIntent = new Intent(this, LogIn.class);
        final String hostname = intent.getStringExtra(LogIn.HOSTNAME_EXTRA);
        final String username = intent.getStringExtra(LogIn.USERNAME_EXTRA);
        final String password = intent.getStringExtra(LogIn.PASSWORD_EXTRA);


        // Attempt to create a new FileSystemModel, which initiates the SSH connection
        // asynchronously
        AsyncTask<Void, Void, FileSystemModel> task = new AsyncTask<Void, Void, FileSystemModel>() {
            @Override
            protected FileSystemModel doInBackground(Void... voids) {
                try {
                    FileSystemModel model = new FileSystemModel(hostname, 22, username, password);
                    return model;
                } catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(FileSystemModel model){
                if (model != null){
                    Log.v("MYAPP", "Connected!");
                } else {
                    startActivity(backIntent);
                }
            }
        };
        task.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse, menu);
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

}
