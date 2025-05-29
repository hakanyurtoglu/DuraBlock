package me.raisy.durablock.command.subcommand;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.util.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddBlockCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public AddBlockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adds a new custom block of the specified type";
    }

    @Override
    public List<String> getArguments() {
        return List.of("block-type");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(plugin.getLanguageManager().getString("no-console"));
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("no-target-block"));
            return true;
        }

        if (args.length < 2) {
            plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("no-block-type"));
            return true;
        }

        String blockTypeArg = args[1];
        BlockType blockType = plugin.getBlockTypes().get(blockTypeArg);

        if (blockType == null) {
            plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-block-type"));
            return true;
        }

        String locationString = LocationUtil.locationToString(targetBlock.getLocation());

        // Check if the block is already in db
        try {
            if (plugin.getCustomBlocksService().isBlockExists(targetBlock.getLocation())) {
                plugin.adventure().player(player).sendMessage(plugin.getLanguageManager().getDeserializedString("block-already-exists"));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Create hologram
        Location hologramLocation = LocationUtil.centerLocation(targetBlock.getLocation().clone().add(0, blockType.getyLevel(), 0));
        Hologram hologram = plugin.getHologramManager().createHologram(hologramLocation, blockType.getEnabledHologramLines(), blockType.getDefaultDurability());
        plugin.getHolograms().put(targetBlock.getLocation(), hologram);

        // Create custom block
        CustomBlocksEntity customBlocksEntity = new CustomBlocksEntity();
        customBlocksEntity.setBlockType(blockType.getName());
        customBlocksEntity.setLocation(locationString);
        customBlocksEntity.setCurrentDurability(blockType.getDefaultDurability());

        // Insert data to db
        try {
            plugin.getCustomBlocksService().addCustomBlock(customBlocksEntity);
        } catch (SQLException e) {
            plugin.adventure().sender(sender).sendMessage(Component.text("A database error occurred. Please check the logs.", NamedTextColor.RED));
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(plugin.getBlockTypes().keySet());
        }

        return List.of();
    }
}
