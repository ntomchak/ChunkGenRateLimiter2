package io.github.ntomchak.chunkgenratelimiter;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RecheckTask extends BukkitRunnable{
  private ChunkCountTracker tracker;
  private Listeners listeners;
  private UUID uuid;

  public RecheckTask(ChunkCountTracker tracker, Listeners listeners, UUID uuid) {
    this.tracker = tracker;
    this.listeners = listeners;
    this.uuid = uuid;
  }
  
  public void run() {
    tracker.updatePlayer(uuid);
    if(tracker.getNumChunks(uuid) < ChunkCountTracker.getLimit()) {
      listeners.unrestrict(uuid);
    } else {
      Bukkit.getPlayer(uuid).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 120, 1));
      new RecheckTask(tracker, listeners, uuid).runTaskLater(ChunkGenRateLimiter.instance(), 20L * 120);
    }
  }

}
