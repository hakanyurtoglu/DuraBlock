package me.raisy.durablock.command;

import me.raisy.durablock.DuraBlockPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

public class DeleteBlockCommand implements TabExecutor {
    private DuraBlockPlugin plugin;

    public DeleteBlockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(plugin.getLanguageManager().getString("no-console"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /deleteblock <id>");
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid ID.");
            return true;
        }

        try {
            boolean isDeleted = plugin.getCustomBlocksService().removeCustomBlockById(id);
            if (isDeleted) {
                player.sendMessage(plugin.getLanguageManager().getDeserializedString("deleted-successfully"));
                plugin.getConfigManager().reloadCustomBlocks();
            } else {
                player.sendMessage(plugin.getLanguageManager().getDeserializedString("failed-to-delete"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
