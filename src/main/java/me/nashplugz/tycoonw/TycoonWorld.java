package me.nashplugz.tycoonw;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class TycoonWorld extends JavaPlugin {
    private PlotManager plotManager;
    private Economy economy;
    private PlotRegistry plotRegistry;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling TycoonWorld.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.plotRegistry = new PlotRegistry();
        setupTycoonWorld();
        plotManager = new PlotManager(this, plotRegistry);

        getServer().getPluginManager().registerEvents(new PlotListener(plotManager, this), this);
        getCommand("plot").setExecutor(new PlotCommand(plotManager, economy, this));

        getLogger().info("TycoonWorld has been enabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    private void setupTycoonWorld() {
        World tycoonWorld = Bukkit.getWorld("TycoonWorld");
        if (tycoonWorld == null) {
            WorldCreator creator = new WorldCreator("TycoonWorld");
            creator.generator(new PlotWorld());
            tycoonWorld = creator.createWorld();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("TycoonWorld has been disabled!");
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }
}
