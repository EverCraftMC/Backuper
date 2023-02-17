package io.github.evercraftmc.backuper.shared;

import java.util.logging.Logger;
import io.github.evercraftmc.backuper.shared.backuper.BackuperConfig;
import io.github.evercraftmc.backuper.shared.backuper.BackuperMessages;
import io.github.kale_ko.ejcl.file.JsonConfig;

public interface Plugin {
    public void onEnable();

    public void onDisable();

    public default void reload() {
        this.onDisable();

        this.onEnable();
    }

    public JsonConfig<BackuperConfig> getPluginConfig();

    public JsonConfig<BackuperMessages> getPluginMessages();
}