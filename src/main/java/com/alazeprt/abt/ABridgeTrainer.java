package com.alazeprt.abt;

import com.alazeprt.abt.commands.BasicCommandHandler;
import com.alazeprt.abt.commands.tab.BasicTabCompleter;
import com.alazeprt.abt.events.BasicEventHandler;
import com.alazeprt.abt.utils.Common;
import com.alazeprt.abt.utils.ConfigurationHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ABridgeTrainer extends JavaPlugin implements CommandExecutor, Listener {
    @Override
    public void onEnable() {
        ConfigurationHandler.load(getDataFolder());
        BasicCommandHandler.register();
        BasicTabCompleter.register();
        BasicEventHandler.register();
    }

    @Override
    public void onDisable() {
        ConfigurationHandler.save(getDataFolder());
        Common.resetAllSite(true);
    }
}
