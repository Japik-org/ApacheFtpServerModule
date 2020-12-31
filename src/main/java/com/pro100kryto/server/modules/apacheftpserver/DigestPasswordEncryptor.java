package com.pro100kryto.server.modules.apacheftpserver;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.ftpserver.usermanager.PasswordEncryptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestPasswordEncryptor implements PasswordEncryptor {
    private final MessageDigest md;

    public DigestPasswordEncryptor(String algorithm, String salt) throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance(algorithm);
        md.update(salt.getBytes(StandardCharsets.UTF_8));
    }

    public DigestPasswordEncryptor(String algorithm) throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance(algorithm);
    }

    @Override
    public String encrypt(String s) {
        byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return HexBin.encode(bytes);
    }

    @Override
    public boolean matches(String s, String s1) {
        byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
        String passCheck = HexBin.encode(bytes);
        return passCheck.equals(s1);
    }
}
