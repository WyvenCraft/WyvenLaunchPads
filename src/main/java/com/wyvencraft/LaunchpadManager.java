package com.wyvencraft;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LaunchpadManager {
    private final Plugin plugin;
    private final File file;
    private final Set<LaunchpadHandler> padHandlers = new HashSet<>();
    private FileConfiguration config;

    public LaunchpadManager(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "launchpads.yml");
        this.loadConfig();
    }

    private void loadConfig() {
        if (!file.exists()) {
            try {
                // Create plugin folder if it doesn't exist
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }

                // Create the file
                file.createNewFile();

                // Initialize with empty launchpads section
                config = new YamlConfiguration();
                config.createSection("launchpads");
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create launchpads.yml!");
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveLaunchpads(Map<String, Launchpad> launchpads) {
        config.set("launchpads", null); // Clear existing data
        ConfigurationSection section = config.createSection("launchpads");

        for (Map.Entry<String, Launchpad> entry : launchpads.entrySet()) {
            ConfigurationSection padSection = section.createSection(entry.getKey());
            Launchpad pad = entry.getValue();

            // Save source location
            saveLocation(padSection.createSection("source"), pad.getSourceLocation());

            // Save target location if exists
            if (pad.hasTarget()) {
                saveLocation(padSection.createSection("target"), pad.getTargetLocation());
            }

            // Save linked blocks
            ConfigurationSection blocksSection = padSection.createSection("linked_blocks");
            int i = 0;
            for (Location loc : pad.getLinkedBlocks()) {
                saveLocation(blocksSection.createSection(String.valueOf(i++)), loc);
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save launchpads to " + file.getName());
        }
    }

    public void loadLaunchpads(Map<String, Launchpad> launchpads) {
        launchpads.clear();
        ConfigurationSection section = config.getConfigurationSection("launchpads");

        if (section == null) return;

        for (String name : section.getKeys(false)) {
            ConfigurationSection padSection = section.getConfigurationSection(name);
            if (padSection == null) continue;

            // Load source location
            Location source = loadLocation(padSection.getConfigurationSection("source"));
            if (source == null) continue;

            String permission = padSection.getString("permission", "");
            Launchpad pad = new Launchpad(source, permission);

            // Load target location
            Location target = loadLocation(padSection.getConfigurationSection("target"));
            if (target != null) {
                pad.setTarget(target);
            }

            // Load linked blocks
            ConfigurationSection blocksSection = padSection.getConfigurationSection("linked_blocks");
            if (blocksSection != null) {
                for (String key : blocksSection.getKeys(false)) {
                    Location blockLoc = loadLocation(blocksSection.getConfigurationSection(key));
                    if (blockLoc != null) {
                        pad.addLinkedBlock(blockLoc);
                    }
                }
            }

            launchpads.put(name, pad);
            padHandlers.add(new LaunchpadHandler(plugin, pad));
        }
    }

    public Set<LaunchpadHandler> getPadHandlers() {
        return padHandlers;
    }

    public LaunchpadHandler getHandler(Launchpad launchpad) {
        for (LaunchpadHandler handler : padHandlers) {
            if (handler.getLaunchpad().equals(launchpad)) {
                return handler;
            }
        }
        return null;
    }

    private void saveLocation(ConfigurationSection section, Location location) {
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    private Location loadLocation(ConfigurationSection section) {
        if (section == null) return null;
        return new Location(
                plugin.getServer().getWorld(section.getString("world", "")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }
}