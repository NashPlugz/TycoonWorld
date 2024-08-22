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
            case "teleport":
            case "tp":
                return handleTeleportCommand(player, args);
            default:
                sendHelpMessage(player);
                return true;
        }
    }

    private boolean handleCreateCommand(Player player) {
        Location location = plotManager.findNearestUnclaimedPlot(player.getWorld());
        if (location == null) {
            player.sendMessage(ChatColor.RED + "No available plots found.");
            return true;
        }

        if (plotManager.claimPlot(player, location)) {
            player.teleport(location.add(0.5, 1, 0.5)); // Teleport to the center of the plot
            player.sendMessage(ChatColor.GREEN + "You have successfully created and claimed a new plot!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to create a new plot. You may have reached the maximum number of plots.");
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

        // Teleport to the center of the plot
        plotLocation.add(PlotWorld.PLOT_SIZE / 2.0, 1, PlotWorld.PLOT_SIZE / 2.0);
        player.teleport(plotLocation);
        player.sendMessage(ChatColor.GREEN + "Teleported to your plot #" + plotNumber);
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== TycoonWorld Plot Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/plot create - Create and claim a new plot");
        player.sendMessage(ChatColor.YELLOW + "/plot teleport <number> - Teleport to your plot");
        // Add more command descriptions as needed
    }
}
