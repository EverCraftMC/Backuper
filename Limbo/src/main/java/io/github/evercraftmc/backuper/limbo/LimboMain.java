package io.github.evercraftmc.backuper.limbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.loohp.limbo.plugins.LimboPlugin;
import io.github.evercraftmc.backuper.shared.Plugin;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.config.FileConfig;
import io.github.evercraftmc.backuper.limbo.commands.LimboCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.ReloadCommand;

public class LimboMain extends LimboPlugin implements Plugin {
    private static LimboMain Instance;

    private FileConfig config;
    private FileConfig messages;

    private Backuper backuper;

    private List<LimboCommand> commands;

    @Override
    public void onLoad() {
        LimboMain.Instance = this;
    }

    @Override
    public void onEnable() {
        System.out.println("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        System.out.println("Loading config..");

        this.config = new FileConfig(this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
        this.config.reload();

        this.config.addDefault("filter", Arrays.asList());

        this.config.copyDefaults();

        System.out.println("Finished loading config");

        System.out.println("Loading messages..");

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

        System.out.println("Finished loading messages");

        System.out.println("Loading backuper..");

        this.backuper = new Backuper(this.getServer().getPluginFolder().getAbsoluteFile().getParentFile().getAbsolutePath(), this.getServer().getPluginFolder().getAbsoluteFile().getParentFile().getAbsolutePath() + File.separator + "backups");

        System.out.println("Finished loading backuper");

        System.out.println("Loading commands..");

        this.commands = new ArrayList<LimboCommand>();

        this.commands.add(new BackupCommand("backup", "Backup the server", Arrays.asList(), "backup.commands.backup").register());
        this.commands.add(new ReloadCommand("backupreload", "Reload the plugin", Arrays.asList("breload"), "backup.commands.reload").register());

        System.out.println("Finished loading commands");

        System.out.println("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        System.out.println("Disabling plugin..");

        System.out.println("Closing config..");

        config.close();

        System.out.println("Finished closing config..");

        System.out.println("Closing messages..");

        messages.close();

        System.out.println("Finished closing messages..");

        System.out.println("Unregistering commands..");

        for (LimboCommand command : this.commands) {
            command.unregister();
        }

        System.out.println("Finished unregistering commands..");

        System.out.println("Finished disabling plugin");
    }

    @Override
    public void reload() {
        System.out.println("Reloading plugin..");

        this.onDisable();

        this.onEnable();

        System.out.println("Finished reloading plugin");
    }

    public static LimboMain getInstance() {
        return LimboMain.Instance;
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