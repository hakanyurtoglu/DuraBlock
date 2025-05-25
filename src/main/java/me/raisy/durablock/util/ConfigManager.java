package me.raisy.durablock.util;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.model.Reward;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final DuraBlockPlugin plugin;

    public ConfigManager(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadBlockTypes() {
        ConfigurationSection blocksSection = plugin.getConfig().getConfigurationSection("blocks");

        for (String blockName : blocksSection.getKeys(false)) {
            String permission = blocksSection.getString(blockName + ".permission");
            int defaultDurability = blocksSection.getInt(blockName + ".durability");
            int yLevel = blocksSection.getInt(blockName + ".y-level");
            int restoreInterval = blocksSection.getInt(blockName + ".restore-interval");
            List<String> enabledHologramLines = blocksSection.getStringList(blockName + ".enabled-hologram-lines");
            List<String> disabledHologramLines = blocksSection.getStringList(blockName + ".disabled-hologram-lines");
            List<Map<?, ?>> rewardSection = blocksSection.getMapList(blockName + ".rewards");

            BlockType blockType = new BlockType();
            blockType.setName(blockName);
            blockType.setDefaultDurability(defaultDurability);
            blockType.setPermission(permission);
            blockType.setEnabledHologramLines(enabledHologramLines);
            blockType.setDisabledHologramLines(disabledHologramLines);
            blockType.setyLevel(yLevel);
            blockType.setRestoreInterval(restoreInterval);

            // Add rewards
            List<Reward> rewards = new ArrayList<>();
            for (Map<?, ?> entry : rewardSection) {
                String command = (String) entry.get("command");
                double chance = (double) entry.get("chance");
                rewards.add(new Reward(command, chance));
            }

            blockType.setRewards(rewards);

            plugin.getBlockTypes().put(blockName, blockType);
            plugin.getLogger().info("Block type " + blockName + " loaded");
        }
    }

    public void loadCustomBlocks() throws SQLException {
        List<CustomBlocksEntity> customBlocksEntities = plugin.getCustomBlocksService().getAllCustomBlocks();
        for (CustomBlocksEntity customBlocksEntity : customBlocksEntities) {

            BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
            int defaultDurability = blockType.getDefaultDurability();

            // Check if block's restore date is passed
            if (customBlocksEntity.getStatus().equals("disabled") && DateUtil.isRestoreTimePassed(customBlocksEntity.getLastBrokenDate(), blockType.getRestoreInterval())) {
                plugin.getCustomBlocksService().restoreCustomBlock(customBlocksEntity, defaultDurability);

                plugin.getLogger().info("Custom block enabled: " + customBlocksEntity.getId());
            }

            Location blockLocation = LocationUtil.stringToLocation(customBlocksEntity.getLocation());
            Location hologramLocation = blockLocation.clone().add(0, blockType.getyLevel(), 0).toCenterLocation();

            List<String> lines = customBlocksEntity.getCurrentDurability() <= 0 ? blockType.getDisabledHologramLines() : blockType.getEnabledHologramLines();
            Hologram hologram = plugin.getHologramManager().createHologram(hologramLocation, lines, customBlocksEntity.getCurrentDurability());

            plugin.getHolograms().put(blockLocation, hologram);
        }
    }

    public String getPrefix() {
        return plugin.getConfig().getString("prefix");
    }

    public void reloadCustomBlocks() throws SQLException {
        plugin.getHologramManager().removeAllHolograms();
        plugin.getHolograms().clear();
        loadCustomBlocks();
    }
}
