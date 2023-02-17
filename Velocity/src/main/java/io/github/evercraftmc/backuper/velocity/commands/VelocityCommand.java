package io.github.evercraftmc.backuper.velocity.commands;

import java.util.List;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import io.github.evercraftmc.backuper.shared.PluginCommand;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;
import io.github.evercraftmc.backuper.velocity.VelocityMain;
import io.github.evercraftmc.backuper.velocity.util.formatting.ComponentFormatter;

public abstract class VelocityCommand implements PluginCommand, SimpleCommand {
    private String name;
    private String description;
    private List<String> aliases;
    private String permission;

    protected VelocityCommand(String name, String description, List<String> aliases, String permission) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return hasPermission(invocation.source());
    }

    public boolean hasPermission(CommandSource sender) {
        return this.getPermission() == null || sender.hasPermission(this.getPermission());
    }

    @Override
    public void execute(final Invocation invocation) {
        if (this.hasPermission(invocation)) {
            run(invocation.source(), invocation.arguments());
        } else {
            invocation.source().sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(VelocityMain.getInstance().getPluginMessages().get().error.noPerms.replace("{permission}", this.getPermission()))));
        }
    }

    public abstract void run(CommandSource sender, String[] args);

    @Override
    public List<String> suggest(final Invocation invocation) {
        return tabComplete(invocation.source(), invocation.arguments());
    }

    public abstract List<String> tabComplete(CommandSource sender, String[] args);

    @Override
    public VelocityCommand register() {
        VelocityMain.getInstance().getProxy().getCommandManager().register(VelocityMain.getInstance().getProxy().getCommandManager().metaBuilder(this.getName()).aliases(this.getAliases().toArray(new String[] {})).plugin(VelocityMain.getInstance()).build(), this);

        return this;
    }

    @Override
    public void unregister() {
        VelocityMain.getInstance().getProxy().getCommandManager().unregister(VelocityMain.getInstance().getProxy().getCommandManager().metaBuilder(this.getName()).plugin(VelocityMain.getInstance()).build());
    }
}