package me.raisy.durablock.model;

public class Reward {
    private String command;
    private double chance;

    public Reward(String command, double chance) {
        this.command = command;
        this.chance = chance;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
