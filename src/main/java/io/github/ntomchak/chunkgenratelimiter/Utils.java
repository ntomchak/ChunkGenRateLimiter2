package io.github.ntomchak.chunkgenratelimiter;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Utils {
  
  public static Player nearestPlayer(Chunk chunk) {
    int chunkX = chunk.getX() * 16;
    int chunkZ = chunk.getZ() * 16;

    Player nearest = null;
    long min = Integer.MAX_VALUE - 1;

    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getWorld().equals(chunk.getWorld())) {
        Location l = p.getLocation();
        long dist = distanceSquared(l.getBlockX(), l.getBlockZ(), chunkX, chunkZ);
        if (dist < min) {
          nearest = p;
          min = dist;
        }
      }
    }
    return nearest;
  }

  private static long distanceSquared(long x1, long y1, long x2, long y2) {
    long x = x1 - x2;
    long y = y1 - y2;
    return (x * x) + (y * y);
  }
}
