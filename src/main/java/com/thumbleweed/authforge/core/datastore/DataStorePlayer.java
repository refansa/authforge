package com.thumbleweed.authforge.core.datastore;

import com.thumbleweed.authforge.core.Player;

public interface DataStorePlayer extends Player {
    boolean isBanned();

    /**
     * Sets the player's ban status.
     * @param ban boolean
     * @return void
     */
    DataStorePlayer setBanned(boolean ban);

    /**
     * Sets the player's password.
     * @param password boolean
     * @return void
     */
    DataStorePlayer setPassword(String password);

    /**
     * Gets the player's password.
     * @return String
     */
    String getPassword();
}
