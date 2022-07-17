package io.github.evercraftmc.backuper.bungee.commands.backup;

import java.util.Arrays;
import java.util.List;
import io.github.evercraftmc.backuper.bungee.BungeeMain;
import io.github.evercraftmc.backuper.bungee.commands.BungeeCommand;
import io.github.evercraftmc.backuper.bungee.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import net.md_5.bungee.api.CommandSender;

public class BackupCommand extends BungeeCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().getParsed().backup.backingUp)));

        BungeeMain.getInstance().getProxy().getScheduler().runAsync(BungeeMain.getInstance(), new Runnable() {
            public void run() {
                BungeeMain.getInstance().getBackuper().backup();

                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().getParsed().backup.backedUp)));
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}