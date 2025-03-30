package com.thumbleweed.authforge.core.datastore;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * A simple Bcrypt implementation of PasswordHash.
 */
public class BcryptPasswordHash implements PasswordHash {
    @Override
    public String hash(String data) {
        return BCrypt.withDefaults().hashToString(12, data.toCharArray());
    }

    @Override
    public boolean compare(String hashedPassword, String rawPassword) {
        BCrypt.Result verifyResult = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);

        return verifyResult.verified;
    }
}
