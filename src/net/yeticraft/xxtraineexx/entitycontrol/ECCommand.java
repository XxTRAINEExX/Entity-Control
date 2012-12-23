package net.yeticraft.xxtraineexx.entitycontrol;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

/**
 * @author XxTRAINEExX
 * This class holds all of the command structure for the plugin. There is also a reporting
 * method that probably doesn't belong here... but I'm tired so it's going here.
 *
 */
public class ECCommand implements CommandExecutor {

	private final EntityControl plugin;

	public ECCommand(EntityControl plugin) {
		this.plugin = plugin;
	}

	enum SubCommand {

		HELP,
		GO,
		STATS,
		TP,
		RESETSTATS,
		DEBUG,
		RELOAD,
		TOGGLE,
		UNKNOWN;

		private static SubCommand toSubCommand(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return UNKNOWN;
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("ec.command")) {
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
			sender.sendMessage(ChatColor.DARK_AQUA + "===============");
			sender.sendMessage(ChatColor.AQUA + "Try /" + command.getName() + " HELP");
			return true;
		}
		if (args.length > 2) {
			sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
			sender.sendMessage(ChatColor.DARK_AQUA + "===============");
			sender.sendMessage(ChatColor.AQUA + "Looks like you typed too many parameters.");
			return true;
		}


		switch (SubCommand.toSubCommand(args[0])) {
			case HELP:
				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl Help");
				sender.sendMessage(ChatColor.DARK_AQUA + "====================");
				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec HELP");
					return true;
				}

				sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " HELP: Shows this help page");
				if (sender.hasPermission("ec.stats")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " STATS: Lists current stats.");
				}
				if (sender.hasPermission("ec.tp")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TP <num>: Teleport to a given location.");
				}
				if (sender.hasPermission("ec.resetstats")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " RESETSTATS: Clears all stats.");
				}
				if (sender.hasPermission("ec.debug")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " DEBUG: Enables DEBUG mode on the console.");
				}
				if (sender.hasPermission("ec.reload")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " RELOAD: Reloads config from disk.");
				}
				if (sender.hasPermission("ec.toggle")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TOGGLE: Enables/Disables the plugin.");
				}
				break;
			case STATS:
/*
				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl Stats");
				sender.sendMessage(ChatColor.DARK_AQUA + "=====================");

				// Check permissions for STATS command
				if (!sender.hasPermission("ec.stats")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec STATS");
					return true;
				}
				findTopSpawners(sender); */
				break;

			case GO:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl GO");
				sender.sendMessage(ChatColor.DARK_AQUA + "================");

				// Check permissions for GO command
				if (!sender.hasPermission("ec.go")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec GO");
					return true;
				}

				int clearedEntities = 0;
				
				// Cycling through all worlds
				for (World world : plugin.getServer().getWorlds()) { 
					
					// Cycling through all loaded chunks in at particular world
					for (Chunk chunk : world.getLoadedChunks()) { 
						
						Entity[] entityList = chunk.getEntities();
						
						// Doing some work if the entity count in that chunk is too high
						if(entityList.length > 100){
							
							// Cycling through all entities in the list
							for (Entity entity : entityList){
								
								// If it's not alive I'm going to remove it.
								if (!entity.getType().isAlive()){
									entity.remove();
									clearedEntities++;
								}
							}
						}
						sender.sendMessage(ChatColor.DARK_AQUA + "Entities Cleared: [" + clearedEntities + "]" + chunk.toString());
					} 
					
				}
				
				break;

				
			case TP:
/*
				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl TP");
				sender.sendMessage(ChatColor.DARK_AQUA + "==================");

				// Not going to allow TP for the console
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_AQUA + "How do you expect to teleport from a console?");
					return true;
				}

				// Check permissions for TP command
				if (!sender.hasPermission("ec.tp")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				// Did they type too many parameters?
				if (args.length > 2) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec TP");
					return true;
				}

				// Did they only type 1 parameter?
				if (args.length == 1) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " TP <type> <num>: Teleport to a given stat location.");
					sender.sendMessage(ChatColor.AQUA + "<num> :  Number pulled from the STATS list.");
					return true;
				}

				// Determing if they actually entered a number (Not a string)
				int spawnNumber;
				try {
					spawnNumber = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER");
					return true;
				}

				// Making sure they entered a valid number in the hashmap
				if ((spawnNumber >= topSpawners.size())
						|| (spawnNumber < 0)) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER from the STATS command. Rerun STATS and verify your entry.");
					return true;
				}
				if (topSpawners.get(spawnNumber) == null) {
					sender.sendMessage(ChatColor.AQUA + "You did not enter a valid NUMBER from the STATS command. Rerun STATS and verify your entry.");
					return true;
				}

				// Teleport player to spawner
				Player player = (Player) sender; // Cast already checked near beginning of command handler
				player.teleport(topSpawners.get(spawnNumber).getLocation());
				sender.sendMessage(ChatColor.AQUA + "Teleporting you to spawner #" + spawnNumber);
				if (plugin.debug) {
					plugin.getLogger().info(sender.getName() + " teleported to spawner at: [" + topSpawners.get(spawnNumber).getLocation().getBlockX()
							+ "," + topSpawners.get(spawnNumber).getLocation().getBlockY() + "," + topSpawners.get(spawnNumber).getLocation().getBlockZ() + "]");
				}*/
				break;
			case RESETSTATS:
/*
				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("ec.resetstats")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec RESETSTATS");
					return true;
				}

				plugin.myListener.activeMobs.clear();
				plugin.myListener.activeSpawners.clear();
				topSpawners.clear();
				sender.sendMessage(ChatColor.AQUA + "All stats reset successfully!");
				plugin.getLogger().info("All stats cleared from the server by " + sender.getName());*/
				break;

			case DEBUG:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("ec.debug")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec DEBUG");
					return true;
				}

				if (plugin.debug) {
					plugin.debug = false;
					sender.sendMessage(ChatColor.AQUA + "Debugging Disabled!");
					plugin.getLogger().info("Debugging disabled by " + sender.getName());
				} else {
					plugin.debug = true;
					sender.sendMessage(ChatColor.AQUA + "Debugging Enabled!");
					plugin.getLogger().info("Debugging enabled by " + sender.getName());
				}
				plugin.saveMainConfig();
				break;
			case RELOAD:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("ec.reload")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec RELOAD");
					return true;
				}

				plugin.reloadConfig();
				plugin.loadMainConfig();
				if (plugin.debug) {
					plugin.getLogger().info("Config reloaded from disk.");
				}
				sender.sendMessage(ChatColor.AQUA + "Config reloaded from disk.");

				break;

			case TOGGLE:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for STATS command
				if (!sender.hasPermission("ec.toggle")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec TOGGLE");
					return true;
				}

				if (plugin.pluginEnable) {
					plugin.pluginEnable = false;
					sender.sendMessage(ChatColor.AQUA + "Plugin Disabled!");
					plugin.getLogger().info("Plugin disabled by " + sender.getName());
				} else {
					plugin.pluginEnable = true;
					sender.sendMessage(ChatColor.AQUA + "Plugin Enabled!");
					plugin.getLogger().info("Plugin enabled by " + sender.getName());
				}
				plugin.saveMainConfig();
				break;
			case UNKNOWN:
				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");
				sender.sendMessage(ChatColor.AQUA + "Unknown command. Use /ec HELP to list available commands.");
		}

		return true;
	}

}
