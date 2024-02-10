package com.alazeprt.afb.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Common {
    public static YamlConfiguration config;
    public static YamlConfiguration message;
    public static YamlConfiguration data;
    public static final List<Group> groupList = new ArrayList<>();
    public static final List<Site> siteList = new ArrayList<>();
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
        System.out.println(Arrays.toString(groupList.toArray()));
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
}
