package com.alazeprt.abt.events;

import com.alazeprt.abt.ABridgeTrainer;
import com.alazeprt.abt.commands.ErrorCommandHandler;
import com.alazeprt.abt.commands.PlayerCommandHandler;
import com.alazeprt.abt.utils.Point;
import com.alazeprt.abt.utils.Site;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

import static com.alazeprt.abt.utils.Common.*;

public class BasicEventHandler implements Listener {
    public static void register() {
        Bukkit.getPluginManager().registerEvents(new BasicEventHandler(),
                ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class));
        Bukkit.getScheduler().runTaskTimer(ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class), new killPlayerRunnable(), 0, 2);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(getMessage("player.auto_teleport_to_lobby"));
        new PlayerCommandHandler(event.getPlayer()).lobby();
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        usingSiteList.removeIf(s -> s.getValue1().equalsIgnoreCase(player.getName()));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        System.out.println(1);
        Player player = event.getPlayer();
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(!entry.getValue1().equals(player.getName())) {
                continue;
            }
            Site site = entry.getKey();
            event.setRespawnLocation(site.getSpawn());
            return;
        }
        player.sendMessage(getMessage("player.teleport_success"));
        player.sendMessage(getMessage("player.auto_teleport_to_lobby"));
        if(data.get("lobby") == null) {
            new ErrorCommandHandler(player).lobbyNotSet();
            return;
        }
        Location lobby = new Location(Bukkit.getWorld(data.getString("lobby.world")), data.getDouble("lobby.x"),
                data.getDouble("lobby.y"), data.getDouble("lobby.z"));
        event.setRespawnLocation(lobby);
        player.sendMessage(getMessage("player.teleport_success"));
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission("abt.admin")) {
            return;
        }
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(!entry.getValue1().equals(event.getPlayer().getName())) {
                continue;
            }
            Site site = entry.getKey();
            if((site.getPos1().getX() >= event.getBlockPlaced().getX() && event.getBlockPlaced().getX() >= site.getPos2().getX()) ||
                    (site.getPos1().getX() <= event.getBlockPlaced().getX() && event.getBlockPlaced().getX() <= site.getPos2().getX())) {
                if((site.getPos1().getZ() >= event.getBlockPlaced().getZ() && event.getBlockPlaced().getZ() >= site.getPos2().getZ()) ||
                        (site.getPos1().getZ() <= event.getBlockPlaced().getZ() && event.getBlockPlaced().getZ() <= site.getPos2().getZ())) {
                    if((site.getPos1().getY() >= event.getBlock().getY() && event.getBlock().getY() > site.getPos2().getY()) ||
                            (site.getPos1().getY() <= event.getBlock().getY() && event.getBlock().getY() < site.getPos2().getY())) {
                        for(Object object : getConfiguration("siteBlocks", List.class)) {
                            try {
                                Material.valueOf(object.toString().toUpperCase());
                            } catch (Exception e) {
                                continue;
                            }
                            if(event.getBlock().getType().equals(Material.valueOf(object.toString().toUpperCase()))) {
                                List<Location> newList = placedBlockMap.get(event.getPlayer().getName());
                                newList.add(event.getBlock().getLocation());
                                placedBlockMap.put(event.getPlayer().getName(), newList);
                                return;
                            }
                        }
                    }
                }
            }
        }
        event.setCancelled(true);
        new ErrorCommandHandler(event.getPlayer()).notAllowed();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission("abt.admin")) {
            return;
        }
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(!entry.getValue1().equals(event.getPlayer().getName())) {
                continue;
            }
            Site site = entry.getKey();
            if((site.getPos1().getX() >= event.getBlock().getX() && event.getBlock().getX() >= site.getPos2().getX()) ||
                    (site.getPos1().getX() <= event.getBlock().getX() && event.getBlock().getX() <= site.getPos2().getX())) {
                if((site.getPos1().getZ() >= event.getBlock().getZ() && event.getBlock().getZ() >= site.getPos2().getZ()) ||
                        (site.getPos1().getZ() <= event.getBlock().getZ() && event.getBlock().getZ() <= site.getPos2().getZ())) {
                    if((site.getPos1().getY() >= event.getBlock().getY() && event.getBlock().getY() > site.getPos2().getY()) ||
                            (site.getPos1().getY() <= event.getBlock().getY() && event.getBlock().getY() < site.getPos2().getY())) {
                        for(Location location : placedBlockMap.get(event.getPlayer().getName())) {
                            if(event.getBlock().getLocation().equals(location)) {
                                List<Location> newList = placedBlockMap.get(event.getPlayer().getName());
                                newList.remove(event.getBlock().getLocation());
                                placedBlockMap.put(event.getPlayer().getName(), newList);
                                return;
                            }
                        }
                    }
                }
            }
        }
        event.setCancelled(true);
        new ErrorCommandHandler(event.getPlayer()).notAllowed();
    }

    static class killPlayerRunnable implements Runnable {
        @Override
        public void run() {
            usingSiteList.forEach(s -> {
                Player player = Bukkit.getPlayer(s.getValue1());
                if(player != null && player.getLocation().getY() < getConfiguration("deathYPosition", Integer.class)) {
                    player.damage(114514);
                }
            });
        }
    }
}
