package me.raisy.durablock.command;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.subcommand.*;
import me.raisy.durablock.command.subcommand.ListBlocksCommand;
import me.raisy.durablock.command.subcommand.ReloadCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DurablockCommand implements TabExecutor {
    private final List<SubCommand> subCommands = new ArrayList<>();
    private final DuraBlockPlugin plugin;

    public DurablockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;

        subCommands.add(new AddBlockCommand(plugin));
        subCommands.add(new ListBlocksCommand(plugin));
        subCommands.add(new ReloadCommand(plugin));
        subCommands.add(new RemoveBlockCommand(plugin));
        subCommands.add(new RestoreBlockCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }

        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand == null) {
            sender.sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-subcommand"));
            return true;
        }

        return subCommand.execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> subCommands = Arrays.asList("reload", "list", "remove", "add", "restore");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);

            return completions;
        } else if (args.length == 2) {
            SubCommand subCommand = getSubCommand(args[0]);

            if (subCommand != null) {
                return subCommand.onTabComplete(sender, args);
            }
        }

        return List.of();
    }

    private SubCommand getSubCommand(String commandName) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(commandName)) {
                return subCommand;
            }
        }
        return null;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== DuraBlock Help ===", NamedTextColor.GOLD));

        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(Component.text(subCommand.getName() + " => " + subCommand.getDescription()));
        }
    }
}
