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
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.starting)));

                    SpigotMain.getInstance().getServer().getScheduler().runTaskAsynchronously(SpigotMain.getInstance(), new Runnable() {
                        public void run() {
                            SpigotMain.getInstance().getBackuper().startBackup();

                            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.finished)));
                        }
                    });
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.alreadyRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (SpigotMain.getInstance().getBackuper().getCurrentRun() == null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.stopping)));

                    SpigotMain.getInstance().getBackuper().stopBackup();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().backup.notRunning)));
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().error.invalidArgs)));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(SpigotMain.getInstance().getPluginMessages().getParsed().error.invalidArgs)));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<String>();

        if (args.length == 1) {
            list.add("start");
            list.add("stop");
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