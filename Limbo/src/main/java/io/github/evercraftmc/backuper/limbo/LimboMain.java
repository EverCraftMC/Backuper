package io.github.evercraftmc.backuper.limbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.loohp.limbo.plugins.LimboPlugin;
import io.github.evercraftmc.backuper.limbo.commands.LimboCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.ReloadCommand;
import io.github.evercraftmc.backuper.shared.Plugin;
import io.github.evercraftmc.backuper.shared.PluginManager;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.shared.config.FileConfig;

public class LimboMain extends LimboPlugin implements Plugin {
    private static LimboMain Instance;

    private Logger logger;

    private FileConfig<BackuperConfig> config;
    private FileConfig<BackuperMessages> messages;

    private Backuper backuper;

    private List<LimboCommand> commands;

    @Override
    public void onLoad() {
        LimboMain.Instance = this;

        PluginManager.register(this);

        this.logger = PluginManager.createLogger(this.getInfo().getName(), "[{timeC} {typeT}] [{name}] {message}");
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.getLogger().info("Loading config..");

        this.config = new FileConfig<BackuperConfig>(BackuperConfig.class, this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
        this.config.reload();

        this.getLogger().info("Finished loading config");

        this.getLogger().info("Loading messages..");

        this.messages = new FileConfig<BackuperMessages>(BackuperMessages.class, this.getDataFolder().getAbsolutePath() + File.separator + "messages.json");
        this.messages.reload();

        this.getLogger().info("Finished loading messages");

        this.getLogger().info("Loading backuper..");

        this.backuper = new Backuper(this.config);

        this.getLogger().info("Finished loading backuper");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<LimboCommand>();

        this.commands.add(new BackupCommand("backuper", "Backup the server", Arrays.asList(), "backuper.commands.backup").register());
        this.commands.add(new ReloadCommand("backuperreload", "Reload the plugin", Arrays.asList(), "backuper.commands.reload").register());

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

        for (LimboCommand command : this.commands) {
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

    public static LimboMain getInstance() {
        return LimboMain.Instance;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public FileConfig<BackuperConfig> getPluginConfig() {
        return this.config;
    }

    public FileConfig<BackuperMessages> getPluginMessages() {
        return this.messages;
    }

    public Backuper getBackuper() {
        return this.backuper;
    }
}