package io.github.evercraftmc.backuper.shared;

import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.evercraftmc.backuper.shared.config.FileConfig;

public interface Plugin {
    public void onEnable();

    public void onDisable();

    public default void reload() {
        this.onDisable();

        this.onEnable();
    }

    public FileConfig<BackuperConfig> getPluginConfig();

    public FileConfig<BackuperMessages> getPluginMessages();
}