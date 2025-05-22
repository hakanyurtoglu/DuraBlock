package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HologramManager {
    private final DuraBlockPlugin plugin;

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

    public void updateHologramValues(BlockType blockType, CustomBlocksEntity customBlocksEntity, int durability) {
        Hologram hologram = plugin.getHolograms().get(LocationUtil.stringToLocation(customBlocksEntity.getLocation()));

        if (hologram == null) return;

        List<String> lines = blockType.getEnabledHologramLines();
        List<String> parsedLines = lines.stream()
                .map(line -> line.replace("{durability}", Integer.toString(durability)))
                .toList();

        plugin.getHologramManager().updateHologram(hologram, parsedLines);
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

                List<String> parsedLines = blockType.getDisabledHologramLines().stream()
                        .map(line -> line.replace("{last_player}", customBlocksEntity.getLastPlayer()))
                        .map(line -> line.replace("{restore}", remaining))
                        .toList();

                updateHologram(hologram, parsedLines);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeAllHolograms() {
        for (Hologram hologram : plugin.getHolograms().values()) {
            hologram.destroy();
        }
    }

    public void removeHologram(String hologramName) {
        DHAPI.getHologram(hologramName).destroy();
    }


}
