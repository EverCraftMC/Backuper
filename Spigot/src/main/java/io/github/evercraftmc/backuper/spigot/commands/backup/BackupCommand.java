package io.github.evercraftmc.backuper.spigot.commands.backup;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import io.github.evercraftmc.backuper.spigot.SpigotMain;
import io.github.evercraftmc.backuper.spigot.commands.SpigotCommand;
import io.github.evercraftmc.backuper.spigot.util.formatting.ComponentFormatter;

public class BackupCommand extends SpigotCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.backingUp)));

        SpigotMain.getInstance().getServer().getScheduler().runTaskAsynchronously(SpigotMain.getInstance(), new Runnable() {
            public void run() {
                SpigotMain.getInstance().getBackuper().backup();

                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.backedUp)));
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}