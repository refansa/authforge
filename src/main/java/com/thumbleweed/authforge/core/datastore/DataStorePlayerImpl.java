package com.thumbleweed.authforge.core.datastore;

import com.thumbleweed.authforge.core.Player;
import com.thumbleweed.authforge.core.PlayerImpl;

/**
 * Implementation of the DataStorePlayer interface.
 */
public class DataStorePlayerImpl implements DataStorePlayer {
    private String password;
    private boolean banned;
    private final Player player;

    public DataStorePlayerImpl(Player player) {
        this.player = player;
        this.password = "";
    }

    public DataStorePlayerImpl() {
        this(new PlayerImpl());
    }

    @Override
    public boolean isBanned() {
        return this.banned;
    }

    @Override
    public DataStorePlayer setBanned(boolean ban) {
        this.banned = ban;
        return this;
    }

    @Override
    public DataStorePlayer setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Player setUsername(String username) {
        return this.player.setUsername(username);
    }

    @Override
    public String getUsername() {
        return this.player.getUsername();
    }

    @Override
    public boolean isPremium() {
        return this.player.isPremium();
    }

    @Override
    public Player setUUID(String uuid) {
        return this.player.setUUID(uuid);
    }

    @Override
    public String getUUID() {
        return this.player.getUUID();
    }

    @Override
    public String toString() {
        return this.player.toString();
    }
}
