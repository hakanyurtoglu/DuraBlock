package me.raisy.durablock.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();

    String getDescription();

    boolean execute(CommandSender sender, String[] args);

    public List<String> onTabComplete(CommandSender sender, String[] args);
}
