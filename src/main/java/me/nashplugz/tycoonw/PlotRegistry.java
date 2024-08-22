package me.nashplugz.tycoonw;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

public class PlotRegistry {
    private final Map<Location, Plot> plots;

    public PlotRegistry() {
        this.plots = new HashMap<>();
    }

    public Plot getPlot(Location location) {
        return plots.get(location);
    }

    public boolean isPlotOwned(Location location) {
        Plot plot = plots.get(location);
        return plot != null && plot.hasOwner();
    }

    public boolean claimPlot(UUID playerUUID, Location location) {
        if (isPlotOwned(location)) return false;
        plots.computeIfAbsent(location, Plot::new).setOwner(playerUUID);
        return true;
    }

    public boolean unclaimPlot(UUID playerUUID, Location location) {
        Plot plot = plots.get(location);
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
        for (Map.Entry<Location, Plot> entry : plots.entrySet()) {
            String key = locationToString(entry.getKey());
            Plot plot = entry.getValue();
            if (plot.hasOwner()) {
                config.set(key + ".owner", plot.getOwner().toString());
            }
        }
    }

    public void loadFromConfig(YamlConfiguration config) {
        plots.clear();
        for (String key : config.getKeys(false)) {
            Location location = stringToLocation(key);
            if (location != null) {
                String ownerString = config.getString(key + ".owner");
                if (ownerString != null) {
                    UUID owner = UUID.fromString(ownerString);
                    Plot plot = new Plot(location);
                    plot.setOwner(owner);
                    plots.put(location, plot);
                }
            }
        }
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private Location stringToLocation(String str) {
        // Implementation depends on your specific needs
        // This is just a placeholder
        return null;
    }

    public List<Plot> getPlayerPlots(UUID playerUUID) {
        return plots.values().stream()
                .filter(plot -> plot.isOwner(playerUUID))
                .collect(Collectors.toList());
    }
}
