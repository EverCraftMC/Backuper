package io.github.evercraftmc.backuper.velocity.commands.backup;

import java.util.Arrays;
import java.util.List;
import com.velocitypowered.api.command.CommandSource;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import io.github.evercraftmc.backuper.velocity.VelocityMain;
import io.github.evercraftmc.backuper.velocity.commands.VelocityCommand;
import io.github.evercraftmc.backuper.velocity.util.formatting.ComponentFormatter;

public class ReloadCommand extends VelocityCommand {
    public ReloadCommand(String name, String description, List<String> aliases, String permission) {
        super(name, description, aliases, permission);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().reload.reloading)));

        VelocityMain.getInstance().reload();

        sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().reload.reloaded)));
    }

    @Override
    public List<String> tabComplete(CommandSource sender, String[] args) {
        return Arrays.asList();
    }
}