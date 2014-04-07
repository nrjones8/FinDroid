package com.willmartin.testapp.findroid.findroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.security.auth.login.LoginException;

public class BrowseActivity extends ActionBarActivity {

    private FileSystemModel model;
    private BrowseListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Only called from the LogIn Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Intent intent = getIntent();

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
                    setModel(model);
                    return model;
                } catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(FileSystemModel model){
                if (model != null) {
                    Log.v("MYAPP", "Connected!");
                    createViews();
                    TextView header = (TextView) findViewById(R.id.header_text);
                    header.setText(model.getCurrentLocation());
                } else {
                    finish(true);
                }
            }
        };
        task.execute();
    }

    private void finish(boolean exitWithError) {
        if (exitWithError) {
            setResult(LogIn.BROWSE_ERROR);
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("MYAPP", "onSTOP");
        if (this.model != null){
            this.model.shutdownConnection();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("MYAPP", "onRESTART");

        // Establish connection based on previous user info, otherwise send back to
        // log in screen
        AsyncTask<Void, Void, FileSystemModel> task = new AsyncTask<Void, Void, FileSystemModel>() {
            @Override
            protected FileSystemModel doInBackground(Void... voids) {
                try {
                    model.connect();
                    return model;
                } catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(FileSystemModel model){
                if (model != null) {
                    Log.v("MYAPP", "Connected!");
                    createViews();
                } else {
                    Toast.makeText(getApplicationContext(), "Connection Error, please log in again",
                            Toast.LENGTH_LONG).show();
                    finish(true);
                }
            }
        };
        task.execute();
    }

    private void createViews() {
        try {
            listAdapter = new BrowseListAdapter(this, Adapter.IGNORE_ITEM_VIEW_TYPE, model.ls());

            ListView listView = (ListView) this.findViewById(R.id.browseListView);
            listView.setAdapter(listAdapter);

        } catch (SftpException e) {
            //do something!
        }
    }

    private void setModel(FileSystemModel model){
        this.model = model;
    }

    /*
     * Parses out the user's selection and create a new path by combining current location
     * with user's selection.
     */
    public void handleDirChange(View view) {
        String relativeDir = ((TextView) view.findViewById(R.id.filenameTextView)).getText().toString();
        File combinedPath = new File(model.getCurrentLocation(), relativeDir);
        final String newDir = combinedPath.getPath();

        this.changeDir(newDir);
    }

    /*
     * Sets the directory to be <newDir>
     */
    private void changeDir(String newDir) {
        // Workaround to access <newDir> in AsyncTask
        final String finalNewDir = newDir;
        // Set the header text to be the new directory
        TextView header = (TextView) this.findViewById(R.id.header_text);
        header.setText(newDir);

        AsyncTask<Void, Void, List<ChannelSftp.LsEntry>> task = new AsyncTask<Void, Void, List<ChannelSftp.LsEntry>>() {
            @Override
            protected List<ChannelSftp.LsEntry> doInBackground(Void... voids) {
                try {
                    return model.ls(finalNewDir);
                } catch (SftpException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(List<ChannelSftp.LsEntry> lsEntries){

                if (lsEntries != null){
                    Log.v("MYAPP", "ls-ed!");
                    listAdapter.clear();
                    listAdapter.addAll(lsEntries);
                } else {
                    Toast.makeText(getApplicationContext(), "Connection Error, please log in again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
    }

    public void getFile(View view) {
        // Grab the enclosing list item so that we can navigate to the text field.
        TextView labelView = (TextView) ((View) view.getParent()).findViewById(R.id.filenameTextView);
        final String filename = labelView.getText().toString();
        final String filePath = model.getCurrentLocation() + "/" + filename;

        Toast.makeText(getApplicationContext(), "Downloading File...",
                Toast.LENGTH_SHORT).show();

        Log.v("DOWNLOADS", "Filename: "+filePath);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.v("DOWNLOADS", "In background test");
                InputStream downloadStream = null;
                OutputStream outStream = null;
                // Our good friend: http://www.mkyong.com/java/how-to-convert-inputstream-to-file-in-java/
                try {
                    downloadStream = model.get(filePath);
                    outStream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS), filename));

                    int read = 0;
                        byte[] bytes = new byte[1024];
                    while ((read = downloadStream.read(bytes)) != -1) {
                        outStream.write(bytes, 0, read);
                    }
                } catch (SftpException e) {
                    Log.v("DOWNLOADS", "SFTP Exception");
                    Toast.makeText(getApplicationContext(), "Download Error, try again",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.v("DOWNLOADS", "IO Exception");
                    Toast.makeText(getApplicationContext(), "Download Error, try again",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    if(downloadStream != null) {
                        try {
                            downloadStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(outStream != null) {
                        try {
                            outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Successfully saved to downloads",
                        Toast.LENGTH_SHORT).show();
                Log.v("TOAST TEST", "In, on post execute");
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
        } else if(id == android.R.id.home) {
            // Back button sends user up one directory
            File currentLocationFile= new File(model.getCurrentLocation());
            String parentDir = currentLocationFile.getParent();
            this.changeDir(parentDir);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
