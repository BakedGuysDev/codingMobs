package com.egirlsnation.codingMobs.events;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.egirlsnation.codingMobs.Config;
import com.egirlsnation.codingMobs.Main;

import net.md_5.bungee.api.ChatColor;

public class PlayerJoinListener implements Listener {

	private Main plugin;
	private Logger log;

	public PlayerJoinListener(Main plugin) {

		this.plugin = plugin;
		this.log = plugin.log;

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		if (Config.isDebugging()) {
			log.info("Caught player join event.");
		}

		if (Config.isWelcomeEnabled()) {

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {

					event.getPlayer()
							.sendMessage(Config.getWelcomeMessageHeaderColor() + "["
									+ Config.getMessage("welcome-message-header") + "] "
									+ Config.getWelcomeMessageColor() + Config.getMessage("welcome-message"));

					if (Config.isDebugging())
						log.info("Sent welcome message to player: " + event.getPlayer().getName());

				}

			}, Config.getWelcomeMessageDelay());

		}

	}

}
