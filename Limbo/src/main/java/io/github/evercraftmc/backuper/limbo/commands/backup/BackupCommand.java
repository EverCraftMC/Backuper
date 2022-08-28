package io.github.evercraftmc.backuper.limbo.commands.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.scheduler.LimboTask;
import io.github.evercraftmc.backuper.limbo.LimboMain;
import io.github.evercraftmc.backuper.limbo.commands.LimboCommand;
import io.github.evercraftmc.backuper.limbo.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.util.StringUtils;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;

public class BackupCommand extends LimboCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                if (LimboMain.getInstance().getBackuper().getCurrentRun() == null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.starting)));

                    LimboMain.getInstance().getServer().getScheduler().runTaskAsync(LimboMain.getInstance(), new LimboTask() {
                        public void run() {
                            LimboMain.getInstance().getBackuper().startBackup();

                            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.finished)));
                        }
                    });
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.alreadyRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (LimboMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.stopping)));

                    LimboMain.getInstance().getBackuper().stopBackup();
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.notRunning)));
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (LimboMain.getInstance().getBackuper().getCurrentRun() != null) {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.status.replace("{files}", LimboMain.getInstance().getBackuper().getCurrentRun().getFinished() + "").replace("{totalFiles}", LimboMain.getInstance().getBackuper().getCurrentRun().getTotal() + "").replace("{bytes}", ((LimboMain.getInstance().getBackuper().getCurrentRun().getFinishedBytes() / 1024) / 1024) + "").replace("{totalBytes}", ((LimboMain.getInstance().getBackuper().getCurrentRun().getTotalBytes() / 1024) / 1024) + ""))));
                } else {
                    sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().backup.notRunning)));
                }
            } else {
                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().error.invalidArgs)));
            }
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getParsed().error.invalidArgs)));
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