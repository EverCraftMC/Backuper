package io.github.evercraftmc.backuper.spigot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.evercraftmc.backuper.shared.Plugin;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.shared.config.FileConfig;
import io.github.evercraftmc.backuper.spigot.commands.SpigotCommand;
import io.github.evercraftmc.backuper.spigot.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.spigot.commands.backup.ReloadCommand;

public class SpigotMain extends JavaPlugin implements Plugin {
    private static SpigotMain Instance;

    private FileConfig<BackuperConfig> config;
    private FileConfig<BackuperMessages> messages;

    private Backuper backuper;

    private List<SpigotCommand> commands;

    @Override
    public void onLoad() {
        SpigotMain.Instance = this;
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

        this.commands = new ArrayList<SpigotCommand>();

        this.commands.add(new BackupCommand("backuper", "Backup the server", Arrays.asList(), "backuper.commands.backup").register());
        this.commands.add(new ReloadCommand("backuperreload", "Reload the plugin", Arrays.asList(), "backuper.commands.reload").register());

        this.getLogger().info("Finished loading commands");

        this.getLogger().info("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabling plugin..");

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