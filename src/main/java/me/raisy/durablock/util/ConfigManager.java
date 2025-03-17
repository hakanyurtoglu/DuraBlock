package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ConfigManager {
    private final DuraBlockPlugin plugin;
    private File configFile;
    private YamlConfiguration config;

    public ConfigManager(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }


    public void loadBlockTypes() {
        ConfigurationSection blocksSection = plugin.getConfig().getConfigurationSection("blocks");

        for (String blockName : blocksSection.getKeys(false)) {
            String path = blockName;

            Material enabledMaterial = Material.getMaterial(blocksSection.getString(path + ".enabled-material"));
            Material disabledMaterial = Material.getMaterial(blocksSection.getString(path + ".disabled-material"));
            String permission = blocksSection.getString(path + ".permission");
            int defaultDurability = blocksSection.getInt(path + ".durability");
            int yLevel = blocksSection.getInt(path + ".y-level");
            List<String> enabledHologramLines = blocksSection.getStringList(path + ".enabled-hologram-lines");
            List<String> disabledHologramLines = blocksSection.getStringList(path + ".disabled-hologram-lines");

            BlockType blockType = new BlockType();
            blockType.setName(blockName);
            blockType.setDefaultDurability(defaultDurability);
//            blockType.setEnabledBlockMaterial(enabledMaterial);
            blockType.setPermission(permission);
            blockType.setDisabledBLockMaterial(disabledMaterial);
            blockType.setEnabledHologramLines(enabledHologramLines);
            blockType.setDisabledhologramLines(disabledHologramLines);
            blockType.setyLevel(yLevel);

            plugin.getBlockTypes().put(blockName, blockType);
            plugin.getLogger().info("Block type " + blockName + " loaded");

        }
    }

    public void loadCustomBlocks() throws SQLException {
        List<CustomBlocksEntity> customBlocksEntities = plugin.getCustomBlocksService().getAllCustomBlocks();
        for (CustomBlocksEntity customBlocksEntity : customBlocksEntities) {
            BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
            Location blockLocation = LocationUtil.stringToLocation(customBlocksEntity.getLocation());
            Location hologramLocation = blockLocation.clone().add(0, blockType.getyLevel(), 0).toCenterLocation();

            List<String> lines = customBlocksEntity.getCurrentDurability() <= 0 ? blockType.getDisabledhologramLines() : blockType.getEnabledHologramLines();
            Hologram hologram = plugin.getHologramManager().createHologram(hologramLocation, lines, customBlocksEntity.getCurrentDurability());

            plugin.getHolograms().put(blockLocation, hologram);
//            CustomBlock customBlock = new CustomBlock();
//            customBlock.setBlockType(blockType);
//            customBlock.setCurrentDurability(customBlocksEntity.getCurrentDurability());
//            customBlock.setId(customBlocksEntity.getId());
//            customBlock.setLocation(hologramLocation);
//            customBlock.setHologram(hologram);

        }

    }

    public void reloadCustomBlocks() throws SQLException {
        plugin.getHologramManager().removeAllHolograms();
        plugin.getCustomBlocks().clear();
        loadCustomBlocks();
    }

}
