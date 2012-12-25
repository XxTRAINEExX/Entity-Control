package net.yeticraft.xxtraineexx.entitycontrol;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
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

		final Logger log = getLogger();
		log.info("Config loaded.");
		if (debug) {
			log.info("[deathBufferSeconds: " + deathBufferSeconds + "] ");
			log.info("[entityCountPerChunk: " + entityCountPerChunk + "] ");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
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

		saveConfig();

		final Logger log = getLogger();
		log.info("Config saved.");
		if (debug) {
			log.info("[deathBufferSeconds: " + deathBufferSeconds + "] ");
			log.info("[entityCountPerChunk: " + entityCountPerChunk + "] ");
			log.info("[pluginEnable: " + String.valueOf(pluginEnable) + "]");
			log.info("[debug: " + String.valueOf(debug) + "]");
		}
	}
	
	public void setupJob(){
		scheduledJob = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override  
		    public void run() {
		    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"ec go");
		    }
		}, 60L, 200L);
	}
}
