package me.raisy.durablock;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.command.DurablockCommand;
import me.raisy.durablock.database.CustomBlocksService;
import me.raisy.durablock.listener.BlockBreakListener;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.util.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
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

    private BukkitAudiences adventure;

    public @Nonnull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        updateChecker = new UpdateChecker(this);
        dateUtil = new DateUtil(this);

        this.adventure = BukkitAudiences.create(this);

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

            int pluginId = 25981;
            Metrics metrics = new Metrics(this, pluginId);
        }, 100L);

    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
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
