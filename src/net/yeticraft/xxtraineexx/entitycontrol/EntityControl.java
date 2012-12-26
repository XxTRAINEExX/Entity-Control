package net.yeticraft.xxtraineexx.entitycontrol;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author XxTRAINEExX
 * This is the main class for the plugin. We initialize the listener, load the config, 
 * and set a few global variables.
 *
 */
public class EntityControl extends JavaPlugin {

	public FileConfiguration config;
	public ECListener myListener;
	public long deathBufferSeconds;
	public int entityCountPerChunk;
	public boolean pluginEnable;
	public boolean debug;
	public long cleanupTimer;
	public int scheduledJob;
	
	@Override
	public void onEnable() {
		myListener = new ECListener(this);
		loadMainConfig();
		CommandExecutor ECCommandExecutor = new ECCommand(this);
		getCommand("entitycontrol").setExecutor(ECCommandExecutor);
		getCommand("ec").setExecutor(ECCommandExecutor);
		setupJob();
	}

	/**
	 * Config loading method.
	 */
	public void loadMainConfig() {
		// Read the config file
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();

		// Assign all the local variables
		deathBufferSeconds = config.getLong("deathBufferSeconds");
		entityCountPerChunk = config.getInt("entityCountPerChunk");
		pluginEnable = config.getBoolean("pluginEnable");
		debug = config.getBoolean("debug");
		cleanupTimer = config.getLong("cleanupTimer");

		final Logger log = getLogger();
		log.info("Config loaded.");
		if (debug) {
			log.info("[deathBufferSeconds: " + deathBufferSeconds + "] ");
			log.info("[entityCountPerChunk: " + entityCountPerChunk + "] ");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
			log.info("[cleanupTimer: " + String.valueOf(cleanupTimer) + "]");
		}
	}

	/**
	 * Config saving method.
	 */
	public void saveMainConfig() {

		config.set("deathBufferSeconds", deathBufferSeconds);
		config.set("entityCountPerChunk", entityCountPerChunk);
		config.set("pluginEnable", pluginEnable);
		config.set("debug", debug);
		config.set("cleanupTimer", cleanupTimer);

		saveConfig();

		final Logger log = getLogger();
		log.info("Config saved.");
		if (debug) {
			log.info("[deathBufferSeconds: " + deathBufferSeconds + "] ");
			log.info("[entityCountPerChunk: " + entityCountPerChunk + "] ");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
			log.info("[cleanupTimer: " + String.valueOf(cleanupTimer) + "]");
		}
	}
	
	public void setupJob(){
		scheduledJob = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override  
		    public void run() {
		    	cleanChunks(Bukkit.getConsoleSender());
		    	// Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"ec go");
		    }
		}, cleanupTimer, cleanupTimer);
	}
	
	public void cleanChunks(CommandSender sender){
		// Check current playerDeaths MAP and remove any old entries
		Iterator<Map.Entry<String,Long>> iter = myListener.playerDeaths.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String,Long> entry = iter.next();
			long deathTime = entry.getValue();
			String deathChunk = entry.getKey();
			if((System.currentTimeMillis() - deathTime) > (deathBufferSeconds * 1000.0)){
				if (debug) {getLogger().info("Removed player death from: " + deathChunk + ". Player died [" + ((System.currentTimeMillis() - deathTime) / 1000.0) + "] seconds ago.");}
				iter.remove();
				continue;
			}
			if (debug) {getLogger().info("Death in: " + deathChunk + " kept. Player died [" + ((System.currentTimeMillis() - deathTime) / 1000.0) + "] seconds ago.");}
		}
	
		// Cycling through all worlds
		for (World world : getServer().getWorlds()) { 
		
			// Cycling through all loaded chunks in at particular world
			for (Chunk chunk : world.getLoadedChunks()) { 
			
				int clearedEntities = 0;
				int keptEntities = 0;
				String currentChunk = world.toString() + "-" + chunk.toString();
				Entity[] entityList = chunk.getEntities();
			
				// Doing some work if the entity count in that chunk is too high
				if (entityList.length < entityCountPerChunk) {
					continue;
				}
				if (myListener.playerDeaths.get(currentChunk) != null) {
					long deathInChunk = myListener.playerDeaths.get(currentChunk);
					if (debug){getLogger().info(currentChunk + 
							" has a death logged [" + ((System.currentTimeMillis() - deathInChunk)/1000.0) + "] seconds ago.  Skipping because it happened < " + deathBufferSeconds + " seconds ago.");}
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
					if (debug){getLogger().info("Chunk: " + world.toString() + "-" + chunk.toString() + 
							" Entity: " + entity.toString() + " is alive.  Skipping.");}
					keptEntities++;               
				}
				if (sender instanceof Player || debug){
				sender.sendMessage(ChatColor.DARK_AQUA +  "[EntityControl] " + world.toString() + " - " + chunk.toString() + ": Entities Cleared [" + clearedEntities + "]");
				sender.sendMessage(ChatColor.DARK_AQUA +  "[EntityControl] " + world.toString() + " - " + chunk.toString() + ": Entities Kept [" + keptEntities + "]");
				}
			}
		} 	
		if (sender instanceof Player || debug){
				sender.sendMessage(ChatColor.DARK_AQUA +  "[EntityControl] Chunk cleanup complete.");
		}
	}

}