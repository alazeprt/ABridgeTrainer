package com.alazeprt.afb.events;

import com.alazeprt.afb.AFastBuilder;
import com.alazeprt.afb.commands.ErrorCommandHandler;
import com.alazeprt.afb.commands.PlayerCommandHandler;
import com.alazeprt.afb.utils.Common;
import com.alazeprt.afb.utils.Site;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

import static com.alazeprt.afb.utils.Common.*;

public class BasicEventHandler implements Listener {
    public static void register() {
        Bukkit.getPluginManager().registerEvents(new BasicEventHandler(),
                AFastBuilder.getProvidingPlugin(AFastBuilder.class));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(getMessage("player.auto_teleport_to_lobby"));
        new PlayerCommandHandler(event.getPlayer()).lobby();
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(usingSiteMap.containsValue(player.getName())) {
            Common.resetSite(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.getPlayer().getLocation().getY() <= config.getDouble("deathYPosition")) {
            event.getPlayer().setFallDistance(666);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(event.getPlayer().hasPermission("afb.admin")) {
            return;
        }
        for(Map.Entry<Site, String> entry : usingSiteMap.entrySet()) {
            if(!entry.getValue().equals(event.getPlayer().getName())) {
                continue;
            }
            Site site = entry.getKey();
            if((site.getPos1().getX() >= event.getBlockPlaced().getX() && event.getBlockPlaced().getX() >= site.getPos2().getX()) ||
                    (site.getPos1().getX() <= event.getBlockPlaced().getX() && event.getBlockPlaced().getX() <= site.getPos2().getX())) {
                if((site.getPos1().getZ() >= event.getBlockPlaced().getZ() && event.getBlockPlaced().getZ() >= site.getPos2().getZ()) ||
                        (site.getPos1().getZ() <= event.getBlockPlaced().getZ() && event.getBlockPlaced().getZ() <= site.getPos2().getZ())) {
                    return;
                }
            }
        }
        event.setCancelled(true);
        new ErrorCommandHandler(event.getPlayer()).notAllowed();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(event.getPlayer().hasPermission("afb.admin")) {
            return;
        }
        for(Map.Entry<Site, String> entry : usingSiteMap.entrySet()) {
            if(!entry.getValue().equals(event.getPlayer().getName())) {
                continue;
            }
            Site site = entry.getKey();
            if((site.getPos1().getX() >= event.getBlock().getX() && event.getBlock().getX() >= site.getPos2().getX()) ||
                    (site.getPos1().getX() <= event.getBlock().getX() && event.getBlock().getX() <= site.getPos2().getX())) {
                if((site.getPos1().getZ() >= event.getBlock().getZ() && event.getBlock().getZ() >= site.getPos2().getZ()) ||
                        (site.getPos1().getZ() <= event.getBlock().getZ() && event.getBlock().getZ() <= site.getPos2().getZ())) {
                    if(event.getBlock().getType().equals(Material.SANDSTONE)) {
                        return;
                    }
                }
            }
        }
        event.setCancelled(true);
        new ErrorCommandHandler(event.getPlayer()).notAllowed();
    }
}
