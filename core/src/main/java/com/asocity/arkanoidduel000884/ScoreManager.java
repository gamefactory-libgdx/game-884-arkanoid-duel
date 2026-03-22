package com.asocity.arkanoidduel000884;

public class ScoreManager {

    public int   currentScore;
    public float multiplier;

    public ScoreManager() {
        reset();
    }

    public void reset() {
        currentScore = 0;
        multiplier   = 1f;
    }

    public void add(int base) {
        currentScore += (int)(base * multiplier);
    }

    public void addLevelClear(int level, int livesLeft) {
        currentScore += Constants.SCORE_LEVEL_CLEAR_BASE * level
                      + Constants.SCORE_LIFE_BONUS * livesLeft;
    }

    public void setMultiballMultiplier(boolean active) {
        multiplier = active ? Constants.SCORE_MULTIBALL_MULT : 1f;
    }
}
