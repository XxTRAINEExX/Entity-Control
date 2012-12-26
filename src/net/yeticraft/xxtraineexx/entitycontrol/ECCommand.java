package net.yeticraft.xxtraineexx.entitycontrol;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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

				plugin.cleanChunks(sender);								
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
					plugin.getServer().getScheduler().cancelTask(plugin.scheduledJob);
					sender.sendMessage(ChatColor.AQUA + "Plugin Disabled!");
					plugin.getLogger().info("Plugin disabled by " + sender.getName());
				} else {
					plugin.pluginEnable = true;
					plugin.setupJob();
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
