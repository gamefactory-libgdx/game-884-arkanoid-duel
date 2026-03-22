package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public final class SaveData {

    private SaveData() {}

    private static Preferences prefs() {
        return Gdx.app.getPreferences(Constants.PREFS_NAME);
    }

    // ── High score ────────────────────────────────────────────────────────────

    public static int getHighScore() {
        return prefs().getInteger(Constants.PREF_HIGH_SCORE, 0);
    }

    public static void setHighScore(int v) {
        if (v > getHighScore()) {
            Preferences p = prefs();
            p.putInteger(Constants.PREF_HIGH_SCORE, v);
            p.flush();
        }
    }

    // ── Level unlock / stars / best score ─────────────────────────────────────

    public static int getMaxLevelUnlocked() {
        return prefs().getInteger(Constants.PREF_MAX_LEVEL, 1);
    }

    public static void unlockLevel(int n) {
        int cur = getMaxLevelUnlocked();
        if (n > cur) {
            Preferences p = prefs();
            p.putInteger(Constants.PREF_MAX_LEVEL, n);
            p.flush();
        }
    }

    public static int getLevelStars(int n) {
        return prefs().getInteger(Constants.prefLevelStars(n), 0);
    }

    public static void setLevelStars(int n, int stars) {
        if (stars > getLevelStars(n)) {
            Preferences p = prefs();
            p.putInteger(Constants.prefLevelStars(n), stars);
            p.flush();
        }
    }

    public static int getLevelBestScore(int n) {
        return prefs().getInteger(Constants.prefLevelBest(n), 0);
    }

    public static void setLevelBestScore(int n, int s) {
        if (s > getLevelBestScore(n)) {
            Preferences p = prefs();
            p.putInteger(Constants.prefLevelBest(n), s);
            p.flush();
        }
    }

    // ── Tutorial ──────────────────────────────────────────────────────────────

    public static boolean isTutorialDone() {
        return prefs().getBoolean(Constants.PREF_TUTORIAL_DONE, false);
    }

    public static void setTutorialDone() {
        Preferences p = prefs();
        p.putBoolean(Constants.PREF_TUTORIAL_DONE, true);
        p.flush();
    }

    // ── Leaderboard ───────────────────────────────────────────────────────────

    public static class LeaderboardEntry {
        public String name;
        public int    score;
        public LeaderboardEntry() {}
        public LeaderboardEntry(String name, int score) {
            this.name  = name;
            this.score = score;
        }
    }

    public static Array<LeaderboardEntry> getLeaderboard() {
        Array<LeaderboardEntry> list = new Array<>();
        String json = prefs().getString(Constants.PREF_LEADERBOARD, "[]");
        try {
            Json j = new Json();
            Array<LeaderboardEntry> parsed = j.fromJson(Array.class, LeaderboardEntry.class, json);
            if (parsed != null) list = parsed;
        } catch (Exception e) {
            Gdx.app.error("SaveData", "Leaderboard parse error", e);
        }
        return list;
    }

    public static void addLeaderboardEntry(String name, int score) {
        Array<LeaderboardEntry> list = getLeaderboard();
        list.add(new LeaderboardEntry(name, score));
        list.sort((a, b) -> b.score - a.score);
        while (list.size > Constants.LEADERBOARD_MAX_ENTRIES) list.removeIndex(list.size - 1);
        Json j = new Json();
        Preferences p = prefs();
        p.putString(Constants.PREF_LEADERBOARD, j.toJson(list));
        p.flush();
    }

    public static void clearLeaderboard() {
        Preferences p = prefs();
        p.putString(Constants.PREF_LEADERBOARD, "[]");
        p.flush();
    }

    // ── Audio / settings ──────────────────────────────────────────────────────

    public static float getMusicVolume() {
        return prefs().getFloat(Constants.PREF_MUSIC_VOLUME, Constants.DEFAULT_MUSIC_VOLUME);
    }

    public static void setMusicVolume(float v) {
        Preferences p = prefs();
        p.putFloat(Constants.PREF_MUSIC_VOLUME, v);
        p.flush();
    }

    public static float getSfxVolume() {
        return prefs().getFloat(Constants.PREF_SFX_VOLUME, Constants.DEFAULT_SFX_VOLUME);
    }

    public static void setSfxVolume(float v) {
        Preferences p = prefs();
        p.putFloat(Constants.PREF_SFX_VOLUME, v);
        p.flush();
    }

    public static boolean isVibrationEnabled() {
        return prefs().getBoolean(Constants.PREF_VIBRATION, true);
    }

    public static void setVibrationEnabled(boolean v) {
        Preferences p = prefs();
        p.putBoolean(Constants.PREF_VIBRATION, v);
        p.flush();
    }

    /** Checks if the given score qualifies for the top-10 leaderboard. */
    public static boolean isTopScore(int score) {
        Array<LeaderboardEntry> list = getLeaderboard();
        if (list.size < Constants.LEADERBOARD_MAX_ENTRIES) return score > 0;
        return score > list.get(list.size - 1).score;
    }
}
