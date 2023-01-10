package org.oci;

import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;

import java.io.InputStream;

public class AuthDetailsProvider implements BasicAuthenticationDetailsProvider {

    @Override
    public String getKeyId() {
        return "MY-KEY";
    }

    @Override
    public InputStream getPrivateKey() {
        return null; // link to key file
    }

    @Override
    public String getPassPhrase() {
        return "password";
    }

    @Override
    public char[] getPassphraseCharacters() {
        return new char[0];
    }
}


