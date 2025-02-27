package com.wyvencraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LaunchpadListener implements Listener {
    private final WyvenLaunchPads plugin;

    public LaunchpadListener(WyvenLaunchPads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Player player = e.getPlayer();

        Block downBlock = to.getBlock().getRelative(BlockFace.DOWN);
        if (downBlock.getType() != Material.SLIME_BLOCK) return;

        if (plugin.isJumping(player)) return;

        Launchpad launchpad = plugin.getLaunchpads().values().stream()
                .filter(lp -> lp.getLinkedBlocks().contains(downBlock.getLocation()))
                .findFirst().orElse(null);

        if (launchpad == null) return;

        plugin.launch(player, launchpad);
    }

    @EventHandler
    public void onDismount(final EntityDismountEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!plugin.isJumping(player)) return;

        new BukkitRunnable() {
            public void run() {
                player.eject();
                e.getDismounted().addPassenger(player);
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (!plugin.isJumping(e.getPlayer())) return;
        Location from = e.getFrom();
        Location to = e.getTo();
        if (from.getWorld() == null || to.getWorld() == null) return;
        if (!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName()) || from.distance(to) > 10.0D) {
            plugin.cancelFlight(e.getPlayer());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!plugin.isJumping(player)) return;
        e.setCancelled(true);
    }
}
