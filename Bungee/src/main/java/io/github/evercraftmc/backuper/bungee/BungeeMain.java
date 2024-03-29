package io.github.evercraftmc.backuper.bungee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.github.evercraftmc.backuper.bungee.commands.BungeeCommand;
import io.github.evercraftmc.backuper.bungee.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.bungee.commands.backup.ReloadCommand;
import io.github.evercraftmc.backuper.shared.PluginManager;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.kale_ko.ejcl.file.bjsl.JsonFileConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin implements io.github.evercraftmc.backuper.shared.Plugin {
    private static BungeeMain Instance;

    private JsonFileConfig<BackuperConfig> config;
    private JsonFileConfig<BackuperMessages> messages;

    private Backuper backuper;

    private List<BungeeCommand> commands;

    @Override
    public void onLoad() {
        BungeeMain.Instance = this;

        PluginManager.register(this);
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.getLogger().info("Loading config..");

        this.config = new JsonFileConfig<BackuperConfig>(BackuperConfig.class, this.getDataFolder().toPath().resolve("config.json").toFile());
        try {
            this.config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getLogger().info("Finished loading config");

        this.getLogger().info("Loading messages..");

        this.messages = new JsonFileConfig<BackuperMessages>(BackuperMessages.class, this.getDataFolder().toPath().resolve("messages.json").toFile());
        try {
            this.messages.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getLogger().info("Finished loading messages");

        this.getLogger().info("Loading backuper..");

        this.backuper = new Backuper(config);

        this.getLogger().info("Finished loading backuper");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<BungeeCommand>();

        this.commands.add(new BackupCommand("bungeebackuper", "Backup the server", Arrays.asList("bbackuper"), "backup.commands.backup").register());
        this.commands.add(new ReloadCommand("bungeebackuperreload", "Reload the plugin", Arrays.asList("bbackuperreload"), "backup.commands.reload").register());

        this.getLogger().info("Finished loading commands");

        this.getLogger().info("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling plugin..");

        if (this.backuper.getCurrentRun() != null) {
            this.getLogger().info("Stopping backup..");

            this.backuper.stopBackup();

            this.getLogger().info("Finished stopping backup..");
        }

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

    public JsonFileConfig<BackuperConfig> getPluginConfig() {
        return this.config;
    }

    public JsonFileConfig<BackuperMessages> getPluginMessages() {
        return this.messages;
    }

    public Backuper getBackuper() {
        return this.backuper;
    }
}