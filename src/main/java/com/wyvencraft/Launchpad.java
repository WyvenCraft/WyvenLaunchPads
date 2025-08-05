package com.wyvencraft;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class Launchpad {
    private final Location sourceLocation;
    private final Set<Location> linkedBlocks;
    private Location targetLocation;
    private String permission;

    public Launchpad(Location sourceLocation, String permission) {
        this.sourceLocation = sourceLocation;
        this.linkedBlocks = new HashSet<>();
        this.permission = permission;
    }

    public void setTarget(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Set<Location> getLinkedBlocks() {
        return linkedBlocks;
    }

    public void addLinkedBlock(Location location) {
        linkedBlocks.add(location);
    }

    public boolean removeLinkedBlock(Location location) {
        return linkedBlocks.remove(location);
    }

    public boolean hasTarget() {
        return targetLocation != null;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}