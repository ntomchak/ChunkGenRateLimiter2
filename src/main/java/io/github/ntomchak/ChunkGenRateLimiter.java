package io.github.ntomchak;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class ChunkGenRateLimiter extends JavaPlugin {

	private static ChunkGenRateLimiter instance;

	public void onEnable() {
	  instance = this;
	  final ChunkCountTracker playerData = new ChunkCountTracker();
	  Listeners listeners = new Listeners(playerData);
	  getServer().getPluginManager().registerEvents(listeners, this);
	  this.getCommand("antilag").setExecutor(new AntilagCommand(playerData));

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				playerData.updateAllPlayers();
			}
		}, 0L, 20L * 1200); // 20*300=5 minutes
	}
	
	public static ChunkGenRateLimiter instance() {
	  return instance;
	}

}
