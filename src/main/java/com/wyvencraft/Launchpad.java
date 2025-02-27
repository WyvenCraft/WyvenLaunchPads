package com.wyvencraft;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class Launchpad {
    private final Location sourceLocation;
    private final Set<Location> linkedBlocks;
    private Location targetLocation;

    public Launchpad(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
        this.linkedBlocks = new HashSet<>();
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
}