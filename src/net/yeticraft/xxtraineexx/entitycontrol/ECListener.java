package net.yeticraft.xxtraineexx.entitycontrol;

import java.util.*;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ECListener implements Listener {

	private final EntityControl plugin;
	Map<String, Long> playerDeaths = new HashMap<String, Long>();
	
	public ECListener(EntityControl plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {

		if (!plugin.pluginEnable) {
			return; // Plugin has been manually disabled
		}
		
		Chunk chunk = e.getEntity().getLocation().getChunk();
		playerDeaths.put(chunk.toString(), System.currentTimeMillis());
		
		Iterator<Map.Entry<String,Long>> iter = playerDeaths.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String,Long> entry = iter.next();
		    if((entry.getValue() - System.currentTimeMillis()) > 300000){
		    	
		    	if (plugin.debug) {
					plugin.getLogger().info("Removed death entry from: " + entry.getKey());
					plugin.getLogger().info("Entry was [" + (entry.getValue() / 1000) + "] seconds old.");
				}
		    	iter.remove();
		    }
		}
	}

}
