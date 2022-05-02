package io.github.evercraftmc.backuper.limbo.commands.backup;

import java.util.Arrays;
import java.util.List;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.scheduler.LimboTask;
import io.github.evercraftmc.backuper.limbo.LimboMain;
import io.github.evercraftmc.backuper.limbo.commands.LimboCommand;
import io.github.evercraftmc.backuper.limbo.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;

public class BackupCommand extends LimboCommand {
    public BackupCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getString("backup.backingUp"))));

        LimboMain.getInstance().getServer().getScheduler().runTaskAsync(LimboMain.getInstance(), new LimboTask() {
            public void run() {
                LimboMain.getInstance().getBackuper().backup(LimboMain.getInstance().getPluginConfig().getStringList("filter"));

                sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().getString("backup.backedUp"))));
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}