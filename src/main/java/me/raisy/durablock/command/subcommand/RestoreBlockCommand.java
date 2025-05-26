package me.raisy.durablock.command.subcommand;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.List;

public class RestoreBlockCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public RestoreBlockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "restore";
    }

    @Override
    public String getDescription() {
        return "Restores all or specified blocks.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /durablock restore <block-id>");
            return true;
        }

        String blockId = args[1];

        if (!isNumericString(blockId)) {
            sender.sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-args"));
            return true;
        }

        try {
            CustomBlocksEntity customBlocksEntity = plugin.getCustomBlocksService().getBlockById(Integer.parseInt(blockId));
            if (customBlocksEntity == null) {
                sender.sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-args"));
                return true;
            }
            BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
            int defaultDurability = blockType.getDefaultDurability();

            if (customBlocksEntity.getCurrentDurability() == defaultDurability) {
                sender.sendMessage(plugin.getLanguageManager().getDeserializedString("block-already-enabled"));
            } else {
                plugin.getCustomBlocksService().restoreCustomBlock(customBlocksEntity, defaultDurability);

                // Update hologram
                plugin.getHologramManager().updateHologramValues(blockType, customBlocksEntity, defaultDurability);

                sender.sendMessage(plugin.getLanguageManager().getDeserializedString("restored-successfully"));
            }

            return true;
        } catch (SQLException e) {
            sender.sendMessage(Component.text("A database error occurred. Please check the logs.", NamedTextColor.RED));
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    private boolean isNumericString(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
