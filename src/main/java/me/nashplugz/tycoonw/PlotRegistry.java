package me.nashplugz.tycoonw;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PlotRegistry {
    private final Map<Long, Plot> plots = new HashMap<>();

    public Plot getPlot(Location location) {
        return plots.get(locationToLong(location));
    }

    public boolean isPlotOwned(Location location) {
        Plot plot = plots.get(locationToLong(location));
        return plot != null && plot.hasOwner();
    }

    public boolean claimPlot(UUID playerUUID, Location location) {
        long key = locationToLong(location);
        if (isPlotOwned(location)) return false;
        plots.computeIfAbsent(key, k -> new Plot(location)).setOwner(playerUUID);
        return true;
    }

    public boolean unclaimPlot(UUID playerUUID, Location location) {
        Plot plot = plots.get(locationToLong(location));
        if (plot != null && plot.isOwner(playerUUID)) {
            plot.setOwner(null);
            return true;
        }
        return false;
    }

    public int getOwnedPlotsCount(UUID playerUUID) {
        return (int) plots.values().stream()
                .filter(plot -> plot.isOwner(playerUUID))
                .count();
    }

    public void saveToConfig(YamlConfiguration config) {
        for (Map.Entry<Long, Plot> entry : plots.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Plot plot = entry.getValue();
            if (plot.hasOwner()) {
                config.set(key + ".owner", plot.getOwner().toString());
            }
        }
    }

    public void loadFromConfig(YamlConfiguration config, World world) {
        plots.clear();
        for (String key : config.getKeys(false)) {
            try {
                long packedLoc = Long.parseLong(key);
                String ownerString = config.getString(key + ".owner");
                if (ownerString != null) {
                    UUID owner = UUID.fromString(ownerString);
                    Plot plot = new Plot(longToLocation(world, packedLoc));
                    plot.setOwner(owner);
                    plots.put(packedLoc, plot);
                }
            } catch (NumberFormatException e) {
                // Handle NumberFormatException
            } catch (IllegalArgumentException e) {
                // Handle IllegalArgumentException
            }
        }
    }

    private long locationToLong(Location location) {
        return ((long) location.getBlockX() << 32) | (location.getBlockZ() & 0xFFFFFFFFL);
    }

    private Location longToLocation(World world, long packed) {
        int x = (int) (packed >> 32);
        int z = (int) packed;
        return new Location(world, x, PlotWorld.PLOT_HEIGHT, z);
    }

    public List<Plot> getPlayerPlots(UUID playerUUID) {
        return plots.values().stream()
                .filter(plot -> plot.isOwner(playerUUID))
                .collect(Collectors.toList());
    }
}
