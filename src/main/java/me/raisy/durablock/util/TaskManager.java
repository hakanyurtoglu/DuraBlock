package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.List;

public class TaskManager {
    private DuraBlockPlugin plugin;
    private BukkitTask updateDisabledHologramsTask;
    private BukkitTask restoreBlocksTask;

    public TaskManager(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTasks() {
        stopTasks();

        int hologramUpdateInterval = plugin.getConfig().getInt("hologram-update-interval");
        int restoreInterval = plugin.getConfig().getInt("block-restore-interval");

        updateDisabledHologramsTask = Bukkit.getScheduler().runTaskTimer(plugin, plugin.getHologramManager()::updateDisabledBlockHolograms, 20L, hologramUpdateInterval * 20L);
        restoreBlocksTask = Bukkit.getScheduler().runTaskTimer(plugin, this::restoreBlocks, 20L, restoreInterval * 20L);

        plugin.getLogger().info("Started hologram update task with interval: " + hologramUpdateInterval + " seconds");
    }

    public void restoreBlocks() {
        try {
            // Find blocks that need restoration (not active, and broken long enough ago)
            for (CustomBlocksEntity customBlocksEntity : plugin.getCustomBlocksService().getAllCustomBlocks()) {
                BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
                int defaultDurability = blockType.getDefaultDurability();
                int durabilityRestoreInterval = blockType.getRestoreInterval();

//                 Check if the block passed time
                Long lastBrokenDate = customBlocksEntity.getLastBrokenDate();
                if (lastBrokenDate == null) return;

                boolean isPassed = DateUtil.isRestoreTimePassed(lastBrokenDate, durabilityRestoreInterval);

                if (isPassed && customBlocksEntity.getStatus().equals("disabled")) {
                    plugin.getCustomBlocksService().restoreCustomBlock(customBlocksEntity, defaultDurability);
                    Hologram hologram = plugin.getHolograms().get(LocationUtil.stringToLocation(customBlocksEntity.getLocation()));

                    if (hologram == null) return;

//                     Update Hologram
                    List<String> lines = blockType.getEnabledHologramLines();
                    List<String> parsedLines = lines.stream()
                            .map(line -> line.replace("{durability}", Integer.toString(defaultDurability)))
                            .toList();

                    plugin.getHologramManager().updateHologram(hologram, parsedLines);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void stopTasks() {
        if (updateDisabledHologramsTask != null && !updateDisabledHologramsTask.isCancelled()) {
            updateDisabledHologramsTask.cancel();
            updateDisabledHologramsTask = null;
        }

        if (restoreBlocksTask != null && !restoreBlocksTask.isCancelled()) {
            restoreBlocksTask.cancel();
            restoreBlocksTask = null;
        }
    }


}
