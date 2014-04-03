package com.willmartin.testapp.findroid.findroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.util.Vector;

public class LogIn extends ActionBarActivity {

    String PASSWORD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MYAPP2", "STARTING APP");
        System.out.println("Starting!");
        setContentView(R.layout.activity_log_in);
        new SSHConnection().execute();
    }

    private class SSHConnection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            JSch jsch = new JSch();
            String username = "jonesn";
            String host = "skittles.mathcs.carleton.edu";
            int port = 22;

            Session session;
            try {
                session = jsch.getSession(username, host, port);
                session.setPassword(PASSWORD);
                UserInfo ui = new TestUserInfo();
                session.setUserInfo(ui);
                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();
                ChannelSftp c = (ChannelSftp)channel;

                Vector results = c.ls("/");
                for (Object result : results) {
                    ChannelSftp.LsEntry actualResult = (ChannelSftp.LsEntry) result;
                    System.out.println(actualResult.getLongname());

//                    Log.v("MYAPP2", actualResult.getLongname());
                    SftpATTRS attrs = actualResult.getAttrs();
                    Log.v("MYAPP2", attrs.toString());

                }
                c.exit();
                session.disconnect();
            } catch (Exception e) {
                Log.v("MYAPP2", "End");
                System.out.println("End");
                Log.v("MYAPP2", e.toString());
                String s = "";
                for (int i=0; i < e.getStackTrace().length; i++)
                {
                    s += e.getStackTrace()[i].toString() + "\n";
                }

                Log.v("MYAPP2", e.getMessage());
                Log.v("MYAPP2", s);
            }
            return null;
        }
    }

    private class TestUserInfo implements  UserInfo, UIKeyboardInteractive {

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            return true;
        }

        @Override
        public void showMessage(String s) {

        }

        @Override
        public String[] promptKeyboardInteractive(String s, String s2, String s3, String[] strings, boolean[] booleans) {
            return null;
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

    public void launchSession(View view) {
        Intent intent = new Intent(this, BrowseActivity.class);
        String hostText = ((EditText) findViewById(R.id.host)).getText().toString();
        String usernameText = ((EditText) findViewById(R.id.username)).getText().toString();
        String passwordText = ((EditText) findViewById(R.id.password)).getText().toString();

        startActivity(intent);
    }
}
