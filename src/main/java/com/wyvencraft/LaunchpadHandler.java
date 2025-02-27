package com.wyvencraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LaunchpadHandler {
    final double SPEED = 1;
    private final Plugin plugin;
    private final Launchpad launchpad;
    private final Location flyLocation;
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private final Map<UUID, ArmorStand> isJumping = new HashMap<>();

    public LaunchpadHandler(Plugin plugin, Launchpad launchpad) {
        this.plugin = plugin;
        this.launchpad = launchpad;
        this.flyLocation = launchpad.getTargetLocation();
    }

    public void launch(final Player player) {
        if (launchpad == null || !flyLocation.getWorld().equals(player.getWorld())) {
            player.sendMessage("Fly location is not set correctly.");
            return;
        }

        // Fire custom event
        LaunchpadLaunchEvent event = new LaunchpadLaunchEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        // Create an invisible ArmorStand to control movement
        final ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.addPassenger(player);
        isJumping.put(player.getUniqueId(), armorStand);

        // Play launch sound and particles
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        player.spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.3, 0.3, 0.3, 0.1);

        BukkitTask task = new BukkitRunnable() {
            final double x3 = flyLocation.distance(player.getLocation());
            final double x2 = this.x3 / 3.0D;
            final double A3 = -((-this.x2 + this.x3) / (-0.0D + this.x2)) * (-0.0D + this.x2 * this.x2) - this.x2 * this.x2 + this.x3 * this.x3;
            final double y3 = Math.abs(flyLocation.getY() - player.getLocation().getY()) % 10.0D;
            final double D3 = -((-this.x2 + this.x3) / (-0.0D + this.x2)) * (-0.0D + this.x2) - this.x2 + this.y3;
            final double a = this.D3 / this.A3;
            final double b = (-0.0D + this.x2 - (-0.0D + this.x2 * this.x2) * this.a) / (-0.0D + this.x2);
            final double c = 0.0D - this.a * 0.0D * 0.0D - this.b * 0.0D;
            final double progress = 0.0;
            double xC = 0.0D;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || flyLocation.distance(armorStand.getLocation()) <= 5.0) {
                    endFlight(player, armorStand);
                    cancel();
                    return;
                }

                // Move player towards target
//                moveToward(armorStand, progress);
                moveToward(armorStand, yCalculate(this.a, this.b, this.c, this.xC));
                xC += 0.84D * SPEED;

                // Play flying sound and particle effect
                player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.5f, 1.5f);
                player.spawnParticle(Particle.END_ROD, player.getLocation(), 5, 0.2, 0.2, 0.2, 0.05);
            }

            private double yCalculate(double a, double b, double c, double xC) {
                return a * xC * xC + xC * b + c;
            }
        }.runTaskTimer(plugin, 1L, 1L);

        tasks.put(player.getUniqueId(), task);
    }

    public boolean isJumping(Player player) {
        return isJumping.containsKey(player.getUniqueId());
    }

    public void cancelFlight(Player player) {
        if (!isJumping.containsKey(player.getUniqueId())) return;
        ArmorStand armorStand = isJumping.get(player.getUniqueId());
        armorStand.remove();
        isJumping.remove(player.getUniqueId());
        tasks.remove(player.getUniqueId());
        player.setNoDamageTicks(100);
    }

    private void moveToward(Entity player, double yC) {
        Location loc = player.getLocation();
        double x = loc.getX() - this.flyLocation.getX();
        double y = loc.getY() - this.flyLocation.getY() - Math.max(yC, 0.0D);
        double z = loc.getZ() - this.flyLocation.getZ();
        Vector velocity = (new Vector(x, y, z)).normalize().multiply(-0.8D * SPEED);
        player.setVelocity(velocity);
    }

    private void endFlight(Player player, ArmorStand armorStand) {
        armorStand.remove();
        isJumping.remove(player.getUniqueId());
        tasks.remove(player.getUniqueId());
        player.teleport(flyLocation);
        player.setNoDamageTicks(100);

        // Fire landing event
        LaunchpadLandEvent event = new LaunchpadLandEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);

        // Play landing sound and particles
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 10, 0.3, 0.3, 0.3, 0.1);
    }

    public Launchpad getLaunchpad() {
        return launchpad;
    }
}