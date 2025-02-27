package com.wyvencraft;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LaunchTask extends BukkitRunnable {
    private static final double LAUNCH_SPEED = 5.0;
    private static final double MAX_HEIGHT = 6.0;
    private static final double ARRIVAL_DISTANCE = 1.0;
    private static final double SPEED_MULTIPLIER = 0.84;
    private final JavaPlugin plugin;
    private final ArmorStand stand;
    private final Player player;
    private final Location target;
    private int ticks = 0;

    public LaunchTask(JavaPlugin plugin, ArmorStand stand, Player player, Launchpad launchpad) {
        this.plugin = plugin;
        this.stand = stand;
        this.player = player;
        this.target = launchpad.getTargetLocation();
    }

    @Override
    public void run() {
        if (!player.isOnline() || stand.isDead() || ticks >= 200) {
            cancel();
            return;
        }

        Location current = stand.getLocation();
        double distance = current.distance(target);

        if (distance < ARRIVAL_DISTANCE) {
            player.teleport(target);
            cancel();
            return;
        }

        // Keep player safe from fall damage
        player.setNoDamageTicks(100);

        double dx = target.getX() - current.getX();
        double dy = target.getY() - current.getY();
        double dz = target.getZ() - current.getZ();

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        // Calculate parabolic arc
        double progress = Math.min(ticks / 40.0, 1.0);
        double verticalMotion = Math.sin(progress * Math.PI) * MAX_HEIGHT + 1.0;

        // Apply velocity directly without adding to current velocity
        Vector velocity = new Vector(
                dx / horizontalDistance * LAUNCH_SPEED * SPEED_MULTIPLIER,
                verticalMotion,
                dz / horizontalDistance * LAUNCH_SPEED * SPEED_MULTIPLIER
        );

        stand.setVelocity(velocity);
        ticks++;
    }

    @Override
    public void cancel() {
        player.setFallDistance(0);
        stand.eject();
        stand.remove();
        super.cancel();
    }
}