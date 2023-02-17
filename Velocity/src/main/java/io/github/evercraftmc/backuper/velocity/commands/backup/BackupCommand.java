package io.github.evercraftmc.backuper.velocity.commands.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.velocitypowered.api.command.CommandSource;
import io.github.evercraftmc.backuper.shared.util.StringUtils;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import io.github.evercraftmc.backuper.velocity.VelocityMain;
import io.github.evercraftmc.backuper.velocity.commands.VelocityCommand;
import io.github.evercraftmc.backuper.velocity.util.formatting.ComponentFormatter;

public class BackupCommand extends VelocityCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (VelocityMain.getInstance().getBackuper().getCurrentRun() == null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.starting)));

                    VelocityMain.getInstance().getProxy().getScheduler().buildTask(VelocityMain.getInstance(), new Runnable() {
                        public void run() {
                            VelocityMain.getInstance().getBackuper().startBackup();

                            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.finished)));
                        }
                    }).schedule();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.alreadyRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (VelocityMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.stopping)));

                    VelocityMain.getInstance().getBackuper().stopBackup();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else if (args[0].equalsIgnoreCase("status")) {
                if (VelocityMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.status.replace("{files}", VelocityMain.getInstance().getBackuper().getCurrentRun().getFinished() + "").replace("{totalFiles}", VelocityMain.getInstance().getBackuper().getCurrentRun().getTotal() + "").replace("{bytes}", ((VelocityMain.getInstance().getBackuper().getCurrentRun().getFinishedBytes() / 1024) / 1024) + "").replace("{totalBytes}", ((VelocityMain.getInstance().getBackuper().getCurrentRun().getTotalBytes() / 1024) / 1024) + ""))));
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().backup.notRunning)));
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().error.invalidArgs)));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().error.invalidArgs)));
        }
    }

    @Override
    public List<String> tabComplete(CommandSource sender, String[] args) {
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