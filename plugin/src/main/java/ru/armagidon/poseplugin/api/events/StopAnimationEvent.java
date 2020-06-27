package ru.armagidon.poseplugin.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.armagidon.poseplugin.PosePluginPlayer;
import ru.armagidon.poseplugin.api.poses.EnumPose;

public class StopAnimationEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final EnumPose pose;
    private final PosePluginPlayer player;
    private boolean log;
    private boolean cancelled;
    private final StopCause cause;

    public StopAnimationEvent(EnumPose pose, PosePluginPlayer player, boolean log, StopCause cause) {
        this.pose = pose;
        this.player = player;
        this.log = log;
        this.cause = cause;
    }

    public EnumPose getPose() {
        return pose;
    }

    public PosePluginPlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public StopCause getCause() {
        return cause;
    }

    public enum StopCause{
        STOPPED, /**Called when animation has been stopped by user*/

        TELEPORT, /**Called when animation has been stopped because of teleport*/

        BLOCK_UPDATE, /**Called when animation has been stopped because of breaking block under player*/

        GAMEMODE_CHANGE, /**Called when animation has been stopped because of changing player's gamemode to spectator*/

        DAMAGE, /**Called when animation has been stopped because of damage*/

        DEAHTH /**Called when animation has been stopped because of player's death*/
    }
}