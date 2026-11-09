package com.github.realystupid.breadmc.role;

public class Role {
    private final String name;
    private final int breadThreshold;
    private final int color;

    public Role(String name, int breadThreshold, int color) {
        this.name = name;
        this.breadThreshold = breadThreshold;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getBreadThreshold() {
        return breadThreshold;
    }

    public Integer getColor() {
        return color;
    }
}
