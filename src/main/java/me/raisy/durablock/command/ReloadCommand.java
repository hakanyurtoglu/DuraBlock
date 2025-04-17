package me.raisy.durablock.command;

import me.raisy.durablock.DuraBlockPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ReloadCommand implements CommandExecutor {
    private final DuraBlockPlugin plugin;

    public ReloadCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        try {
            plugin.getLanguageManager().loadLanguage();
            plugin.reloadConfig();
            plugin.getBlockTypes().clear();
            plugin.getConfigManager().loadBlockTypes();
            plugin.getConfigManager().reloadCustomBlocks();
            plugin.getTaskManager().startTasks();

            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Custom blocks reloaded successfully"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
