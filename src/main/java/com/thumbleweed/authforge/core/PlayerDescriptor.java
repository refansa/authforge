package com.thumbleweed.authforge.core;

public class PlayerDescriptor {

    private final double x;
    private final double y;
    private final double z;

    public PlayerDescriptor(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}