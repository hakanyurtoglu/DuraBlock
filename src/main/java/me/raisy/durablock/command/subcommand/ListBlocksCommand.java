package me.raisy.durablock.command.subcommand;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class ListBlocksCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public ListBlocksCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all configured custom blocks";
    }

    @Override
    public List<String> getArguments() {
        return List.of();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.adventure().sender(sender).sendMessage(plugin.getLanguageManager().getDeserializedString("no-console"));
            return true;
        }

        List<CustomBlocksEntity> customBlocks;
        try {
            customBlocks = plugin.getCustomBlocksService().getAllCustomBlocks();

            if (customBlocks.isEmpty()) {
                plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("blocks-not-found"));
                return true;
            }

            for (CustomBlocksEntity customBlock : customBlocks) {

                Component deleteButton = Component.text("[X] ", NamedTextColor.RED)
                        .hoverEvent(HoverEvent.showText(plugin.getLanguageManager().getDeserializedString("click-to-delete")))
                        .clickEvent(ClickEvent.runCommand("/durablock remove " + customBlock.getId()));

                Component restoreButton = Component.text("[↺] ", NamedTextColor.YELLOW)
                        .hoverEvent(HoverEvent.showText(plugin.getLanguageManager().getDeserializedString("click-to-restore")))
                        .clickEvent(ClickEvent.runCommand("/durablock restore " + customBlock.getId()));

                Component commandMessage = MiniMessage.miniMessage().deserialize(plugin.getLanguageManager().getString("list-command-message")
                        .replace("{block_type}", customBlock.getBlockType())
                        .replace("{location}", customBlock.getLocation())
                        .replace("{block_id}", String.valueOf(customBlock.getId()))
                        .replace("{current_durability}", Integer.toString(customBlock.getCurrentDurability())));

                Component message = Component.text()
                        .append(deleteButton)
                        .append(restoreButton)
                        .append(commandMessage)
                        .build();

                plugin.adventure().player(player).sendMessage(message);
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
