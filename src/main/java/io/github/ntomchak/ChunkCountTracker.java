package io.github.ntomchak;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.bukkit.Bukkit;

public class ChunkCountTracker {

  private HashMap<UUID, Queue<Long>> chunks;

  private int chunksGenerated;
  private int nullChunks;

  public ChunkCountTracker() {
    this.chunks = new HashMap<UUID, Queue<Long>>();
  }

  /**
   * Removes times older than one minute from the chunks queue
   * 
   * @param uuid uuid of player in the hashmap
   * @return false if the player is not in the hashmap
   */
  public boolean updatePlayer(UUID uuid) {
    if (!chunks.containsKey(uuid)) {
      return false;
    }
    Queue<Long> q = chunks.get(uuid);
    while (q.peek() != null && System.currentTimeMillis() - q.peek() > 60000) {
      q.poll();
    }
    return true;
  }

  public Collection<Map.Entry<UUID, Queue<Long>>> getAllPlayers() {
    return chunks.entrySet();
  }

  public void updateAllPlayers() {
    Iterator<Map.Entry<UUID, Queue<Long>>> itr = chunks.entrySet().iterator();
    while (itr.hasNext()) {
      Map.Entry<UUID, Queue<Long>> entry = itr.next();
      UUID uuid = entry.getKey();
      updatePlayer(uuid);
      if (entry.getValue().size() == 0 && !Bukkit.getOfflinePlayer(uuid).isOnline()) {
        itr.remove();
      }
    }
  }

  public static int getLimit() {
    return 900;
  }

  public int size() {
    return chunks.size();
  }

  public boolean containsPlayer(UUID uuid) {
    return chunks.containsKey(uuid);
  }

  /**
   * Records that a player generated a chunk and updates that player's last-minute
   * history
   * 
   * @param uuid
   * @return true if they have passed the chunk generating rate limit
   */
  public boolean addChunk(UUID uuid) {
    boolean hasQ = updatePlayer(uuid);

    if (!hasQ) {
      chunks.put(uuid, new ArrayDeque<Long>());
    }

    Queue<Long> q = chunks.get(uuid);

    q.add(System.currentTimeMillis());

    int limit = getLimit();

    if (q.size() >= limit)
      return true;
    return false;
  }

  /**
   * Gets the number of chunks the specified player generated over the minute
   * before the last time that player was updated.
   * 
   * @param uuid player to check
   * @return number of chunks the player has generated in the past minute before
   *         the last time that player was updated
   */
  public int getNumChunks(UUID uuid) {
    Queue<Long> q = chunks.get(uuid);
    if (q == null)
      return 0;
    return q.size();
  }

  public int chunksGenerated() {
    return this.chunksGenerated;
  }

  public int nullChunks() {
    return this.nullChunks;
  }

}
