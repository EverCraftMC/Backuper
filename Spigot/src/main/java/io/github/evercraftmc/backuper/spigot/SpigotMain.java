package io.github.evercraftmc.backuper.spigot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.evercraftmc.backuper.shared.Plugin;
import io.github.evercraftmc.backuper.shared.PluginManager;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.spigot.commands.SpigotCommand;
import io.github.evercraftmc.backuper.spigot.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.spigot.commands.backup.ReloadCommand;
import io.github.kale_ko.ejcl.file.JsonConfig;

public class SpigotMain extends JavaPlugin implements Plugin {
    private static SpigotMain Instance;

    private JsonConfig<BackuperConfig> config;
    private JsonConfig<BackuperMessages> messages;

    private Backuper backuper;

    private List<SpigotCommand> commands;

    @Override
    public void onLoad() {
        SpigotMain.Instance = this;

        PluginManager.register(this);
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

        this.backuper = new Backuper(this.config);

        this.getLogger().info("Finished loading backuper");

        this.getLogger().info("Loading commands..");

        this.commands = new ArrayList<SpigotCommand>();

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

        for (SpigotCommand command : this.commands) {
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

    public static SpigotMain getInstance() {
        return SpigotMain.Instance;
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
}