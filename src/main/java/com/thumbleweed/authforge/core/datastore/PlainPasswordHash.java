package com.thumbleweed.authforge.core.datastore;

/**
 * A simple implementation of PasswordHash, there is literally no hashing method, just plain text for debugging.
 */
public class PlainPasswordHash implements PasswordHash {
    @Override
    public String hash(String data) {
        return data;
    }

    @Override
    public boolean compare(String hashedPassword, String rawPassword) {
        return hashedPassword.equals(rawPassword);
    }
}
