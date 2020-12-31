package com.pro100kryto.server.modules.apacheftpserver;

import org.apache.ftpserver.usermanager.PasswordEncryptor;

public class NoPasswordEncryptor implements PasswordEncryptor {
    @Override
    public String encrypt(String s) {
        return s;
    }

    @Override
    public boolean matches(String s, String s1) {
        return s.equals(s1);
    }
}
