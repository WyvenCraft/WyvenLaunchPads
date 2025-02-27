package com.wyvencraft;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LaunchpadLandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final LaunchpadHandler launchpad;
    private boolean cancelled = false;

    public LaunchpadLandEvent(Player player, LaunchpadHandler launchpad) {
        this.player = player;
        this.launchpad = launchpad;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public LaunchpadHandler getLaunchpad() {
        return launchpad;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}