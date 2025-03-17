package me.raisy.durablock.command;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class ListBlocksCommand implements CommandExecutor {

    private DuraBlockPlugin plugin;

    public ListBlocksCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getDeserializedString("no-console"));
            return true;
        }

        List<CustomBlocksEntity> customBlocks;
        try {
            customBlocks = plugin.getCustomBlocksService().getAllCustomBlocks();

            if (customBlocks.isEmpty()) {
                player.sendMessage(plugin.getLanguageManager().getDeserializedString("blocks-not-found"));
                return true;
            }

            for (CustomBlocksEntity customBlock : customBlocks) {

                Component deleteButton = Component.text("[X] ", NamedTextColor.RED)
                        .hoverEvent(HoverEvent.showText(plugin.getLanguageManager().getDeserializedString("click-to-delete")))
                        .clickEvent(ClickEvent.runCommand("/deletecustomblock " + customBlock.getId()));

                Component commandMessage = MiniMessage.miniMessage().deserialize(plugin.getLanguageManager().getString("list-command-message")
                        .replace("{block_type}", customBlock.getBlockType())
                        .replace("{location}", customBlock.getLocation())
                        .replace("{current_durability}", Integer.toString(customBlock.getCurrentDurability())));

                Component message = Component.text()
                        .append(deleteButton)
                        .append(commandMessage)
                        .build();

                player.sendMessage(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
