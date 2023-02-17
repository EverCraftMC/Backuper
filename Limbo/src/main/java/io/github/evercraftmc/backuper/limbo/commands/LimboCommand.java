package io.github.evercraftmc.backuper.limbo.commands;

import java.util.Arrays;
import java.util.List;
import com.loohp.limbo.commands.CommandExecutor;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.TabCompletor;
import io.github.evercraftmc.backuper.limbo.LimboMain;
import io.github.evercraftmc.backuper.limbo.util.formatting.ComponentFormatter;
import io.github.evercraftmc.backuper.shared.PluginCommand;
import io.github.evercraftmc.backuper.shared.util.formatting.TextFormatter;

public abstract class LimboCommand implements CommandExecutor, TabCompletor, PluginCommand {
    private String name;
    private String description;
    private List<String> aliases;
    private String permission;

    protected LimboCommand(String name, String description, List<String> aliases, String permission) {
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
    public void execute(CommandSender sender, String[] args) {
        Boolean isCommand = args[0].equalsIgnoreCase(this.getName());

        for (String alias : this.getAliases()) {
            if (args[0].equalsIgnoreCase(alias)) {
                isCommand = true;
            }
        }

        if (isCommand && this.testPermission(sender)) {
            this.run(sender, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    public boolean testPermission(CommandSender sender) {
        if (this.testPermissionSilent(sender)) {
            return true;
        } else {
            sender.sendMessage(ComponentFormatter.stringToComponent(TextFormatter.translateColors(LimboMain.getInstance().getPluginMessages().get().error.noPerms.replace("{permission}", this.getPermission()))));

            return false;
        }
    }

    public boolean testPermissionSilent(CommandSender sender) {
        return this.getPermission() == null || sender.hasPermission(this.getPermission());
    }

    public abstract void run(CommandSender sender, String[] args);

    @Override
    public LimboCommand register() {
        LimboMain.getInstance().getServer().getPluginManager().registerCommands(LimboMain.getInstance(), this);

        return this;
    }

    @Override
    public void unregister() {}
}