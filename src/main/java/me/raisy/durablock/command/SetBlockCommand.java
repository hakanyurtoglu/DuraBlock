package me.raisy.durablock.command;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.model.CustomBlock;
import me.raisy.durablock.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SetBlockCommand implements TabExecutor {
    private final DuraBlockPlugin plugin;

    public SetBlockCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            plugin.getLogger().warning(plugin.getLanguageManager().getString("no-console"));
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            player.sendMessage(plugin.getLanguageManager().getDeserializedString("no-target-block"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getLanguageManager().getDeserializedString("no-block-type"));
            return true;
        }

        String blockTypeArg = args[0];
        BlockType blockType = plugin.getBlockTypes().get(blockTypeArg);

        if (blockType == null) {
            player.sendMessage(plugin.getLanguageManager().getDeserializedString("invalid-block-type"));
            return true;
        }

        String locationString = LocationUtil.locationToString(targetBlock.getLocation());

        // Check if the block is already in db
        try {
            if (plugin.getCustomBlocksService().isBlockExists(targetBlock.getLocation())) {
                player.sendMessage(plugin.getLanguageManager().getDeserializedString("block-already-exists"));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //TODO: check is material valid in config manager
        // Set target block to enabled block material
//        Material material = blockType.getEnabledBlockMaterial();
//        targetBlock.setType(material);

        // Create hologram
//        String hologramName = "durablock-" + targetBlock.getLocation().getBlockX() + targetBlock.getLocation().getBlockY() + targetBlock.getLocation().getBlockZ();
//        Location hologramLocation = targetBlock.getLocation().clone().add(0, blockType.getyLevel(), 0).toCenterLocation();
//        Hologram hologram = DHAPI.createHologram(hologramName, hologramLocation, blockType.getEnabledHologramLines());
        Location hologramLocation = targetBlock.getLocation().clone().add(0, blockType.getyLevel(), 0).toCenterLocation();
        Hologram hologram = plugin.getHologramManager().createHologram(hologramLocation, blockType.getEnabledHologramLines(), blockType.getDefaultDurability());

        plugin.getHolograms().put(targetBlock.getLocation(), hologram);

        // Create custom block
        CustomBlock customBlock = new CustomBlock();
        customBlock.setBlockType(blockType);
        customBlock.setLocation(targetBlock.getLocation());
        customBlock.setCurrentDurability(blockType.getDefaultDurability());
        customBlock.setHologram(hologram);


        // Insert data to db
        try {
            plugin.getCustomBlocksService().addCustomBlock(customBlock);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

//        plugin.getCustomBlocks().put(targetBlock.getLocation(), customBlock);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return new ArrayList<>(plugin.getBlockTypes().keySet());
        }

        return List.of();
    }
}
