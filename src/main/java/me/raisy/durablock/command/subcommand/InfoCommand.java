package me.raisy.durablock.command.subcommand;

import me.raisy.durablock.DuraBlockPlugin;
import me.raisy.durablock.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class InfoCommand implements SubCommand {
    private final DuraBlockPlugin plugin;

    public InfoCommand(DuraBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays information about the plugin.";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Component component = Component.text("This server is running ", NamedTextColor.YELLOW)
                .append(Component.text("DuraBlock ", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("v" + plugin.getDescription().getVersion(), NamedTextColor.LIGHT_PURPLE))
                .append(Component.text(" by ", NamedTextColor.YELLOW))
                .append(Component.text("Raisy.", NamedTextColor.LIGHT_PURPLE))
                .appendNewline()
                .append(Component.text("https://modrinth.com/plugin/durablock", NamedTextColor.GRAY).clickEvent(ClickEvent.openUrl("https://modrinth.com/plugin/durablock")));

        sender.sendMessage(component);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
