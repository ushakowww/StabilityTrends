package com.example.trend;

public class Jump {
    public final int indexFrom;
    public final String timeFrom;
    public final String timeTo;
    public final double diff;

    public Jump(int indexFrom, String timeFrom, String timeTo, double diff) {
        this.indexFrom = indexFrom;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.diff = diff;
    }
}
