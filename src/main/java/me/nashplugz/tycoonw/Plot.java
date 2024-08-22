package me.nashplugz.tycoonw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.UUID;

public class Plot {
    private final Location corner;
    private UUID owner;

    public Plot(Location corner) {
        this.corner = corner;
    }

    public Location getCorner() {
        return corner;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean isOwner(UUID playerId) {
        return playerId.equals(owner);
    }

    public String getOwnerName() {
        if (owner == null) return "Unclaimed";
        return Bukkit.getOfflinePlayer(owner).getName();
    }
}
