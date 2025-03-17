package me.raisy.durablock.model;

import org.bukkit.Material;

import java.util.List;

public class BlockType {
    private String name;
    private int defaultDurability;
    private String permission;
    private Material enabledBlockMaterial;
    private Material disabledBLockMaterial;
    private List<String> enabledHologramLines;
    private List<String> disabledhologramLines;
    private int yLevel;


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

    public Material getEnabledBlockMaterial() {
        return enabledBlockMaterial;
    }

    public void setEnabledBlockMaterial(Material enabledBlockMaterial) {
        this.enabledBlockMaterial = enabledBlockMaterial;
    }

    public Material getDisabledBLockMaterial() {
        return disabledBLockMaterial;
    }

    public void setDisabledBLockMaterial(Material disabledBLockMaterial) {
        this.disabledBLockMaterial = disabledBLockMaterial;
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
}
