package me.raisy.durablock.model;

import java.util.List;

public class BlockType {
    private String name;
    private int defaultDurability;
    private String permission;
    private List<String> enabledHologramLines;
    private List<String> disabledhologramLines;
    private int yLevel;
    private int restoreInterval;
    private List<Reward> rewards;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnabledHologramLines() {
        return enabledHologramLines;
    }

    public void setEnabledHologramLines(List<String> enabledHologramLines) {
        this.enabledHologramLines = enabledHologramLines;
    }

    public int getDefaultDurability() {
        return defaultDurability;
    }

    public void setDefaultDurability(int defaultDurability) {
        this.defaultDurability = defaultDurability;
    }

    public List<String> getDisabledhologramLines() {
        return disabledhologramLines;
    }

    public void setDisabledhologramLines(List<String> disabledhologramLines) {
        this.disabledhologramLines = disabledhologramLines;
    }

    public int getyLevel() {
        return yLevel;
    }

    public void setyLevel(int yLevel) {
        this.yLevel = yLevel;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getRestoreInterval() {
        return restoreInterval;
    }

    public void setRestoreInterval(int restoreInterval) {
        this.restoreInterval = restoreInterval;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }
}
