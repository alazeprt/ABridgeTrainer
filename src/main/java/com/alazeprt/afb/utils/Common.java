package com.alazeprt.afb.utils;

import com.alazeprt.afb.AFastBuilder;
import com.alazeprt.afb.commands.ErrorCommandHandler;
import com.alazeprt.afb.commands.PlayerCommandHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Common {
    public static YamlConfiguration config;
    public static YamlConfiguration message;
    public static YamlConfiguration data;
    public static final List<Group> groupList = new ArrayList<>();
    public static final List<Site> siteList = new ArrayList<>();
    public static final Map<Site, String> usingSiteMap = new HashMap<>();
    public static final List<TempSite> tempSiteList = new ArrayList<>();
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
                resetSite(entry.getKey());
            }
        }
        player.sendMessage(getMessage("player.exit_success"));
    }

    private static void resetSite(Site site) {
        Location pos1 = site.getPos1();
        Location pos2 = site.getPos2();
        World world = pos1.getWorld();
        File siteFile = new File(AFastBuilder.getProvidingPlugin(AFastBuilder.class).getDataFolder(), "sites/" + site.getName() + ".yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(siteFile);
        List<String> list = configuration.getStringList("blocks");
        if(getConfiguration("clearMethod", String.class).equals("AIR_PLACE")) {
            List<Chunk> chunks = new ArrayList<>();
            for(int i = Math.min(pos1.getBlockX(), pos2.getBlockX()); i <= Math.max(pos1.getBlockX(), pos2.getBlockX()); i++) {
                for(int j = Math.min(pos1.getBlockY(), pos2.getBlockY()); j <= Math.max(pos1.getBlockY(), pos2.getBlockY()); j++) {
                    for(int k = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); k <= Math.max(pos1.getBlockZ(), pos2.getBlockZ()); k++) {
                        Chunk chunk = world.getChunkAt(i, k);
                        world.loadChunk(chunk);
                        Block block = world.getBlockAt(i, j, k);
                        block.setType(Material.AIR);
                        if(!chunks.contains(chunk)) {
                            chunks.add(chunk);
                        }
                    }
                }
            }
            for(Chunk c : chunks) {
                c.unload(true);
            }
        }
        List<Chunk> chunks = new ArrayList<>();
        for(String s : list) {
            String[] splitArray = s.split(";");
            Location blockLocation = new Location(world, Integer.parseInt(splitArray[0]), Integer.parseInt(splitArray[1]), Integer.parseInt(splitArray[2]));
            Chunk chunk = world.getChunkAt(blockLocation);
            world.loadChunk(chunk);
            Block block = blockLocation.getBlock();
            block.setType(Material.valueOf(splitArray[3]));
            if(!chunks.contains(chunk)) {
                chunks.add(chunk);
            }
        }
        for(Chunk c : chunks) {
            c.unload(true);
        }
    }

    public static void writeSiteToFile(CommandSender sender, Site site) {
        Thread thread = new Thread(() -> {
            sender.sendMessage(getMessage("admin.saving_site_data"));
            Location pos1 = site.getPos1();
            Location pos2 = site.getPos2();
            World world = pos1.getWorld();
            File siteFile = new File(AFastBuilder.getProvidingPlugin(AFastBuilder.class).getDataFolder(), "sites/" + site.getName() + ".yml");
            if(!siteFile.exists()) {
                try {
                    siteFile.createNewFile();
                } catch (IOException e) {
                    new ErrorCommandHandler(sender).operation("create site file", e);
                    return;
                }
            }
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(siteFile);
            configuration.set("world", world.getName());
            List<String> list = new ArrayList<>();
            for(int i = Math.min(pos1.getBlockX(), pos2.getBlockX()); i <= Math.max(pos1.getBlockX(), pos2.getBlockX()); i++) {
                for(int j = Math.min(pos1.getBlockY(), pos2.getBlockY()); j <= Math.max(pos1.getBlockY(), pos2.getBlockY()); j++) {
                    for(int k = Math.min(pos1.getBlockZ(), pos2.getBlockZ()); k <= Math.max(pos1.getBlockZ(), pos2.getBlockZ()); k++) {
                        Chunk chunk = world.getChunkAt(i, k);
                        Block block = chunk.getBlock(getChunkBlockAt(i), j, getChunkBlockAt(k));
                        if(block.getType().equals(Material.AIR) && getConfiguration("clearMethod", String.class).equals("AIR_PLACE")) {
                            continue;
                        }
                        list.add(i + ";" + j + ";" + k + ";" + block.getType().toString());
                    }
                }
            }
            configuration.set("blocks", list);
            try {
                configuration.save(siteFile);
            } catch (Exception e) {
                new ErrorCommandHandler(sender).operation("save site data", e);
            } finally {
                sender.sendMessage(getMessage("operation_successful"));
            }
        });
        thread.start();
    }

    private static int getChunkBlockAt(int pos) {
        return pos < 0 ? 16-Math.abs(pos)%16 : Math.abs(pos)%16;
    }
}
