package com.example.proglam.sensori;

public class Wifi implements Misurazioni {
    private static final String UNIT = "dBm";
    private int level;

    public Wifi(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String getUnit() {
        return UNIT;
    }
}
