package com.thumbleweed.authforge.core;

/**
 * Implementation of the Player interface.
 */
public class PlayerImpl implements Player {
    private String username;
    private String uuid;

    public PlayerImpl() {
        this("", "");
    }

    public PlayerImpl(String username, String uuid) {
        this.setUsername(username);
        this.setUUID(uuid);
    }

    @Override
    public Player setUsername(String username) {
        this.username = username == null ? "" : username.trim();
        return this;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isPremium() {
        return !this.getUUID().isEmpty();
    }

    @Override
    public Player setUUID(String uuid) {
        if (uuid == null) uuid = "";
        else if (uuid.length() == 32) {
            uuid = String.format(
                    "%s-%s-%s-%s-%s",
                    uuid.substring(0, 8),
                    uuid.substring(8, 12),
                    uuid.substring(12, 16),
                    uuid.substring(16, 20),
                    uuid.substring(20, 32)
            );
        } else {
            uuid = "";
        }
        this.uuid = uuid;

        return this;
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public String toString() {
        return String.format("Username: %s, UUID: %s", this.getUsername(), this.getUUID());
    }
}
