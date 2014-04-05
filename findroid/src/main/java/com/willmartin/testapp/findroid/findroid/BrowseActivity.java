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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BrowseActivity extends ActionBarActivity {

    private FileSystemModel model;
    private BrowseListAdapter listAdapter;

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
                    setModel(model);
                    return model;
                } catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(FileSystemModel model){
                if (model != null){
                    Log.v("MYAPP", "Connected!");
                    createViews();
                } else {
                    startActivity(backIntent);
                }
            }
        };
        task.execute();
    }

    private void createViews(){

        try {

            listAdapter = new BrowseListAdapter(this, Adapter.IGNORE_ITEM_VIEW_TYPE, model.ls());

            ListView listView = (ListView)this.findViewById(R.id.browseListView);
            listView.setAdapter(listAdapter);

        } catch (SftpException e) {
            //do something!
        }
    }

    private void setModel(FileSystemModel model){
        this.model = model;
    }

    public void changeDir(View view){


        final String newDir = model.getCurrentLocation() + "/" + ((TextView) view.findViewById(R.id.filenameTextView)).getText().toString();

        Log.v("MYAPP", newDir);

        AsyncTask<Void, Void, List<ChannelSftp.LsEntry>> task = new AsyncTask<Void, Void, List<ChannelSftp.LsEntry>>() {
            @Override
            protected List<ChannelSftp.LsEntry> doInBackground(Void... voids) {
                try {
                    return model.ls(newDir);
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
                    //TODO TOAST
                }
            }
        };
        task.execute();
    }

    public void getFile(View view) {
        TextView labelView = (TextView) ((View) view.getParent()).findViewById(R.id.filenameTextView);
        final String filename = labelView.getText().toString();
        final String filePath = model.getCurrentLocation() + "/" + filename;

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
                    //TODO: toast?
                } catch (IOException e) {
                    Log.v("DOWNLOADS", "IO Exception");
                    //TODO: More toast?
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
