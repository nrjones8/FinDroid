package com.willmartin.testapp.findroid.findroid;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.InputStream;
import java.util.Vector;

/**
 * Created by martinw on 4/3/14.
 */
public class FileSystemModel {

    private String host;
    private int port;
    private String username;
    private String password;

    private Session session;
    private ChannelSftp sftpChannel;
    private String currentLocation;

    public FileSystemModel(String host, int port, String username, String password)
            throws JSchException, SftpException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connect();
    }

    /*
     * Initializes sftpchannel, connection, and session
     * Called when first creating the model, or when reconnecting onRestart()
     */
    public void connect() throws JSchException, SftpException {
        JSch jsch = new JSch();
        this.session = jsch.getSession(this.username, this.host, this.port);
        this.session.setPassword(this.password);
        UserInfo ui = new DummyUserInfo();
        this.session.setUserInfo(ui);

        this.session.connect();
        this.sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
        this.sftpChannel.connect();

        if(this.currentLocation == null) {
            this.currentLocation = this.sftpChannel.pwd();
        }
    }
    public String getCurrentLocation(){
        return currentLocation;
    }
    public String getUsername() {return username; }
    public String getHost() {return host;}
    public int getPort(){return port;}
    /*
     * Returns Vector of Entry objects stored in <absPath>
     */
    public Vector<ChannelSftp.LsEntry> ls(String absPath) throws SftpException {
        this.currentLocation = absPath;

        Vector<ChannelSftp.LsEntry> items = this.sftpChannel.ls(absPath);
        return items;
    }

    public Vector<ChannelSftp.LsEntry> ls() throws SftpException {
        return this.ls(this.currentLocation);
    }

    public InputStream get(String filePath) throws SftpException {
        return this.sftpChannel.get(filePath);
    }

    public void shutdownConnection() {
        this.sftpChannel.disconnect();
        this.session.disconnect();
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
