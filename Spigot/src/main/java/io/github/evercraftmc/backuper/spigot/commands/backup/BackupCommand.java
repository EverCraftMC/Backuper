package io.github.evercraftmc.backuper.spigot.commands.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import io.github.evercraftmc.backuper.shared.util.StringUtils;
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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (SpigotMain.getInstance().getBackuper().getCurrentRun() == null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.starting)));

                    SpigotMain.getInstance().getServer().getScheduler().runTaskAsynchronously(SpigotMain.getInstance(), new Runnable() {
                        public void run() {
                            SpigotMain.getInstance().getBackuper().startBackup();

                            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.finished)));
                        }
                    });
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.alreadyRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (SpigotMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.stopping)));

                    SpigotMain.getInstance().getBackuper().stopBackup();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else if (args[0].equalsIgnoreCase("status")) {
                if (SpigotMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.status.replace("{files}", SpigotMain.getInstance().getBackuper().getCurrentRun().getFinished() + "").replace("{totalFiles}", SpigotMain.getInstance().getBackuper().getCurrentRun().getTotal() + "").replace("{bytes}", ((SpigotMain.getInstance().getBackuper().getCurrentRun().getFinishedBytes() / 1024) / 1024) + "").replace("{totalBytes}", ((SpigotMain.getInstance().getBackuper().getCurrentRun().getTotalBytes() / 1024) / 1024) + ""))));
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().error.invalidArgs)));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().get().error.invalidArgs)));
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