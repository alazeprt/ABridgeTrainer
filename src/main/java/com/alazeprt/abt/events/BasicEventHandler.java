package com.alazeprt.abt.events;

import com.alazeprt.abt.ABridgeTrainer;
import com.alazeprt.abt.commands.ErrorCommandHandler;
import com.alazeprt.abt.commands.PlayerCommandHandler;
import com.alazeprt.abt.utils.Common;
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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alazeprt.abt.utils.Common.*;

public class BasicEventHandler implements Listener {
    public static void register() {
        Bukkit.getPluginManager().registerEvents(new BasicEventHandler(),
                ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class));
        Bukkit.getScheduler().runTaskTimer(ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class), new killPlayerRunnable(), 0, 2);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(notClean.contains(event.getPlayer().getName())) {
            event.getPlayer().getInventory().clear();
            notClean.remove(event.getPlayer().getName());
        }
        event.getPlayer().sendMessage(getMessage("player.auto_teleport_to_lobby"));
        new PlayerCommandHandler(event.getPlayer()).lobby();
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AtomicBoolean contains = new AtomicBoolean(false);
        usingSiteList.forEach(s -> {
            if(s.getValue1().equals(player.getName())) {
                contains.set(true);
            }
        });
        if(contains.get()) {
            Common.resetSite(player, true);
        }
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
    public void onDrop(PlayerDropItemEvent event) {
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(entry.getValue1().equalsIgnoreCase(event.getPlayer().getName())) {
                List<Object> blocks = getConfiguration("siteBlocks", List.class);
                for(int i = 0; i < blocks.size(); i++) {
                    try {
                        Material material = Material.valueOf(blocks.get(i).toString());
                        event.getPlayer().getInventory().setItem(i, new ItemStack(material));
                    } catch (Exception ignored) {}
                }
            }
        }
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
                                if(newList.isEmpty()) {
                                    StopwatchEventHandler.start(event.getPlayer().getName());
                                }
                                newList.add(event.getBlock().getLocation());
                                placedBlockMap.put(event.getPlayer().getName(), newList);
                                List<Object> blocks = getConfiguration("siteBlocks", List.class);
                                for(int i = 0; i < blocks.size(); i++) {
                                    try {
                                        Material material = Material.valueOf(blocks.get(i).toString());
                                        event.getPlayer().getInventory().setItem(i, new ItemStack(material));
                                    } catch (Exception ignored) {}
                                }
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

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(!entry.getValue1().equals(event.getPlayer().getName())) {
                continue;
            }
            Site site = entry.getKey();
            if(Math.abs(event.getPlayer().getLocation().getBlockX() - site.getEndPos().getX()) < 1 &&
            Math.abs(event.getPlayer().getLocation().getBlockY()) - site.getEndPos().getY() < 1.5 &&
            Math.abs(event.getPlayer().getLocation().getBlockZ()) - site.getEndPos().getZ() < 1) {
                double time = StopwatchEventHandler.stop(event.getPlayer().getName());
                String formattedTime = getConfiguration("stopwatch.time_format", String.class)
                        .replace("hh", String.valueOf(((int) time)/1000/60/60))
                        .replace("mm", String.format("%02d", ((int) time) / 1000 / 60 % 60))
                        .replace("ss", String.format("%02d", ((int) time) / 1000 % 60))
                        .replace("ms", String.valueOf(time%1000));
                event.getPlayer().sendMessage(getMessage("player.finished").replace("%time%", formattedTime));
                Common.resetSite(event.getPlayer(), false);
                event.getPlayer().teleport(site.getSpawn());
                placedBlockMap.clear();
            }
        }
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
