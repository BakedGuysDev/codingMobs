package com.egirlsnation.codingMobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;

public final class Config {

	private static Main plugin;
	public static FileConfiguration cfg;
	private static File cfgFile;

	// Available settings variables
	private static boolean debug;
	private static boolean welcomeMessage;
	private static boolean thiefMessage;
	private static String welcomeMessageColor;
	private static String thiefMessageColor;
	private static ConfigurationSection cfgMessagesMap;

	public static void init(Main ci) {
		plugin = ci;

		cfgFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
		cfg = YamlConfiguration.loadConfiguration(cfgFile);

		loadCfg();

	}

	public static void loadCfg() {

		try {
			cfg.load(cfgFile);
			cfgMessagesMap = cfg.getConfigurationSection("messages");
			debug = cfg.getBoolean("display-debug-messages");
			welcomeMessage = cfg.getBoolean("display-welcome-message");
			thiefMessage = cfg.getBoolean("display-thief-message");
			welcomeMessageColor = cfg.getString("welcome-message-color");
			thiefMessageColor = cfg.getString("thief-message-color");
		} catch (FileNotFoundException e) {
			setupCfg();
		} catch (IOException | InvalidConfigurationException e) {
			plugin.log.warning("IO exception while reading plugin config");
			e.printStackTrace();
		}

	}

	// Sets up the default variables if they don't exist yet.
	public static void setupCfg() {
		// Print debug message
		plugin.log.warning("config.yml not found. Creating a new one");

		// Add sexy ass header with instructions for idiots to follow
		String header = "General options:\n"
				+ "display-debug-messages, enable the display of plugin debug messages.\n"
				+ "display-welcome-message, enable the display of a custom welcome message to players when they join.\n"
				+ "display-thief-message, enable the display of message when thief steals a player items.\n"
				+ "welcome-message-color, select custom color for welcome message from mojang chat colors ex. red, green, aqua.\n"
				+ "thief-message-color, select custom color for thief message from mojang chat colors ex. red, green, aqua.\n"
				+ "messages section, you can edit the messages if you want custom funny ones.\n";
		cfg.options().header(header);

		cfg.addDefault("display-debug-messages", false);
		cfg.addDefault("display-welcome-message", true);
		cfg.addDefault("display-thief-message", true);
		cfg.addDefault("welcome-message-color", "red");
		cfg.addDefault("thief-message-color", "red");

		// messages section keys and values
		Map<String, Object> messages = new HashMap<String, Object>();
		messages.put("no-perm", "BAKA! You don't have permission to use this command!");
		messages.put("no-player", "NANI, Only players can use this command!");
		messages.put("welcome-message", "Merry Christmas, beware of the new sussy christmas mobs!");
		messages.put("thief-message", "The dirty thief stole your items, kill him to get your items back!");
		// janky ass code but should work to add values to message section
		cfg.createSection("messages");
		for (String i : messages.keySet()) {
			cfg.getConfigurationSection("messages").addDefault(i, messages.get(i));
		}

		if (!cfg.isSet("messages") || !cfg.isSet("stack-size") || !cfg.isSet("debug-messages")) {
			cfg.options().copyDefaults(true);
			saveConfig(cfg, cfgFile);
		}

		loadCfg();

	}

	public static void saveConfig(FileConfiguration fileConfig, File file) {
		try {
			fileConfig.save(file);
		} catch (IOException e) {
			plugin.log.warning("Error writing config file.");
			e.printStackTrace();
		}
	}

	public static String getMessage(String key) {
		String result = "";

		try {
			result = cfgMessagesMap.getString(key);
		} catch (Exception ex) {
			plugin.log.warning("Error getting requested message from config.");
			ex.printStackTrace();
		}

		return result;
	}

	public static boolean isDebugging() {
		return debug;
	}

	public static boolean isWelcomeEnabled() {
		return welcomeMessage;
	}

	public static boolean isThiefMessageEnabled() {
		return thiefMessage;
	}

	public static ChatColor getWelcomeMessageColor() {

		ChatColor returnColor;

		try {
			returnColor = ChatColor.of(welcomeMessageColor);
		} catch (Exception ex) {
			plugin.log.warning("Wrong welcome message color code using default red color.");
			returnColor = ChatColor.RED;
		}

		return returnColor;

	}

	public static ChatColor getThiefMessageColor() {

		ChatColor returnColor;

		try {
			returnColor = ChatColor.of(thiefMessageColor);
		} catch (Exception ex) {
			plugin.log.warning("Wrong thief message color code, using default red color.");
			returnColor = ChatColor.RED;
		}

		return returnColor;

	}

}