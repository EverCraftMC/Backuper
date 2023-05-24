package io.github.evercraftmc.backuper.shared;

import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.kale_ko.ejcl.StructuredConfig;

public interface Plugin {
    public void onEnable();

    public void onDisable();

    public default void reload() {
        this.onDisable();

        this.onEnable();
    }

    public StructuredConfig<BackuperConfig> getPluginConfig();

    public StructuredConfig<BackuperMessages> getPluginMessages();
}