package net.yeticraft.xxtraineexx.entitycontrol;

import java.util.Iterator;
import java.util.Map;

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
				if (sender.hasPermission("ec.go")) {
					sender.sendMessage(ChatColor.AQUA + " /" + command.getName() + " GO: Executes Chunk Cleanup.");
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
			case GO:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl GO");
				sender.sendMessage(ChatColor.DARK_AQUA + "================");
				
				if (!plugin.pluginEnable) {
				    sender.sendMessage(ChatColor.DARK_AQUA + "Plugin is manually disabled. Try re-enabling it!");
		            return true; // Plugin has been manually disabled
		        }
				
				// Check permissions for GO command
				if (!sender.hasPermission("ec.go")) {
					sender.sendMessage(ChatColor.DARK_AQUA + "Permissions DENIED.");
					return true;
				}

				if (args.length > 1) {
					sender.sendMessage(ChatColor.AQUA + "Too manyparameters! Try /ec GO");
					return true;
				}

				// Check current playerDeaths MAP and remove any old entries
				Iterator<Map.Entry<String,Long>> iter = plugin.myListener.playerDeaths.entrySet().iterator();
				while (iter.hasNext()) {
				    Map.Entry<String,Long> entry = iter.next();
				    				    
				    if((System.currentTimeMillis() - entry.getValue()) > (plugin.deathBufferSeconds * 1000.0)){
				    	
				    	if (plugin.debug) {plugin.getLogger().info("Removed player death from: " + entry.getKey() + ". Player died [" + ((System.currentTimeMillis() - entry.getValue()) / 1000.0) + "] seconds ago.");}
				    	iter.remove();
				    	continue;
				    }
				    
				    if (plugin.debug) {plugin.getLogger().info("Death in: " + entry.getKey() + " kept. Player died [" + ((System.currentTimeMillis() - entry.getValue()) / 1000.0) + "] seconds ago.");}
                    
				}

				
				
				// Cycling through all worlds
				for (World world : plugin.getServer().getWorlds()) { 
					
					// Cycling through all loaded chunks in at particular world
					for (Chunk chunk : world.getLoadedChunks()) { 
						
						int clearedEntities = 0;
						int keptEntities = 0;
						Entity[] entityList = chunk.getEntities();
						
						// Doing some work if the entity count in that chunk is too high
						if (entityList.length < plugin.entityCountPerChunk) {
						    continue;
						}
						if (plugin.myListener.playerDeaths.get(world.toString() + "-" + chunk.toString()) != null) {
						    if (plugin.debug){plugin.getLogger().info(world.toString() + "-" + chunk.toString() + 
						            " has a death logged [" + ((System.currentTimeMillis() - (plugin.myListener.playerDeaths.get(world.toString() + 
						            "-" + chunk.toString())))/1000.0) + "] seconds ago.  Skipping because it happened < " + plugin.deathBufferSeconds + " seconds ago.");}
						    continue;
						}

						
						// Cycling through all entities in the list
						for (Entity entity : entityList){
							
							// If it's not alive I'm going to remove it.
							if (!entity.getType().isAlive()){
							    entity.remove();
								clearedEntities++;
								continue;
							}
							
	                         if (plugin.debug){plugin.getLogger().info("Chunk: " + world.toString() + "-" + chunk.toString() + 
	                                 " Entity: " + entity.toString() + " is alive.  Skipping.");}
	                         keptEntities++;
	                        
						}
						sender.sendMessage(ChatColor.DARK_AQUA +  world.toString() + " - " + chunk.toString() + ": Entities Cleared [" + clearedEntities + "]");
						sender.sendMessage(ChatColor.DARK_AQUA +  world.toString() + " - " + chunk.toString() + ": Entities Kept [" + keptEntities + "]");
					}
				} 
					
				sender.sendMessage(ChatColor.DARK_AQUA +  "Chunk cleanup complete.");
				
				break;

				
			case DEBUG:

				sender.sendMessage(ChatColor.DARK_AQUA + "EntityControl");
				sender.sendMessage(ChatColor.DARK_AQUA + "===============");

				// Check permissions for DEBUG command
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

				// Check permissions for RELOAD command
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

				// Check permissions for TOGGLE command
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
