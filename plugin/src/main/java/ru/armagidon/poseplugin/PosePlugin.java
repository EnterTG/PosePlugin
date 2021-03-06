package ru.armagidon.poseplugin;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.armagidon.poseplugin.api.PosePluginAPI;
import ru.armagidon.poseplugin.api.poses.EnumPose;
import ru.armagidon.poseplugin.plugin.UpdateChecker;
import ru.armagidon.poseplugin.plugin.command.PluginCommands;
import ru.armagidon.poseplugin.plugin.configuration.ConfigConstants;
import ru.armagidon.poseplugin.plugin.configuration.ConfigManager;
import ru.armagidon.poseplugin.plugin.configuration.messaging.Messages;
import ru.armagidon.poseplugin.plugin.listeners.MessagePrintingHandler;
import ru.armagidon.poseplugin.plugin.listeners.PluginEventListener;

import java.util.HashMap;
import java.util.Map;

public final class PosePlugin extends JavaPlugin implements Listener
{
    private @Getter static PosePlugin instance;
    private @Getter final ConfigManager configManager;

    public static Map<Player, EnumPose> PLAYERS_POSES = new HashMap<>();

    public static UpdateChecker checker;
    private Messages messages;

    private PluginCommands pcs;


    public PosePlugin() {
        instance = this;
        configManager = new ConfigManager();
        try {
            this.messages = new Messages(ConfigConstants.locale());
        } catch (IllegalArgumentException e){
            setEnabled(false);
            getLogger().severe(e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        PosePluginAPI.getAPI().init(this);
        getServer().getPluginManager().registerEvents(new PluginEventListener(),this);
        getServer().getPluginManager().registerEvents(new MessagePrintingHandler(),this);
        pcs = new PluginCommands();
        //Init commands
        pcs.initCommands();
        //Check for updates
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        PosePluginAPI.getAPI().shutdown();
        pcs.unregisterAll();
    }

    public Messages message(){
        return messages;
    }

    private void checkForUpdates(){
        if(ConfigConstants.checkForUpdates()){
            checker = new UpdateChecker();
            checker.runTaskAsynchronously(this);
        }
    }

    @Override
    public void reloadConfig() {
        configManager.reload();
    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return configManager.getConfiguration();
    }
}
