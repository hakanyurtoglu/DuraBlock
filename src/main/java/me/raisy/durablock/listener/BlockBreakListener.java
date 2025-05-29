package me.raisy.durablock.listener;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.model.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;
import java.util.List;

public class BlockBreakListener implements Listener {
    private final DuraBlockPlugin plugin;

    public BlockBreakListener(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        try {
            CustomBlocksEntity customBlocksEntity = plugin.getCustomBlocksService().getBlock(block.getLocation());

            if (customBlocksEntity == null) return;
            event.setCancelled(true);

            BlockType blockType = plugin.getBlockTypes().get(customBlocksEntity.getBlockType());
            Hologram hologram = plugin.getHolograms().get(block.getLocation());

            if (blockType.getPermission() != null && !player.hasPermission(blockType.getPermission())) {
                String permissionMessage = plugin.getLanguageManager().getString("no-permission-block-break").replace("{block_type}", blockType.getName());
                Component formatted = MiniMessage.miniMessage().deserialize(permissionMessage);

                plugin.adventure().player(player).sendMessage(formatted);
                return;
            }

            int currentDurability = customBlocksEntity.getCurrentDurability();
            int newDurability = currentDurability - 1;


            if (newDurability > 0) {
                customBlocksEntity.setCurrentDurability(currentDurability - 1);

                plugin.getCustomBlocksService().updateCustomBlock(customBlocksEntity);

                List<String> lines = blockType.getEnabledHologramLines();
                List<String> parsedLines = lines.stream()
                        .map(line -> line.replace("{durability}", Integer.toString(newDurability)))
                        .toList();

                plugin.getHologramManager().updateHologram(hologram, parsedLines);

                // Handle rewards
                List<Reward> rewards = blockType.getRewards();

                for (Reward reward : rewards) {
                    if (Math.random() <= reward.getChance()) {
                        String cmd = reward.getCommand().replace("{player}", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        break;
                    }
                }

            } else {
                if (currentDurability == 0) return;

                customBlocksEntity.setCurrentDurability(0);
                customBlocksEntity.setStatus("disabled");
                customBlocksEntity.setLastBrokenDate(System.currentTimeMillis());
                customBlocksEntity.setLastPlayer(player.getName());
                plugin.getCustomBlocksService().updateCustomBlock(customBlocksEntity);

                String remaining = plugin.getDateUtil().formatTimeLeft(customBlocksEntity.getLastBrokenDate(), blockType.getRestoreInterval());
                List<String> lines = blockType.getDisabledHologramLines();
                List<String> parsedLines = lines.stream()
                        .map(line -> line.replace("{last_player}", customBlocksEntity.getLastPlayer()))
                        .map(line -> line.replace("{restore}", remaining))
                        .toList();


                plugin.getHologramManager().updateHologram(hologram, parsedLines);

                // Execute commands
                List<String> commands = plugin.getConfig().getStringList("blocks." + blockType.getName() + ".on-break.commands");
                for (String command : commands) {
                    String parsedCommand = command.replace("{player}", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
                }

                // Send broadcast message
                boolean isBroadcastEnabled = plugin.getConfig().getBoolean("blocks." + blockType.getName() + ".on-break.broadcast.enabled");
                String broadcastMessage = plugin.getConfig().getString("blocks." + blockType.getName() + ".on-break.broadcast.message").replace("{player}", player.getName());


                if (isBroadcastEnabled && !broadcastMessage.isEmpty()) {
                    plugin.adventure().players().sendMessage(MiniMessage.miniMessage().deserialize(broadcastMessage));
                }

                // Play sound
                String basePath = "blocks." + blockType.getName() + ".on-break.sound";
                boolean isSoundEnabled = plugin.getConfig().getBoolean(basePath + ".enabled");
                if (!isSoundEnabled) return;

                String soundKey = plugin.getConfig().getString(basePath + ".sound-type");
                boolean playToEveryone = plugin.getConfig().getBoolean(basePath + ".play-to-everyone");

                if (soundKey == null || soundKey.isEmpty()) {
                    plugin.getLogger().severe("Sound key is missing for block type: " + blockType.getName());
                    return;
                }

                NamespacedKey namespacedKey = NamespacedKey.fromString(soundKey);
                if (namespacedKey == null) {
                    plugin.getLogger().severe("Invalid sound key: " + soundKey);
                    return;
                }

                Sound sound = Registry.SOUNDS.get(namespacedKey);
                if (sound == null) {
                    plugin.getLogger().severe("Sound not found in registry: " + soundKey);
                    return;
                }

                if (playToEveryone) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.playSound(onlinePlayer.getLocation(), sound, SoundCategory.MASTER, 1.0F, 1.0F);
                    }
                } else {
                    player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1.0F, 1.0F);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
