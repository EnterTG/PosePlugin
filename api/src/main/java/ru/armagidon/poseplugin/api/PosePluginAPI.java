package ru.armagidon.poseplugin.api;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.armagidon.poseplugin.api.personalListener.PersonalEventDispatcher;
import ru.armagidon.poseplugin.api.player.P3Map;
import ru.armagidon.poseplugin.api.ticking.TickModuleManager;
import ru.armagidon.poseplugin.api.utils.misc.PluginLogger;
import ru.armagidon.poseplugin.api.utils.misc.event.EventListener;
import ru.armagidon.poseplugin.api.utils.nms.NMSFactory;
import ru.armagidon.poseplugin.api.utils.nms.PlayerHider;
import ru.armagidon.poseplugin.api.utils.packetManagement.PacketReaderManager;
import ru.armagidon.poseplugin.api.utils.packetManagement.readers.SwingPacketReader;
import ru.armagidon.poseplugin.api.utils.scoreboard.NameTagHider;

import java.lang.reflect.Method;

public class PosePluginAPI
{
    private static final PosePluginAPI api = new PosePluginAPI();

    private @Getter final PacketReaderManager packetReaderManager;
    private NMSFactory nmsFactory;
    private @Getter PlayerHider playerHider;
    private @Getter final P3Map playerMap;
    private @Getter final TickModuleManager tickManager;
    private @Getter final NameTagHider nameTagHider;

    private PluginLogger logger;

    private PosePluginAPI() {
        this.packetReaderManager = new PacketReaderManager();
        this.playerMap = new P3Map();
        this.tickManager = new TickModuleManager();
        this.nameTagHider = new NameTagHider();
    }

    /**PoopCode starts*/
    private Plugin plugin;

    public void init(Plugin plugin){
        this.plugin = plugin;
        /*PoopCode ends(i hope)*/
        //Init logger
        this.logger = new PluginLogger(plugin);
        //Init nms-factory and player-hider
        try {
            this.nmsFactory = new NMSFactory();
            this.playerHider = nmsFactory.createPlayerHider();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            setEnabled(false);
            plugin.getLogger().severe("Failed to enabled plugin! This version is not supported!");
            e.printStackTrace();
        }
        //Foreach online players

        registerPacketListeners();

        Bukkit.getOnlinePlayers().forEach(player->{
            packetReaderManager.inject(player);
            playerMap.addPlayer(player);
        });
        //Register main events
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(),plugin);
        //Register PersonalEventDispatcher
        Bukkit.getServer().getPluginManager().registerEvents(new PersonalEventDispatcher(),plugin);
    }

    public void shutdown(){
        getPlayerMap().forEach(p -> p.getPose().stop());
        Bukkit.getOnlinePlayers().forEach(packetReaderManager::eject);
    }

    public static PosePluginAPI getAPI() {
        return api;
    }

    public NMSFactory getNMSFactory() {
        return nmsFactory;
    }

    public PluginLogger getLogger(){
        return logger;
    }

    public Plugin getPlugin(){
        return plugin;
    }

    private void registerPacketListeners(){
        getPacketReaderManager().registerPacketReader(new SwingPacketReader());
       // getPacketReaderManager().registerPacketReader(new EqReader());
    }

    @SneakyThrows
    public static void setEnabled(boolean enabled){
        Method m = JavaPlugin.class.getDeclaredMethod("setEnabled", boolean.class);
        m.setAccessible(true);
        m.invoke(getAPI().getPlugin(), enabled);
    }

    public void registerListener(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }
}
