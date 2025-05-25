package me.raisy.durablock.command.subcommand;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;

public class ReloadCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public ReloadCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        try {
            plugin.reloadConfig();
            plugin.getLanguageManager().reloadLanguage();
            plugin.getBlockTypes().clear();
            plugin.getConfigManager().loadBlockTypes();
            plugin.getConfigManager().reloadCustomBlocks();
            plugin.getTaskManager().startTasks();

            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Custom blocks reloaded successfully"));
        } catch (SQLException e) {
            sender.sendMessage(Component.text("A database error occurred. Please check the logs.", NamedTextColor.RED));
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
