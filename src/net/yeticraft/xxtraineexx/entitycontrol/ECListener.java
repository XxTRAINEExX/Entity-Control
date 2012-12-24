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
		
	}

}
