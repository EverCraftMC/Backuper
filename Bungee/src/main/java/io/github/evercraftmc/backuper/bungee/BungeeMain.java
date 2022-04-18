package io.github.evercraftmc.backuper.bungee;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.github.evercraftmc.backuper.bungee.commands.BungeeCommand;
import io.github.evercraftmc.backuper.bungee.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.bungee.commands.backup.ReloadCommand;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.config.FileConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin implements io.github.evercraftmc.backuper.shared.Plugin {
    private static BungeeMain Instance;

    private FileConfig config;
    private FileConfig messages;

    private Backuper backuper;

    private List<BungeeCommand> commands;

    @Override
    public void onLoad() {
        BungeeMain.Instance = this;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.getLogger().info("Loading config..");

        this.config = new FileConfig(this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
        this.config.reload();

        this.config.addDefault("filter", Arrays.asList());

        this.config.copyDefaults();

        this.getLogger().info("Finished loading config");

        this.getLogger().info("Loading messages..");

        this.messages = new FileConfig(this.getDataFolder().getAbsolutePath() + File.separator + "messages.json");
        this.messages.reload();

        this.messages.addDefault("error.noPerms", "&cYou need the permission {permission} to do that");
        this.messages.addDefault("error.noConsole", "&cYou can't do that from the console");
        this.messages.addDefault("error.playerNotFound", "&cCouldn't find player {player}");
        this.messages.addDefault("error.invalidArgs", "&cInvalid arguments");
        this.messages.addDefault("reload.reloading", "&aReloading plugin..");
        this.messages.addDefault("reload.reloaded", "&aSuccessfully reloaded");
        this.messages.addDefault("backup.backingUp", "&aBacking up data..");
        this.messages.addDefault("backup.backedUp", "&aSuccessfully backed up all data");

        this.messages.copyDefaults();

        this.getLogger().info("Finished loading messages");

        this.getLogger().info("Loading backuper..");

        this.backuper = new Backuper(this.getProxy().getPluginsFolder().getAbsoluteFile().getParentFile().getAbsolutePath(), this.getProxy().getPluginsFolder().getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator + "backups");

        this.getLogger().info("Finished loading backuper");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<BungeeCommand>();

        this.commands.add(new BackupCommand("bungeebackup", "Backup the server", Arrays.asList("bbackup"), "backup.commands.backup").register());
        this.commands.add(new ReloadCommand("bungeebackupreload", "Reload the plugin", Arrays.asList("bbackupreload", "bbreload"), "backup.commands.reload").register());

        this.getLogger().info("Finished loading commands");

        this.getLogger().info("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling plugin..");

        this.getLogger().info("Closing config..");

        config.close();

        this.getLogger().info("Finished closing config..");

        this.getLogger().info("Closing messages..");

        messages.close();

        this.getLogger().info("Finished closing messages..");

        this.getLogger().info("Unregistering commands..");

        for (BungeeCommand command : this.commands) {
            command.unregister();
        }

        this.getLogger().info("Finished unregistering commands..");

        this.getLogger().info("Finished disabling plugin");
    }

    @Override
    public void reload() {
        this.getLogger().info("Reloading plugin..");

        this.onDisable();

        this.onEnable();

        this.getLogger().info("Finished reloading plugin");
    }

    public static BungeeMain getInstance() {
        return BungeeMain.Instance;
    }

    public FileConfig getPluginConfig() {
        return this.config;
    }

    public FileConfig getPluginMessages() {
        return this.messages;
    }

    public Backuper getBackuper() {
        return this.backuper;
    }
}