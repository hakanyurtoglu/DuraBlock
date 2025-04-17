package me.raisy.durablock.listener;

import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.database.entity.CustomBlocksEntity;
import me.raisy.durablock.model.BlockType;
import me.raisy.durablock.model.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

                player.sendMessage(formatted);
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
                List<String> lines = blockType.getDisabledhologramLines();
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
                    plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(broadcastMessage));
                }

                // Play sound
                boolean isPlaySoundEnabled = plugin.getConfig().getBoolean("blocks." + blockType.getName() + ".on-break.sound.enabled");
                String soundType = plugin.getConfig().getString("blocks." + blockType.getName() + ".on-break.sound.sound-type");
                boolean playType = plugin.getConfig().getBoolean("blocks." + blockType.getName() + ".on-break.sound.play-to-everyone");
                Sound sound = Sound.valueOf(soundType);

                if (isPlaySoundEnabled) {
                    if (playType) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            onlinePlayer.playSound(onlinePlayer.getLocation(), sound, 1f, 1f);
                        }
                    } else {
                        player.playSound(block.getLocation(), sound, 1f, 1f);
                    }
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
