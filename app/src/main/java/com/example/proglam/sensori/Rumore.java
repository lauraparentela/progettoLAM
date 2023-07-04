package com.example.proglam.sensori;

public class Rumore implements Misurazioni {
    private static final String UNIT = "dB";
    private int level;

    public Rumore(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getUnit() {
        return UNIT;
    }
}
