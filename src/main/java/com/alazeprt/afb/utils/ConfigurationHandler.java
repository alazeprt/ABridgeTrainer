package com.alazeprt.afb.utils;

import com.alazeprt.afb.AFastBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.alazeprt.afb.utils.Common.*;

public class ConfigurationHandler {
    public static void load(File dataFolder) {
        File configFile = new File(dataFolder, "config.yml");
        File messageFile = new File(dataFolder, "message.yml");
        File dataFile = new File(dataFolder, "data.yml");
        File siteFolder = new File(dataFolder, "sites");
        if(!configFile.exists()) {
            AFastBuilder.getProvidingPlugin(AFastBuilder.class).saveResource("config.yml", false);
        }
        if(!messageFile.exists()) {
            AFastBuilder.getProvidingPlugin(AFastBuilder.class).saveResource("message.yml", false);
        }
        if(!dataFile.exists()) {
            AFastBuilder.getProvidingPlugin(AFastBuilder.class).saveResource("data.yml", false);
        }
        if(!siteFolder.exists()) {
            siteFolder.mkdirs();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        message = YamlConfiguration.loadConfiguration(messageFile);
        data = YamlConfiguration.loadConfiguration(dataFile);
        List<String> groupStringList = data.getStringList("groups");
        for(String groupString : groupStringList) {
            groupList.add(Group.toGroup(groupString));
        }
        try {
            Map<String, Object> siteMap = ((MemorySection) data.getValues(true).get("sites")).getValues(false);
            for(String siteName : siteMap.keySet()) {
                Site site = new Site(siteName, new Location(
                        Bukkit.getWorld(data.getString("sites." + siteName + "." + "pos1.world")),
                        data.getDouble("sites." + siteName + "." + "pos1.x"),
                        data.getDouble("sites." + siteName + "." + "pos1.y"),
                        data.getDouble("sites." + siteName + "." + "pos1.z")), new Location(
                        Bukkit.getWorld(data.getString("sites." + siteName + "." + "pos2.world")),
                        data.getDouble("sites." + siteName + "." + "pos2.x"),
                        data.getDouble("sites." + siteName + "." + "pos2.y"),
                        data.getDouble("sites." + siteName + "." + "pos2.z")
                ), new Location(
                        Bukkit.getWorld(data.getString("sites." + siteName + "." + "spawn.world")),
                        data.getDouble("sites." + siteName + "." + "spawn.x"),
                        data.getDouble("sites." + siteName + "." + "spawn.y"),
                        data.getDouble("sites." + siteName + "." + "spawn.z")), new Location(
                        Bukkit.getWorld(data.getString("sites." + siteName + "." + "endPos.world")),
                        data.getDouble("sites." + siteName + "." + "endPos.x"),
                        data.getDouble("sites." + siteName + "." + "endPos.y"),
                        data.getDouble("sites." + siteName + "." + "endPos.z")),
                        data.getString("sites." + siteName + "." + "displayname") == null ? siteName : data.getString("sites." + siteName + "." + "displayname"));
                if(!data.getString("sites." + siteName + "." + "group").equals("null")) {
                    site.setGroup(Group.toGroup(data.getString("sites." + siteName + "." + "group")));
                }
                siteList.add(site);
            }
        } catch (NullPointerException ignored) {}
    }

    public static void save(File dataFolder) {
        File configFile = new File(dataFolder, "config.yml");
        File messageFile = new File(dataFolder, "message.yml");
        File dataFile = new File(dataFolder, "data.yml");
        List<String> groupStringList = new ArrayList<>();
        for(Group group : groupList) {
            groupStringList.add(group.toString());
        }
        data.set("groups", groupStringList);
        for(Site site : siteList) {
            String prefix = "sites." + site.getName() + ".";
            data.set(prefix + "pos1.world", site.getPos1().getWorld().getName());
            data.set(prefix + "pos1.x", site.getPos1().getX());
            data.set(prefix + "pos1.y", site.getPos1().getY());
            data.set(prefix + "pos1.z", site.getPos1().getZ());
            data.set(prefix + "pos2.world", site.getPos2().getWorld().getName());
            data.set(prefix + "pos2.x", site.getPos2().getX());
            data.set(prefix + "pos2.y", site.getPos2().getY());
            data.set(prefix + "pos2.z", site.getPos2().getZ());
            data.set(prefix + "spawn.world", site.getSpawn().getWorld().getName());
            data.set(prefix + "spawn.x", site.getSpawn().getX());
            data.set(prefix + "spawn.y", site.getSpawn().getY());
            data.set(prefix + "spawn.z", site.getSpawn().getZ());
            data.set(prefix + "endPos.world", site.getEndPos().getWorld().getName());
            data.set(prefix + "endPos.x", site.getEndPos().getX());
            data.set(prefix + "endPos.y", site.getEndPos().getY());
            data.set(prefix + "endPos.z", site.getEndPos().getZ());
            data.set(prefix + "displayname", site.getDisplayName());
            if(site.getGroup() != null) {
                data.set(prefix + "group", site.getGroup().toString());
            } else {
                data.set(prefix + "group", "null");
            }
        }
        try {
            config.save(configFile);
            message.save(messageFile);
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        File configFile = new File(AFastBuilder.getProvidingPlugin(AFastBuilder.class).getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            AFastBuilder.getProvidingPlugin(AFastBuilder.class).saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void reloadMessage() {
        File messageFile = new File(AFastBuilder.getProvidingPlugin(AFastBuilder.class).getDataFolder(), "message.yml");
        if(!messageFile.exists()) {
            AFastBuilder.getProvidingPlugin(AFastBuilder.class).saveResource("message.yml", false);
        }
        message = YamlConfiguration.loadConfiguration(messageFile);
    }
}
