package me.nashplugz.tycoonw;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import net.milkbowl.vault.economy.Economy;

public class PlotCommand implements CommandExecutor {
    private final PlotManager plotManager;
    private final Economy economy;
    private final TycoonWorld plugin;

    public PlotCommand(PlotManager plotManager, Economy economy, TycoonWorld plugin) {
        this.plotManager = plotManager;
        this.economy = economy;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreateCommand(player);
            case "claim":
                return handleClaimCommand(player);
            case "delete":
                return handleDeleteCommand(player);
            case "teleport":
            case "tp":
                return handleTeleportCommand(player, args);
            default:
                sendHelpMessage(player);
                return true;
        }
    }

    private boolean handleCreateCommand(Player player) {
        Location location = plotManager.findNearestUnclaimedPlot(plugin.getServer().getWorld("TycoonWorld"));
        if (location == null) {
            player.sendMessage(ChatColor.RED + "No available plots found.");
            return true;
        }

        if (plotManager.claimPlot(player, location)) {
            // Ensure the chunk is loaded before teleporting
            location.getChunk().load();
            // Get the teleport location
            Location teleportLocation = plotManager.getPlotTeleportLocation(location);
            player.teleport(teleportLocation);
            player.sendMessage(ChatColor.GREEN + "You have successfully created and claimed a new plot!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to create a new plot. You may have reached the maximum number of plots.");
        }
        return true;
    }

    private boolean handleClaimCommand(Player player) {
        Location playerLocation = player.getLocation();
        if (!plotManager.isInPlot(playerLocation)) {
            player.sendMessage(ChatColor.RED + "You must be standing in an unclaimed plot to claim it.");
            return true;
        }

        if (plotManager.isPlotOwned(playerLocation)) {
            player.sendMessage(ChatColor.RED + "This plot is already owned.");
            return true;
        }

        if (plotManager.claimPlot(player, playerLocation)) {
            player.sendMessage(ChatColor.GREEN + "You have successfully claimed this plot!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to claim the plot. You may have reached the maximum number of plots.");
        }
        return true;
    }

    private boolean handleDeleteCommand(Player player) {
        Location playerLocation = player.getLocation();
        if (!plotManager.isInPlot(playerLocation)) {
            player.sendMessage(ChatColor.RED + "You must be standing in your plot to delete it.");
            return true;
        }

        if (!plotManager.isPlotOwned(playerLocation)) {
            player.sendMessage(ChatColor.RED + "This plot is not owned by anyone.");
            return true;
        }

        Plot plot = plotManager.getPlotAt(playerLocation);
        if (plot == null || !plot.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't own this plot.");
            return true;
        }

        if (plotManager.unclaimPlot(player, playerLocation)) {
            player.sendMessage(ChatColor.GREEN + "You have successfully deleted your plot.");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to delete the plot. Please try again.");
        }
        return true;
    }

    private boolean handleTeleportCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /plot teleport <plot number>");
            return true;
        }

        int plotNumber;
        try {
            plotNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid plot number. Please use a number.");
            return true;
        }

        Location plotLocation = plotManager.getPlayerPlotLocation(player, plotNumber);
        if (plotLocation == null) {
            player.sendMessage(ChatColor.RED + "You don't own a plot with that number.");
            return true;
        }

        // Get the teleport location
        Location teleportLocation = plotManager.getPlotTeleportLocation(plotLocation);
        player.teleport(teleportLocation);
        player.sendMessage(ChatColor.GREEN + "Teleported to your plot #" + plotNumber);
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== TycoonWorld Plot Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/plot create - Create and claim a new plot");
        player.sendMessage(ChatColor.YELLOW + "/plot claim - Claim the plot you're standing in");
        player.sendMessage(ChatColor.YELLOW + "/plot delete - Delete the plot you're standing in");
        player.sendMessage(ChatColor.YELLOW + "/plot teleport <number> - Teleport to your plot");
    }
}
