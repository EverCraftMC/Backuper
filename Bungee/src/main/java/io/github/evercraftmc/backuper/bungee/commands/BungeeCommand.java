package io.github.evercraftmc.backuper.bungee.commands;

import java.util.List;
import io.github.evercraftmc.backuper.bungee.BungeeMain;
import io.github.evercraftmc.backuper.bungee.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.PluginCommand;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public abstract class BungeeCommand extends Command implements PluginCommand, TabExecutor {
    protected BungeeCommand(String name, String description, List<String> aliases, String permission) {
        super(name, permission, aliases.toArray(new String[] {}));
        if (permission != null) {
            this.setPermissionMessage(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().error.noPerms.replace("{permission}", permission)));
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (this.hasPermission(sender)) {
            this.run(sender, args);
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(BungeeMain.getInstance().getPluginMessages().get().error.noPerms.replace("{permission}", this.getPermission()))));
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return this.getPermission() == null || sender.hasPermission(this.getPermission());
    }

    public abstract void run(CommandSender sender, String[] args);

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return tabComplete(sender, args);
    }

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public BungeeCommand register() {
        BungeeMain.getInstance().getProxy().getPluginManager().registerCommand(BungeeMain.getInstance(), this);

        return this;
    }

    public void unregister() {
        BungeeMain.getInstance().getProxy().getPluginManager().unregisterCommand(this);
    }
}