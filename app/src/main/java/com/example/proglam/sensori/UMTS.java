package com.example.proglam.sensori;

public class UMTS implements Misurazioni {
    private static final String UNIT = "UMTS";
    private int level;

    public UMTS(int level) {
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

    public void setLevel(int newLevel) {
        this.level = newLevel;
    }
}
