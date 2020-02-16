package io.github.ntomchak;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class Listeners implements Listener {
  private ChunkCountTracker chunkTracker;
  private HashSet<UUID> restricted;
  float slowFly = 0.045f;
  float normalFly = 0.075f;

  public Listeners(ChunkCountTracker chunkTracker) {
    this.chunkTracker = chunkTracker;
    this.restricted = new HashSet<UUID>();
  }

  @EventHandler
  public void onChunkPopulate(ChunkPopulateEvent e) {
    Player p = Utils.nearestPlayer(e.getChunk());

    if (p != null) {
      UUID uuid = p.getUniqueId();
      boolean passedLimit = chunkTracker.addChunk(uuid);
      if (passedLimit) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 120, 1));
        if (p.isInsideVehicle()) {
          p.leaveVehicle();
        } else if (p.isGliding()) {
          p.setGliding(false);
        }
        restricted.add(uuid);
        p.sendMessage(
            ChatColor.DARK_RED + "You have generated more than " + ChatColor.RED + ChunkCountTracker.getLimit()
                + ChatColor.DARK_RED + " chunks in the past minute. Please slow down this rate.");
        new RecheckTask(chunkTracker, this, uuid).runTaskLater(ChunkGenRateLimiter.instance(), 20L * 120);
      }
    }
  }

  @EventHandler
  public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
    Player p = e.getPlayer();
    UUID uuid = p.getUniqueId();
    if (restricted.contains(uuid)) {
      p.setFlySpeed(slowFly);
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    if (e.getMaterial().equals(Material.ENDER_PEARL)) {
      Player p = e.getPlayer();
      UUID uuid = p.getUniqueId();
      if (restricted.contains(uuid)) {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onEntityToggleGlide(EntityToggleGlideEvent e) {
    if (e.getEntity() instanceof Player) {
      if (e.isGliding()) {
        Player p = (Player) e.getEntity();
        UUID uuid = p.getUniqueId();
        if (restricted.contains(uuid)) {
          e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onVehicleEnter(VehicleEnterEvent e) {
    if (e.getEntered() instanceof Player) {
      if (e.getVehicle() instanceof AbstractHorse || e.getVehicle() instanceof Boat) {
        Player p = (Player) e.getEntered();
        UUID uuid = p.getUniqueId();
        if (restricted.contains(uuid))
          e.setCancelled(true);
      }
    }
  }

  public void unrestrict(UUID uuid) {
    restricted.remove(uuid);
    Bukkit.getPlayer(uuid).setFlySpeed(normalFly);
  }
}
