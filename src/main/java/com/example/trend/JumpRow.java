package com.example.trend;
public class JumpRow {
    public final String trend;
    public final int indexFrom;
    public final String timeFrom;
    public final String timeTo;
    public final double diff;
    public JumpRow(String trend, int indexFrom, String timeFrom, String timeTo, double diff) {
        this.trend = trend;
        this.indexFrom = indexFrom;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.diff = diff;
    }
}
