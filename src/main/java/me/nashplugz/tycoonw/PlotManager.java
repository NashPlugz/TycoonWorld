package me.nashplugz.tycoonw;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public class PlotManager {
    private final TycoonWorld plugin;
    private final PlotRegistry plotRegistry;
    private final int maxPlotsPerPlayer;

    public PlotManager(TycoonWorld plugin, PlotRegistry plotRegistry) {
        this.plugin = plugin;
        this.plotRegistry = plotRegistry;
        this.maxPlotsPerPlayer = plugin.getConfig().getInt("max-plots-per-player", 2);
    }

    public Plot getPlotAt(Location location) {
        int plotX = Math.floorDiv(location.getBlockX(), PlotWorld.TOTAL_SIZE);
        int plotZ = Math.floorDiv(location.getBlockZ(), PlotWorld.TOTAL_SIZE);
        Location plotCorner = new Location(location.getWorld(),
                plotX * PlotWorld.TOTAL_SIZE,
                PlotWorld.PLOT_HEIGHT,
                plotZ * PlotWorld.TOTAL_SIZE);
        return plotRegistry.getPlot(plotCorner);
    }

    public boolean isInPlot(Location location) {
        int relativeX = Math.floorMod(location.getBlockX(), PlotWorld.TOTAL_SIZE);
        int relativeZ = Math.floorMod(location.getBlockZ(), PlotWorld.TOTAL_SIZE);
        return relativeX >= PlotWorld.BORDER_WIDTH &&
                relativeX < PlotWorld.PLOT_SIZE + PlotWorld.BORDER_WIDTH &&
                relativeZ >= PlotWorld.BORDER_WIDTH &&
                relativeZ < PlotWorld.PLOT_SIZE + PlotWorld.BORDER_WIDTH;
    }

    public Location findNearestUnclaimedPlot(World world) {
        int x = 0, z = 0;
        while (true) {
            Location plotLoc = new Location(world,
                    x * PlotWorld.TOTAL_SIZE + PlotWorld.BORDER_WIDTH,
                    PlotWorld.PLOT_HEIGHT + 1,
                    z * PlotWorld.TOTAL_SIZE + PlotWorld.BORDER_WIDTH);
            if (!plotRegistry.isPlotOwned(plotLoc)) {
                return plotLoc;
            }
            if (x > z) z++; else x++;
        }
    }

    public boolean claimPlot(Player player, Location location) {
        return getOwnedPlotsCount(player) < maxPlotsPerPlayer &&
                plotRegistry.claimPlot(player.getUniqueId(), location);
    }

    public void claimInitialPlot(Location location) {
        Plot plot = getPlotAt(location);
        if (plot != null && !plot.hasOwner()) {
            plot.setOwner(null); // Set to null to indicate it's the initial unclaimed plot
        }
    }

    public boolean canBuildAt(Player player, Location location) {
        Plot plot = getPlotAt(location);
        if (plot == null) {
            return false; // This is a border or path area
        }
        if (!isInPlot(location)) {
            return false; // This is a border or path area
        }
        return plot.isOwner(player.getUniqueId()) || (plot.getOwner() == null && getOwnedPlotsCount(player) == 0);
    }

    public boolean unclaimPlot(Player player, Location location) {
        return plotRegistry.unclaimPlot(player.getUniqueId(), location);
    }

    public boolean isPlotOwned(Location location) {
        return plotRegistry.isPlotOwned(location);
    }

    public int getOwnedPlotsCount(Player player) {
        return plotRegistry.getOwnedPlotsCount(player.getUniqueId());
    }

    public int getMaxPlotsPerPlayer() {
        return maxPlotsPerPlayer;
    }

    public Location getPlayerPlotLocation(Player player, int plotNumber) {
        List<Plot> playerPlots = plotRegistry.getPlayerPlots(player.getUniqueId());
        if (plotNumber < 1 || plotNumber > playerPlots.size()) {
            return null;
        }
        return playerPlots.get(plotNumber - 1).getCorner();
    }
}
