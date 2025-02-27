package com.wyvencraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WyvenLaunchPads extends JavaPlugin implements Listener {
    private static WyvenLaunchPads plugin;

    private final Map<String, Launchpad> launchpads = new HashMap<>();
    private final Map<String, String> linkingPlayers = new HashMap<>();
    private LaunchpadManager launchpadManager;

    public static WyvenLaunchPads getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new LaunchpadListener(this), this);

        // Initialize and load launchpads
        launchpadManager = new LaunchpadManager(this);
        launchpadManager.loadLaunchpads(launchpads);

        getLogger().info("WyvenLaunchPads has been enabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;

        if (command.getName().equalsIgnoreCase("launchpad")) {
            if (args.length == 0) {
                player.sendMessage("Usage: /launchpad create <name> | delete <name> | target <name> | link <name>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "create" -> {
                    if (args.length != 2) {
                        player.sendMessage("Usage: /launchpad create <name>");
                        return true;
                    }
                    if (launchpads.containsKey(args[1])) {
                        player.sendMessage("A launchpad with that name already exists!");
                        return true;
                    }
                    Location sourceLoc = player.getLocation();
                    launchpads.put(args[1], new Launchpad(sourceLoc));
                    launchpadManager.saveLaunchpads(launchpads);
                    player.sendMessage("Launchpad created! Use /launchpad target <name> to set target location.");
                }
                case "target" -> {
                    if (args.length != 2) {
                        player.sendMessage("Usage: /launchpad target <name>");
                        return true;
                    }
                    Launchpad pad = launchpads.get(args[1]);
                    if (pad == null) {
                        player.sendMessage("Launchpad not found!");
                        return true;
                    }
                    pad.setTarget(player.getLocation());
                    launchpadManager.saveLaunchpads(launchpads); // Save after setting target
                    player.sendMessage("Target location set! You can now link slime blocks.");
                }
                case "delete" -> {
                    if (args.length != 2) {
                        player.sendMessage("Usage: /launchpad delete <name>");
                        return true;
                    }
                    if (!launchpads.containsKey(args[1])) {
                        player.sendMessage("Launchpad not found!");
                        return true;
                    }

                    // Remove any linking players for this launchpad
                    linkingPlayers.values().removeIf(name -> name.equals(args[1]));

                    launchpads.remove(args[1]);
                    launchpadManager.saveLaunchpads(launchpads);
                    player.sendMessage("Launchpad deleted!");
                }
                case "link" -> {
                    if (args.length != 2) {
                        player.sendMessage("Usage: /launchpad link <name>");
                        return true;
                    }

                    // If player is already linking, disable it
                    if (linkingPlayers.containsKey(player.getName())) {
                        disableLinking(player);
                        return true;
                    }

                    Launchpad pad = launchpads.get(args[1]);
                    if (pad == null) {
                        player.sendMessage("Launchpad not found!");
                        return true;
                    }
                    if (!pad.hasTarget()) {
                        player.sendMessage("Set a target first using /launchpad target <name>");
                        return true;
                    }
                    linkingPlayers.put(player.getName(), args[1]);
                    player.sendMessage("Click slime blocks to link them to the launchpad! Use /launchpad link " + args[1] + " again to stop linking.");
                }
            }
            return true;
        }
        return false;
    }

    public boolean isJumping(Player player) {
        return launchpadManager.getPadHandlers().stream().anyMatch(handler -> handler.isJumping(player));
    }

    public void cancelFlight(Player player) {
        launchpadManager.getPadHandlers().forEach(handler -> handler.cancelFlight(player));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only handle right clicks
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        String playerName = player.getName();

        if (linkingPlayers.containsKey(playerName)) {
            // Disable linking if clicking with empty hand
//            if (event.getItem() == null) {
//                disableLinking(player);
//                event.setCancelled(true);
//                return;
//            }

            String launchpadName = linkingPlayers.get(playerName);
            Launchpad pad = launchpads.get(launchpadName);

            if (pad != null && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SLIME_BLOCK) {
                pad.addLinkedBlock(event.getClickedBlock().getLocation());
                launchpadManager.saveLaunchpads(launchpads);
                player.sendMessage("Slime block linked to launchpad " + launchpadName + "!");
                event.setCancelled(true);
            }
        }
    }

    private void disableLinking(Player player) {
        linkingPlayers.remove(player.getName());
        player.sendMessage("Linking mode disabled!");
    }

    public void launch(Player player, Launchpad pad) {
        LaunchpadHandler handler = launchpadManager.getHandler(pad);
        if (handler == null) return;
        handler.launch(player);
    }

    public Map<String, Launchpad> getLaunchpads() {
        return launchpads;
    }

//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//        Location to = event.getTo();
//
//        Location blockBelow = to.clone().subtract(0, 1, 0);
//        if (blockBelow.getBlock().getType() != Material.SLIME_BLOCK) return;
//        if (launchingPlayers.contains(player.getUniqueId())) return;
//
//        Location blockLoc = blockBelow.getBlock().getLocation();
//
//        for (Launchpad pad : launchpads.values()) {
//            boolean isOnLinkedBlock = pad.getLinkedBlocks().stream()
//                    .anyMatch(loc -> loc.getBlockX() == blockLoc.getBlockX()
//                            && loc.getBlockY() == blockLoc.getBlockY()
//                            && loc.getBlockZ() == blockLoc.getBlockZ()
//                            && loc.getWorld().equals(blockLoc.getWorld()));
//
//            if (isOnLinkedBlock && pad.hasTarget()) {
//                launchingPlayers.add(player.getUniqueId()); // Add player to launching set
//
//                ArmorStand stand = to.getWorld().spawn(to, ArmorStand.class);
//                stand.setVisible(false);
//                stand.setGravity(false);
//                stand.setInvulnerable(true);
//                stand.addPassenger(player);
//
//                new LaunchTask(this, stand, player, pad) {
//                    @Override
//                    public void cancel() {
//                        super.cancel();
//                        launchingPlayers.remove(player.getUniqueId()); // Remove player when done
//                    }
//                }.runTaskTimer(this, 1L, 1L);
//                break;
//            }
//        }
//    }

//    @EventHandler
//    public void onVehicleExit(VehicleExitEvent event) {
//        if (event.getVehicle() instanceof ArmorStand && event.getExited() instanceof Player) {
//            event.setCancelled(true);
//        }
//    }
}