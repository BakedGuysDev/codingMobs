package com.egirlsnation.codingMobs;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.egirlsnation.codingMobs.commands.DebugCommands;
import com.egirlsnation.codingMobs.commands.SpawnCommands;
import com.egirlsnation.codingMobs.commands.SpawnTabCompleter;
import com.egirlsnation.codingMobs.events.ChunkLoadListener;
import com.egirlsnation.codingMobs.events.MobEventListener;

public class Main extends JavaPlugin {

	public Logger log;
	public boolean supportsInventoryStackSize = true;

	@Override
	public void onEnable() {

		log = this.getLogger();

		// Initialize classes and commands
		this.getCommand("debug").setExecutor(new DebugCommands(this));
		this.getCommand("spawn").setExecutor(new SpawnCommands(this));
		this.getCommand("spawn").setTabCompleter(new SpawnTabCompleter());
		
		// Register the plugin listener
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MobEventListener(this), this);
		pm.registerEvents(new ChunkLoadListener(this), this);
		
		log.info("codingMobs plugin has been enabled!");

	}

	@Override
	public void onDisable() {
		log.info("codingMobs plugin has been disabled!");
	}

}
