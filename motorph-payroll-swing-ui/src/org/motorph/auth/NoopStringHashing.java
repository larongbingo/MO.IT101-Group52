package org.motorph.auth;

import org.motorph.employees.crypto.StringHashing;

public class NoopStringHashing implements StringHashing {
    @Override
    public String hash(String plaintext) {
        return plaintext;
    }

    @Override
    public boolean verify(String plaintext, String hash) {
        return plaintext.equals(hash);
    }
}
