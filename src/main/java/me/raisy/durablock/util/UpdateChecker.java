package me.raisy.durablock.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.raisy.durablock.DuraBlockPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker implements Listener {
    private final DuraBlockPlugin plugin;
    private final String githubRepoUrl = "hakanyurtoglu/durablock";
    private final String currentVersion;
    private String latestVersion = null;
    private boolean updateAvailable = false;

    public UpdateChecker(DuraBlockPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String apiUrl = "https://api.github.com/repos/" + githubRepoUrl + "/releases/latest";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "DuraBlock-UpdateChecker/1.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    reader.close();

                    latestVersion = json.get("tag_name").getAsString();
                    updateAvailable = isNewerVersion(currentVersion, latestVersion);

                    return updateAvailable;
                }
            } catch (IOException error) {
                plugin.getLogger().warning("Error checking for updates: " + error.getMessage());
                return false;
            }

            return false;
        });
    }

    private boolean isNewerVersion(String current, String latest) {
        try {
            current = current.startsWith("v") ? current.substring(1) : current;
            latest = latest.startsWith("v") ? latest.substring(1) : latest;

            String[] currentParts = current.split("\\.");
            String[] latestParts = latest.split("\\.");

            int maxLength = Math.max(currentParts.length, latestParts.length);

            for (int i = 0; i < maxLength; i++) {
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

                if (latestPart > currentPart) {
                    return true;
                } else if (latestPart < currentPart) {
                    return false;
                }
            }

            return false;
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Error parsing version numbers: " + e.getMessage());
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("durablock.notify.update")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                notifyPlayer(player);
            }, 60L);
        }
    }

    public void notifyConsole() {
        if (updateAvailable && latestVersion != null) {
            String separator = "===========================================";

            plugin.getComponentLogger().info(Component.text(separator, NamedTextColor.DARK_GRAY));
            plugin.getComponentLogger().info(Component.text("DuraBlock plugin update available!", NamedTextColor.LIGHT_PURPLE));
            plugin.getComponentLogger().info(Component.text("Current version: ", NamedTextColor.YELLOW).append(Component.text(currentVersion, NamedTextColor.RED)));
            plugin.getComponentLogger().info(Component.text("Latest version: ", NamedTextColor.YELLOW).append(Component.text(latestVersion, NamedTextColor.GREEN)));
            plugin.getComponentLogger().info(Component.text(separator, NamedTextColor.DARK_GRAY));
        }
    }

    public void notifyPlayer(Player player) {
        if (updateAvailable && latestVersion != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                String notifierMessage = plugin.getLanguageManager().getString("update-notifier", true).replace("{current}", currentVersion).replace("{latest}", latestVersion);
                Component component = MiniMessage.miniMessage().deserialize(notifierMessage);
                player.sendMessage(component);
            });
        }
    }
}
