package me.raisy.durablock.util;

import me.raisy.durablock.DuraBlockPlugin;

public class DateUtil {
    private final DuraBlockPlugin plugin;

    public DateUtil(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public static boolean isRestoreTimePassed(long lastBrokenDate, int restoreIntervalSeconds) {
        long currentTime = System.currentTimeMillis();
        long restoreIntervalMillis = restoreIntervalSeconds * 1000L;

        return currentTime >= (lastBrokenDate + restoreIntervalMillis);
    }

    public String formatTimeLeft(long lastBrokenDate, int restoreIntervalSeconds) {
        long restoreTime = lastBrokenDate + (restoreIntervalSeconds * 1000L);
        long currentTime = System.currentTimeMillis();

        long remainingMillis = restoreTime - currentTime;

        if (remainingMillis <= 0) {
            return plugin.getLanguageManager().getString("restoration-ready");
        }

        long seconds = remainingMillis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        String h = plugin.getLanguageManager().getString("date.hour");
        String m = plugin.getLanguageManager().getString("date.minute");
        String s = plugin.getLanguageManager().getString("date.second");

        if (hours > 0) sb.append(hours).append(h).append(" ");
        if (minutes > 0 || hours > 0) sb.append(minutes).append(m).append(" ");
        sb.append(secs).append(s).append(" ");

        return sb.toString().trim();
    }
}
