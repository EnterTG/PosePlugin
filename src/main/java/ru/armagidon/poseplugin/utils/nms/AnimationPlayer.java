package ru.armagidon.poseplugin.utils.nms;

import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.lang.reflect.InvocationTargetException;

import static ru.armagidon.poseplugin.utils.nms.NMSUtils.getNmsClass;
import static ru.armagidon.poseplugin.utils.nms.NMSUtils.sendPacket;

public interface AnimationPlayer
{
    static void play(Player target, Player receiver, Pose pose) {
        try{
            Object vanilla = NMSUtils.asNMSCopy(target);
            int id = NMSUtils.getPlayerID(target);

            Object watcher = NMSUtils.getNmsClass("Entity").getDeclaredMethod("getDataWatcher").invoke(vanilla);

            NMSUtils.setPlayerPose(target, pose);

            Object metadata = NMSUtils.createPacketInstance("PacketPlayOutEntityMetadata",new Class[]{int.class, getNmsClass("DataWatcher"), boolean.class}, id,watcher, false);

            sendPacket(receiver, metadata);
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
