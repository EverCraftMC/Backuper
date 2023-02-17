package io.github.evercraftmc.backuper.velocity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.evercraftmc.backuper.shared.PluginManager;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.velocity.commands.VelocityCommand;
import io.github.evercraftmc.backuper.velocity.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.velocity.commands.backup.ReloadCommand;
import io.github.kale_ko.ejcl.file.JsonConfig;

@Plugin(id = "backuper", name = "Backuper", version = "${plugin_version}", url = "https://github.com/EverCraft-MC/Backuper", description = "A custom plugin to backup all server data", authors = { "Kale Ko" })
public class VelocityMain implements io.github.evercraftmc.backuper.shared.Plugin {
    private static VelocityMain Instance;

    private JsonConfig<BackuperConfig> config;
    private JsonConfig<BackuperMessages> messages;

    private Backuper backuper;

    private List<VelocityCommand> commands;

    private ProxyServer server;
    private File dataFolder;
    private Logger logger;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        VelocityMain.Instance = this;

        PluginManager.register(this);

        this.server = server;
        this.dataFolder = dataFolder.toFile();
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        onEnable();
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading plugin..");

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.getLogger().info("Loading config..");

        this.config = new JsonConfig<BackuperConfig>(BackuperConfig.class, this.getDataFolder().toPath().resolve("config.json").toFile());
        try {
            this.config.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.getLogger().info("Finished loading config");

        this.getLogger().info("Loading messages..");

        this.messages = new JsonConfig<BackuperMessages>(BackuperMessages.class, this.getDataFolder().toPath().resolve("messages.json").toFile());
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

        this.commands = new ArrayList<VelocityCommand>();

        this.commands.add(new BackupCommand("velocitybackuper", "Backup the server", Arrays.asList("vbackuper"), "backup.commands.backup").register());
        this.commands.add(new ReloadCommand("velocitybackuperreload", "Reload the plugin", Arrays.asList("vbackuperreload"), "backup.commands.reload").register());

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

        for (VelocityCommand command : this.commands) {
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

    public static VelocityMain getInstance() {
        return VelocityMain.Instance;
    }

    public JsonConfig<BackuperConfig> getPluginConfig() {
        return this.config;
    }

    public JsonConfig<BackuperMessages> getPluginMessages() {
        return this.messages;
    }

    public Backuper getBackuper() {
        return this.backuper;
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }

    public Logger getLogger() {
        return this.logger;
    }
}