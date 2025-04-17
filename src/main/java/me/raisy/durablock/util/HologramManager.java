package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HologramManager {
    private final DuraBlockPlugin plugin;
    private BukkitTask updateTask;

    public HologramManager(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public Hologram createHologram(Location hologramLocation, List<String> lines, int durability) {
        String hologramName = "durablock-" + hologramLocation.getBlockX() + "-" + hologramLocation.getBlockY() + "-" + hologramLocation.getBlockZ();
        List<String> parsedLines = lines.stream()
                .map(line -> line.replace("{durability}", Integer.toString(durability)))
                .toList();


        return DHAPI.createHologram(hologramName, hologramLocation, parsedLines);
    }

    public void updateHologram(Hologram hologram, List<String> lines) {
        DHAPI.setHologramLines(hologram, lines);
    }

    public void updateDisabledBlockHolograms() {
        Map<Location, Hologram> holograms = plugin.getHolograms();


        for (Map.Entry<Location, Hologram> entry : holograms.entrySet()) {

            try {
                Location location = entry.getKey();
                Hologram hologram = entry.getValue();
                CustomBlocksEntity customBlocksEntity = plugin.getCustomBlocksService().getBlock(location);
                if (customBlocksEntity == null || customBlocksEntity.getStatus().equals("enabled")) continue;

                BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
                String remaining = plugin.getDateUtil().formatTimeLeft(customBlocksEntity.getLastBrokenDate(), blockType.getRestoreInterval());

                List<String> parsedLines = blockType.getDisabledhologramLines().stream()
                        .map(line -> line.replace("{last_player}", customBlocksEntity.getLastPlayer()))
                        .map(line -> line.replace("{restore}", remaining))
                        .toList();

                updateHologram(hologram, parsedLines);
//                plugin.getLogger().info("Updated: " + hologram.getName());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAllHolograms() {
//        try {
//            List<CustomBlocksEntity> customBlocks = plugin.getCustomBlocksService().getAllCustomBlocks();
//
//            for (CustomBlocksEntity customBlock : customBlocks) {
//                BlockType blockType = plugin.getBlockTypes().get(customBlock.getBlockType());
//                Location hologramLocation = LocationUtil.stringToLocation(customBlock.getLocation()).clone().add(0, blockType.getyLevel(), 0).toCenterLocation();
//                String hologramName = "durablock-" + hologramLocation.getBlockX() + "-" + hologramLocation.getBlockY() + "-" + hologramLocation.getBlockZ();
//
//                removeHologram(hologramName);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        for (Hologram hologram : plugin.getHolograms().values()) {
            hologram.destroy();

        }
    }

    public void removeHologram(String hologramName) {
        DHAPI.getHologram(hologramName).destroy();
    }


}
