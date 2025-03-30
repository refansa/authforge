package com.thumbleweed.authforge.core.datastore;

import com.thumbleweed.authforge.core.exception.AuthForgeException;

public interface DataStoreStrategy {
    /**
     * Possible store strategy values.
     */
    enum Strategy {
        FILE,
        DATABASE,
    }

    /**
     * Finds stored data by player's username.
     *
     * @param username String
     * @return DataStorePlayer
     * @throws AuthForgeException unexpected exception
     */
    DataStorePlayer findByUsername(String username) throws AuthForgeException;

    /**
     * Adds player to data store.
     *
     * @param player DataStorePlayer
     * @return boolean
     * @throws AuthForgeException unexpected exception
     */
    boolean add(DataStorePlayer player) throws AuthForgeException;

    /**
     * Check whether the player exists in the data store.
     *
     * @param player DataStorePlayer
     * @return boolean
     * @throws AuthForgeException unexpected exception
     */
    boolean isExist(DataStorePlayer player) throws AuthForgeException;

    /**
     * Updates the user's password.
     *
     * @param player DataStorePlayer
     * @return boolean
     * @throws AuthForgeException unexpected exception
     */
    boolean updatePassword(DataStorePlayer player) throws AuthForgeException;

    /**
     * Resets the user's data.
     *
     * @param player DataStorePlayer
     * @return boolean
     * @throws AuthForgeException unexpected exception
     */
    boolean resetPlayer(DataStorePlayer player) throws AuthForgeException;

    PasswordHash getPasswordHash();
}
