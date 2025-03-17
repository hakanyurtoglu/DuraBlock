package me.raisy.durablock.listener;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;
import java.util.List;

public class BlockBreakListener implements Listener {
    private final DuraBlockPlugin plugin;

    public BlockBreakListener(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        try {
            CustomBlocksEntity customBlocksEntity = plugin.getCustomBlocksService().getBlock(block.getLocation());

            if (customBlocksEntity == null) return;
            event.setCancelled(true);

            BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
            Hologram hologram = plugin.getHolograms().get(block.getLocation());

            if (blockType.getPermission() != null && !player.hasPermission(blockType.getPermission())) {
                player.sendMessage(plugin.getLanguageManager().getString("no-permission-block-break").replace("{block_type}", blockType.getName()));
                return;
            }

            int currentDurability = customBlocksEntity.getCurrentDurability();
            int newDurability = currentDurability - 1;


            if (newDurability > 0) {
                customBlocksEntity.setCurrentDurability(currentDurability - 1);

                plugin.getCustomBlocksService().updateCustomBlock(customBlocksEntity);

                List<String> lines = blockType.getEnabledHologramLines();
                List<String> parsedLines = lines.stream()
                        .map(line -> line.replace("{durability}", Integer.toString(newDurability)))
                        .toList();

                plugin.getHologramManager().updateHologram(hologram, parsedLines);
            } else {
                if (currentDurability == 0) return;

                List<String> lines = blockType.getDisabledhologramLines();
                List<String> parsedLines = lines.stream()
                        .map(line -> line.replace("{durability}", Integer.toString(newDurability)))
                        .toList();

                customBlocksEntity.setCurrentDurability(0);
                plugin.getCustomBlocksService().updateCustomBlock(customBlocksEntity);

                plugin.getHologramManager().updateHologram(hologram, parsedLines);

                player.sendMessage("You have broken the block.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
