package com.willmartin.testapp.findroid.findroid;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.util.Vector;

/**
 * Created by martinw on 4/3/14.
 */
public class FileSystemModel {


    private Session session;
    private ChannelSftp sftpChannel;
    private String currentLocation;

    public FileSystemModel(String host, int port, String username, String password)
            throws JSchException, SftpException {
        JSch jsch = new JSch();
        this.session = jsch.getSession(username, host, port);
        this.session.setPassword(password);
        UserInfo ui = new DummyUserInfo();
        this.session.setUserInfo(ui);

        this.session.connect();
        this.sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
        this.sftpChannel.connect();
        this.currentLocation = this.sftpChannel.pwd();
    }


    public Vector<ChannelSftp.LsEntry> ls(String absPath) throws SftpException {
        this.currentLocation = absPath;

        Vector<ChannelSftp.LsEntry> items = this.sftpChannel.ls(absPath);
        return items;
    }

    public void shutdownConnection() {
        this.sftpChannel.disconnect();
        this.session.disconnect();
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


                //Vector results = c.ls("/");
                Vector results = null;
                for (Object result : results) {
                    ChannelSftp.LsEntry actualResult = (ChannelSftp.LsEntry) result;
                    System.out.println(actualResult.getLongname());

//                    Log.v("MYAPP2", actualResult.getLongname());
                    SftpATTRS attrs = actualResult.getAttrs();
                    Log.v("MYAPP2", attrs.toString());

                }
                //c.exit();
                //session.disconnect();
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

    private class DummyUserInfo implements  UserInfo, UIKeyboardInteractive {

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
}
