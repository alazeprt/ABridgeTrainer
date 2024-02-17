package com.alazeprt.abt.commands.tab;

import com.alazeprt.abt.ABridgeTrainer;
import com.alazeprt.abt.utils.Site;
import com.alazeprt.abt.utils.TempSite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

import static com.alazeprt.abt.utils.Common.*;

public class BasicTabCompleter implements TabCompleter {
    public static void register() {
        ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class).getCommand("abt").setTabCompleter(new BasicTabCompleter());
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("abt.player")) return new ArrayList<>();
        switch (strings.length) {
            case 1:
                if(commandSender.hasPermission("abt.admin")) {
                    return List.of("setLobby", "createGroup", "removeGroup", "listGroup", "createSite",
                            "editSite", "removeSite", "listSite", "setPos1", "setPos2", "setSpawn", "setEndPos",
                            "saveSite", "throwSite", "reload", "lobby", "join", "exit", "spawn", "help");
                } else {
                    return List.of("lobby", "join", "exit", "spawn", "help");
                }
            case 2:
                if(commandSender.hasPermission("abt.admin")) {
                    if(strings[0].equalsIgnoreCase("listGroup")) {
                        List<String> list = new ArrayList<>();
                        int contentNum = groupList.size();
                        int contentPerPage = getConfiguration("contentPerPage", Integer.class);
                        for(int i = 1; i <= (contentNum / contentPerPage + 1); i++) {
                            list.add(String.valueOf(i));
                        }
                        return list;
                    } else if(strings[0].equalsIgnoreCase("listSite")) {
                        List<String> list = new ArrayList<>();
                        int contentNum = siteList.size();
                        int contentPerPage = getConfiguration("contentPerPage", Integer.class);
                        for(int i = 1; i <= (contentNum / contentPerPage + 1); i++) {
                            list.add(String.valueOf(i));
                        }
                        return list;
                    } else if(strings[0].equalsIgnoreCase("editSite")) {
                        List<String> list = new ArrayList<>();
                        for(Site site : siteList) {
                            list.add(site.getName());
                        }
                        return list;
                    } else if(strings[0].equalsIgnoreCase("setPos1") ||
                            strings[0].equalsIgnoreCase("setPos2") ||
                    strings[0].equalsIgnoreCase("setSpawn") ||
                    strings[0].equalsIgnoreCase("setEndPos") ||
                    strings[0].equalsIgnoreCase("saveSite") ||
                    strings[0].equalsIgnoreCase("throwSite")) {
                        List<String> list = new ArrayList<>();
                        for(TempSite site : tempSiteList) {
                            list.add(site.getName());
                        }
                        return list;
                    } else if(strings[0].equalsIgnoreCase("reload")) {
                        return List.of("config", "message");
                    }
                } else {
                    if(strings[0].equalsIgnoreCase("join")) {
                        List<String> list = new ArrayList<>();
                        siteList.forEach(site -> list.add(site.getName()));
                        groupList.forEach(group -> list.add(group.getName()));
                        return list;
                    }
                }
            case 3:
                if(commandSender.hasPermission("abt.admin") && strings[0].equalsIgnoreCase("createSite")) {
                    List<String> list = new ArrayList<>();
                    groupList.forEach(group -> list.add(group.getName()));
                    return list;
                }
            default:
                return new ArrayList<>();
        }
    }
}
