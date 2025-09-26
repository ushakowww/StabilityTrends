package com.example.trend;

import java.util.List;

public class StabilityResult {
    public final int totalPairs;
    public final int stablePairs;
    public final double maxJump;
    public final List<Jump> jumps;

    public StabilityResult(int totalPairs, int stablePairs, double maxJump, List<Jump> jumps) {
        this.totalPairs = totalPairs;
        this.stablePairs = stablePairs;
        this.maxJump = maxJump;
        this.jumps = jumps;
    }

    public int getJumpPairs() { return totalPairs - stablePairs; }

    public double getStabilityPercent() {
        return totalPairs == 0 ? 100.0 : (stablePairs * 100.0 / totalPairs);
    }
}
