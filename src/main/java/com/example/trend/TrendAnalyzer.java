package com.example.trend;

import java.util.ArrayList;
import java.util.List;

public class TrendAnalyzer {
    public static StabilityResult analyze(List<Double> series, List<String> times, double threshold) {
        List<Jump> jumps = new ArrayList<>();
        double maxJump = 0.0;
        int stable = 0;

        for (int i = 1; i < series.size(); i++) {
            double diff = Math.abs(series.get(i) - series.get(i - 1));

            if (diff < threshold) {
                stable++;
            } else {
                String tFrom = (i - 1) < times.size() ? times.get(i - 1) : String.valueOf(i - 1);
                String tTo   = i < times.size() ? times.get(i) : String.valueOf(i);
                jumps.add(new Jump(i - 1, tFrom, tTo, diff));
            }

            if (diff > maxJump) maxJump = diff;
        }

        int totalPairs = Math.max(0, series.size() - 1);
        return new StabilityResult(totalPairs, stable, maxJump, jumps);
    }
}
