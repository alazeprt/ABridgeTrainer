package com.alazeprt.abt.utils;

import com.alazeprt.abt.commands.PlayerCommandHandler;
import com.alazeprt.abt.events.StopwatchEventHandler;
import org.apache.commons.lang.time.StopWatch;
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
    public static final List<TempSite> tempSiteList = new ArrayList<>();
    public static final List<Point<Site, String, StopWatch>> usingSiteList = new ArrayList<>();
    public static final Map<String, List<Location>> placedBlockMap = new HashMap<>();
    public static String getMessage(String node) {
        String origin = message.getString(node) == null ? "" : message.getString(node);
        return origin.replace("&", "§");
    }

    public static List<String> getMessages(String node) {
        List<String> origin = message.getStringList(node);
        List<String> result = new ArrayList<>();
        origin.forEach(s -> result.add(s.replace("&", "§")));
        return result;
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

    public static void resetSite(Player player, boolean exit) {
        Iterator<Point<Site, String, StopWatch>> iterator = usingSiteList.iterator();
        while(iterator.hasNext()) {
            Point<Site, String, StopWatch> entry = iterator.next();
            if(entry.getValue1().equals(player.getName())) {
                if(exit) {
                    iterator.remove();
                }
                resetSite(entry.getValue1(), entry.getKey(), exit);
                StopwatchEventHandler.stop(entry.getValue1());
            }
        }
    }

    private static void resetSite(String player, Site site, boolean exit) {
        for(Location location : placedBlockMap.get(player)) {
            Chunk chunk = site.getPos1().getWorld().getChunkAt(location);
            Block block = chunk.getBlock(getChunkBlockAt(location.getBlockX()), location.getBlockY(), getChunkBlockAt(location.getBlockZ()));
            block.setType(Material.AIR);
        }
        if(exit) {
            placedBlockMap.remove(player);
        }
    }

    public static void resetAllSite(boolean exit) {
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            resetSite(entry.getValue1(), entry.getKey(), exit);
            StopwatchEventHandler.stop(entry.getValue1());
        }
    }

    private static int getChunkBlockAt(int pos) {
        int n = Math.abs(pos);
        return pos < 0 ? (16 - n % 16) % 16 : n % 16;
    }
}
