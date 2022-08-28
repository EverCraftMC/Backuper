package io.github.evercraftmc.backuper.limbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.loohp.limbo.plugins.LimboPlugin;
import io.github.evercraftmc.backuper.limbo.commands.LimboCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.BackupCommand;
import io.github.evercraftmc.backuper.limbo.commands.backup.ReloadCommand;
import io.github.evercraftmc.backuper.shared.Plugin;
import io.github.evercraftmc.backuper.shared.backuper.Backuper;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.shared.config.FileConfig;

public class LimboMain extends LimboPlugin implements Plugin {
    private static LimboMain Instance;

    private FileConfig<BackuperConfig> config;
    private FileConfig<BackuperMessages> messages;

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

        this.config = new FileConfig<BackuperConfig>(BackuperConfig.class, this.getDataFolder().getAbsolutePath() + File.separator + "config.json");
        this.config.reload();

        System.out.println("Finished loading config");

        System.out.println("Loading messages..");

        this.messages = new FileConfig<BackuperMessages>(BackuperMessages.class, this.getDataFolder().getAbsolutePath() + File.separator + "messages.json");
        this.messages.reload();

        System.out.println("Finished loading messages");

        System.out.println("Loading backuper..");

        this.backuper = new Backuper(this.config);

        System.out.println("Finished loading backuper");

        System.out.println("Loading commands..");

        this.commands = new ArrayList<LimboCommand>();

        this.commands.add(new BackupCommand("backuper", "Backup the server", Arrays.asList(), "backuper.commands.backup").register());
        this.commands.add(new ReloadCommand("backuperreload", "Reload the plugin", Arrays.asList(), "backuper.commands.reload").register());

        System.out.println("Finished loading commands");

        System.out.println("Finished loading plugin");
    }

    @Override
    public void onDisable() {
        System.out.println("Disabling plugin..");

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