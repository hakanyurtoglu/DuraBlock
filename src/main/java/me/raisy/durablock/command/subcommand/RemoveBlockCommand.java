package me.raisy.durablock.command.subcommand;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class RemoveBlockCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public RemoveBlockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a specific custom block by ID";
    }

    @Override
    public List<String> getArguments() {
        return List.of("block-id");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(plugin.getLanguageManager().getString("no-console"));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /durablock remove <block-id>");
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            plugin.adventure().sender(sender).sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-args"));
            return true;
        }

        try {
            boolean isDeleted = plugin.getCustomBlocksService().removeCustomBlockById(id);
            if (isDeleted) {
                plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("deleted-successfully"));
                plugin.getConfigManager().reloadCustomBlocks();
            } else {
                plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("failed-to-delete"));
            }
        } catch (SQLException e) {
            plugin.adventure().sender(sender).sendMessage(Component.text("A database error occurred. Please check the logs.", NamedTextColor.RED));
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
