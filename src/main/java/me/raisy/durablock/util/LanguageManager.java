package me.raisy.durablock.util;

import me.raisy.durablock.DuraBlockPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageManager {
    private final DuraBlockPlugin plugin;
    private YamlConfiguration languageConfig;

    public LanguageManager(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveDefaultLanguageFiles() {
        // Save en_us.yml if it doesn't exist
        File enUsFile = new File(plugin.getDataFolder(), "lang/en_us.yml");
        if (!enUsFile.exists()) {
            plugin.saveResource("lang/en_us.yml", false);
        }

        // Save tr_tr.yml if it doesn't exist
        File trTrFile = new File(plugin.getDataFolder(), "lang/tr_tr.yml");
        if (!trTrFile.exists()) {
            plugin.saveResource("lang/tr_tr.yml", false);
        }
    }

    public void loadLanguage() {
        String langKey = plugin.getConfig().getString("language", "en_us");
        File langFile = new File(plugin.getDataFolder(), "lang/" + langKey + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file " + langKey + ".yml not found! Using default (en_us) language instead.");

            langFile = new File(plugin.getDataFolder(), "lang/" + "en_us.yml");
        }

        languageConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public void reloadLanguage() {
        saveDefaultLanguageFiles();
        loadLanguage();
    }

    public String getString(String key) {
        return languageConfig.getString(key, "Missing value in language file: " + key);
    }

    public String getString(String key, Boolean prependPrefix) {
        String value = languageConfig.getString(key, "Missing value in language file: " + key);

        if (prependPrefix) {
            return plugin.getConfigManager().getPrefix() + value;
        }

        return value;
    }

    public Component getDeserializedString(String key) {
        return MiniMessage.miniMessage().deserialize(getString(key));
    }

    public Component getDeserializedString(String key, Boolean prependPrefix) {
        Component value = MiniMessage.miniMessage().deserialize(getString(key));

        if (prependPrefix) {
            return Component.empty().append(MiniMessage.miniMessage().deserialize(plugin.getConfigManager().getPrefix())).append(value);
        }

        return value;
    }
}
