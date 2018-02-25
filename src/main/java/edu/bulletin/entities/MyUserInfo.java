package edu.bulletin.entities;

import com.jcraft.jsch.UserInfo;

import javax.swing.*;


public class MyUserInfo implements UserInfo {

    private String passwd;

    public MyUserInfo(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String getPassword() {
        return passwd;
    }

    @Override
    public boolean promptYesNo(String str) {
        return true;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptPassword(String message) {
        return true;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

}

