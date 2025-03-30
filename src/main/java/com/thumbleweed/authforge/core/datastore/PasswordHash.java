package com.thumbleweed.authforge.core.datastore;

public interface PasswordHash {
    /**
     * Hashes the provided data.
     *
     * @param data String
     * @return String - The hashed data
     */
    String hash(String data);

    /**
     * Compares if the hashed password equals to the raw password.
     *
     * @param hashedPassword String
     * @param rawPassword    String
     * @return boolean
     */
    boolean compare(String hashedPassword, String rawPassword);
}
