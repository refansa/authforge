package com.thumbleweed.authforge.core;

public class PayloadImpl implements Payload {

    private final Player player;
    private final String[] arguments;

    public PayloadImpl(Player player, String[] args) {
        this.player = player;
        this.arguments = args;
    }

    public PayloadImpl(Player player) {
        this(player, new String[] {});
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String[] getArgs() {
        return this.arguments;
    }
}