package com.alazeprt.abt.utils;

import com.alazeprt.abt.commands.PlayerCommandHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Common {
    public static YamlConfiguration config;
    public static YamlConfiguration message;
    public static YamlConfiguration data;
    public static final List<Group> groupList = new ArrayList<>();
    public static final List<Site> siteList = new ArrayList<>();
    public static final Map<Site, String> usingSiteMap = new HashMap<>();
    public static final List<TempSite> tempSiteList = new ArrayList<>();
    public static final Map<String, List<Location>> placedBlockMap = new HashMap<>();
    public static String getMessage(String node) {
        String origin = message.getString(node) == null ? "" : message.getString(node);
        return origin.replace("&", "§");
    }

    public static <T> T getConfiguration(String node, Class<T> type) {
        return type.cast(config.get(node));
    }

    public static Group getGroup(String group) {
        try {
            Integer.parseInt(group);
        } catch (Exception e) {
            for(Group g : groupList) {
                if(g.getName().equals(group)) {
                    return g;
                }
            }
            return null;
        }
        int groupIndex = Integer.parseInt(group);
        if(groupIndex < 1) return null;
        if(groupList.size() < groupIndex) return null;
        return groupList.get(groupIndex-1);
    }

    public static Site getSite(String site) {
        try {
            Integer.parseInt(site);
        } catch (Exception e) {
            for(Site s : siteList) {
                if(s.getName().equals(site)) {
                    return s;
                }
            }
            return null;
        }
        int siteIndex = Integer.parseInt(site);
        if(siteIndex < 1) return null;
        if(siteList.size() < siteIndex) return null;
        return siteList.get(siteIndex-1);
    }

    public static TempSite getTempSite(String site) {
        try {
            Integer.parseInt(site);
        } catch (Exception e) {
            for(TempSite s : tempSiteList) {
                if(s.getName().equals(site)) {
                    return s;
                }
            }
            return null;
        }
        int siteIndex = Integer.parseInt(site);
        if(siteIndex < 1) return null;
        if(tempSiteList.size() < siteIndex) return null;
        return tempSiteList.get(siteIndex-1);
    }

    public static void resetSite(Player player) {
        new PlayerCommandHandler(player).lobby();
        for(Map.Entry<Site, String> entry : usingSiteMap.entrySet()) {
            if(entry.getValue().equals(player.getName())) {
                usingSiteMap.remove(entry.getKey());
                resetSite(entry.getValue(), entry.getKey());
            }
        }
        player.sendMessage(getMessage("player.exit_success"));
    }

    private static void resetSite(String player, Site site) {
        for(Location location : placedBlockMap.get(player)) {
            Chunk chunk = site.getPos1().getWorld().getChunkAt(location);
            Block block = chunk.getBlock(getChunkBlockAt(location.getBlockX()), location.getBlockY(), getChunkBlockAt(location.getBlockZ()));
            block.setType(Material.AIR);
        }
        placedBlockMap.remove(player);
    }

    public static void resetAllSite() {
        for(Map.Entry<Site, String> entry : usingSiteMap.entrySet()) {
            resetSite(entry.getValue(), entry.getKey());
        }
    }

    private static int getChunkBlockAt(int pos) {
        int n = Math.abs(pos);
        return pos < 0 ? (16 - n % 16) % 16 : n % 16;
    }
}
