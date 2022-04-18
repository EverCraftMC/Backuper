package io.github.evercraftmc.backuper.spigot.commands.backup;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import io.github.evercraftmc.backuper.spigot.SpigotMain;
import io.github.evercraftmc.backuper.spigot.commands.SpigotCommand;
import io.github.evercraftmc.backuper.spigot.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;

public class BackupCommand extends SpigotCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getString("backup.backingUp"))));

        SpigotMain.getInstance().getBackuper().backup(SpigotMain.getInstance().getPluginConfig().getStringList("filter"));

        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getString("backup.backedUp"))));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}