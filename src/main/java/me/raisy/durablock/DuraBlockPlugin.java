package me.raisy.durablock;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.command.DurablockCommand;
import me.raisy.durablock.database.CustomBlocksService;
import me.raisy.durablock.listener.BlockBreakListener;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class DuraBlockPlugin extends JavaPlugin {
    // Map<String blockName, BlockType>
    // Map<Location blockLocation, Hologram>
    private final Map<String, BlockType> blockTypes = new HashMap<>();
    private final Map<Location, Hologram> holograms = new HashMap<>();
    private final HologramManager hologramManager = new HologramManager(this);
    private final TaskManager taskManager = new TaskManager(this);
    private UpdateChecker updateChecker;
    private DateUtil dateUtil;
    private LanguageManager languageManager;
    private CustomBlocksService customBlocksService;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        updateChecker = new UpdateChecker(this);
        dateUtil = new DateUtil(this);

        saveDefaultConfig();

        configManager.loadBlockTypes();
        languageManager.saveDefaultLanguageFiles();
        languageManager.loadLanguage();

        getCommand("durablock").setExecutor(new DurablockCommand(this));

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(updateChecker, this);

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            customBlocksService = new CustomBlocksService(getDataFolder().getAbsolutePath() + "/data.db");

            configManager.loadCustomBlocks();

            taskManager.startTasks();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load database: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
            updateChecker.checkForUpdates().thenAccept(hasUpdate -> {
                if (hasUpdate) {
                    updateChecker.notifyConsole();
                }
            });
        }, 100L);

    }


    public Map<String, BlockType> getBlockTypes() {
        return blockTypes;
    }

    public CustomBlocksService getCustomBlocksService() {
        return customBlocksService;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Map<Location, Hologram> getHolograms() {
        return holograms;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public DateUtil getDateUtil() {
        return dateUtil;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
