package io.github.evercraftmc.backuper.bungee.commands.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.github.evercraftmc.backuper.bungee.BungeeMain;
import io.github.evercraftmc.backuper.bungee.commands.BungeeCommand;
import io.github.evercraftmc.backuper.bungee.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.util.StringUtils;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import net.md_5.bungee.api.CommandSender;

public class BackupCommand extends BungeeCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (BungeeMain.getInstance().getBackuper().getCurrentRun() == null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.starting)));

                    BungeeMain.getInstance().getProxy().getScheduler().runAsync(BungeeMain.getInstance(), new Runnable() {
                        public void run() {
                            BungeeMain.getInstance().getBackuper().startBackup();

                            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.finished)));
                        }
                    });
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.alreadyRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (BungeeMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.stopping)));

                    BungeeMain.getInstance().getBackuper().stopBackup();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else if (args[0].equalsIgnoreCase("status")) {
                if (BungeeMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.status.replace("{files}", BungeeMain.getInstance().getBackuper().getCurrentRun().getFinished() + "").replace("{totalFiles}", BungeeMain.getInstance().getBackuper().getCurrentRun().getTotal() + "").replace("{bytes}", ((BungeeMain.getInstance().getBackuper().getCurrentRun().getFinishedBytes() / 1024) / 1024) + "").replace("{totalBytes}", ((BungeeMain.getInstance().getBackuper().getCurrentRun().getTotalBytes() / 1024) / 1024) + ""))));
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().error.invalidArgs)));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().error.invalidArgs)));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<String>();

        if (args.length == 1) {
            list.add("start");
            list.add("stop");
            list.add("status");
        } else {
            return Arrays.asList();
        }

        if (args.length > 0) {
            return StringUtils.matchPartial(args[args.length - 1], list);
        } else {
            return list;
        }
    }
}