package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import org.bukkit.Location;

import java.util.List;

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
