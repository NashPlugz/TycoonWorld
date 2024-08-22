package me.nashplugz.tycoonw;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.ChatColor;

public class PlotListener implements Listener {
    private final PlotManager plotManager;
    private final TycoonWorld plugin;

    public PlotListener(PlotManager plotManager, TycoonWorld plugin) {
        this.plotManager = plotManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        Player player = event.getPlayer();
        Plot fromPlot = plotManager.getPlotAt(from);
        Plot toPlot = plotManager.getPlotAt(to);

        boolean isInFromPlot = plotManager.isInPlot(from);
        boolean isInToPlot = plotManager.isInPlot(to);

        if (fromPlot != toPlot || isInFromPlot != isInToPlot) {
            if (isInToPlot) {
                if (toPlot != null && toPlot.hasOwner()) {
                    if (toPlot.isOwner(player.getUniqueId())) {
                        player.sendMessage(ChatColor.GREEN + "Welcome back to your plot!");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Entering plot owned by " + toPlot.getOwnerName());
                    }
                } else {
                    player.sendMessage(ChatColor.AQUA + "You have entered an unclaimed plot. Type " +
                            ChatColor.YELLOW + "/plot claim" + ChatColor.AQUA + " to purchase this plot.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plotManager.canBuildAt(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't build here!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plotManager.canBuildAt(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here!");
        }
    }
}
