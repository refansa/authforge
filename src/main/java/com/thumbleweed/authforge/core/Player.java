package com.thumbleweed.authforge.core;

public interface Player {
    /**
     * Sets the current player's username.
     *
     * @param username String
     * @return Player
     */
    Player setUsername(String username);

    /**
     * Gets the current player's username.
     *
     * @return String
     */
    String getUsername();

    /**
     * Check whether the current player is authenticating using an official microsoft account.
     *
     * @return boolean
     */
    boolean isPremium();

    /**
     * Sets the current player's UUID.
     *
     * @param uuid String
     * @return Player
     */
    Player setUUID(String uuid);

    /**
     * Gets the current player's UUID.
     *
     * @return String
     */
    String getUUID();
}
