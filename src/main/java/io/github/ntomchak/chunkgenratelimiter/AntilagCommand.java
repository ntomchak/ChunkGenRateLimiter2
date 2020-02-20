package io.github.ntomchak.chunkgenratelimiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AntilagCommand implements CommandExecutor {
  private ChunkCountTracker chunkTracker;

  public AntilagCommand(ChunkCountTracker chunkTracker) {
    this.chunkTracker = chunkTracker;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("antilag")) {
      if (sender.hasPermission("tmcantilag.admin")) {
        if (args.length == 0) {
          // help menu
          return true;
        } else if (args[0].equalsIgnoreCase("top")) {
          topCommand(sender);
          return true;
        } else if (args[0].equalsIgnoreCase("self")) {
          selfCommand(sender);
          return true;
        }
      } else {
        sender.sendMessage("You don't have permission to do that.");
      }
    }
    return true;
  }

  private void topCommand(CommandSender sender) {
    chunkTracker.updateAllPlayers();

    ArrayList<Map.Entry<UUID, Queue<Long>>> qs = new ArrayList<Map.Entry<UUID, Queue<Long>>>(
        chunkTracker.getAllPlayers());

    Collections.sort(qs, new Comparator<Map.Entry<UUID, Queue<Long>>>() {
      public int compare(Map.Entry<UUID, Queue<Long>> q1, Map.Entry<UUID, Queue<Long>> q2) {
        return q2.getValue().size() - q1.getValue().size();
      }
    });
    sender.sendMessage("Players in chunks hashmap: " + qs.size());
    for (int i = 0; i < 10 && i < qs.size(); i++) {
      Map.Entry<UUID, Queue<Long>> entry = qs.get(i);
      sender.sendMessage(Bukkit.getOfflinePlayer(entry.getKey()).getName() + " " + entry.getValue().size());
    }
  }

  private void selfCommand(CommandSender sender) {
    if (sender instanceof ConsoleCommandSender) {
      sender.sendMessage("Only players can use this command.");
    } else {
      Player p = (Player) sender;
      UUID uuid = p.getUniqueId();
      chunkTracker.updatePlayer(uuid);
      if (chunkTracker.containsPlayer(uuid)) {
        p.sendMessage("You're in the chunks hashmap and the queue is size " + chunkTracker.getNumChunks(uuid));
      } else {
        p.sendMessage("You're not in the chunks hashmap");
      }
    }
  }
}
