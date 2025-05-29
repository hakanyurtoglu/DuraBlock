package me.raisy.durablock.command;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.subcommand.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DurablockCommand implements TabExecutor {
    private final List<SubCommand> subCommands = new ArrayList<>();
    private final DuraBlockPlugin plugin;

    public DurablockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;

        subCommands.add(new AddBlockCommand(plugin));
        subCommands.add(new InfoCommand(plugin));
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
            plugin.adventure().sender(sender).sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-subcommand"));
            return true;
        }

        return subCommand.execute(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> subCommandNames = subCommands.stream().map(SubCommand::getName).toList();
            StringUtil.copyPartialMatches(args[0], subCommandNames, completions);

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
        String dashes = "-".repeat(15);

        Component separator = Component.text(dashes, NamedTextColor.DARK_GRAY)
                .append(Component.text(" DuraBlock Help Menu ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(dashes, NamedTextColor.DARK_GRAY));

        plugin.adventure().sender(sender).sendMessage(separator);

        for (SubCommand subCommand : subCommands) {
            String argsString = subCommand.getArguments().stream().map(s -> "<" + s + ">")
                    .collect(Collectors.joining(" "));

            Component commandInfo = Component.text("/durablock ", NamedTextColor.GOLD)
                    .append(Component.text(subCommand.getName(), NamedTextColor.GREEN))
                    .append(Component.text(" " + argsString, NamedTextColor.GRAY))
                    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(subCommand.getDescription(), NamedTextColor.YELLOW));

            plugin.adventure().sender(sender).sendMessage(commandInfo);
        }

        plugin.adventure().sender(sender).sendMessage(separator);
    }
}
