package com.alazeprt.afb;

import com.alazeprt.afb.commands.AdminCommandHandler;
import com.alazeprt.afb.commands.ErrorCommandHandler;
import com.alazeprt.afb.commands.PlayerCommandHandler;
import com.alazeprt.afb.utils.Group;
import com.alazeprt.afb.utils.Site;
import com.alazeprt.afb.utils.TempSite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
afastbuilder - adminCommand alias
setLobby [x, y, z] ✔
createGroup <name> [displayname] ✔
removeGroup <name/index> ✔
listGroup [page] ✔
createSite <name> [displayname] [group] // new TempSite() ✔
editSite <name/index>    // Site -> TempSite ✔
removeSite <name/index> // remove(Site) ✔
listSite [page] // list(Site) ✔
setPos1 <tempsite> ✔
setPos2 <tempsite> ✔
setSpawn <tempsite> ✔
setEndPos <tempsite> ✔
saveSite <tempsite> // remove(TempSite) && new Site() ✔
throwSite <tempsite> // remove(TempSite) ✔
reload [config/message]

afastbuilder - playerCommand alias
spawn  (able to no prefix)
lobby ✔
join [site/group]
exit
help

afastbuilder - events
join -> -> lobby
place -> ?
break -> ?
move -> y=? -> die
 */

public class AFastBuilder extends JavaPlugin implements CommandExecutor, Listener {
    public static YamlConfiguration config;
    public static YamlConfiguration message;
    public static YamlConfiguration data;
    public static final List<Group> groupList = new ArrayList<>();
    public static final List<Site> siteList = new ArrayList<>();
    public static final List<TempSite> tempSiteList = new ArrayList<>();
    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        File messageFile = new File(dataFolder, "message.yml");
        File dataFile = new File(dataFolder, "data.yml");
        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }
        if(!messageFile.exists()) {
            saveResource("message.yml", false);
        }
        if(!dataFile.exists()) {
            saveResource("data.yml", false);
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
            System.out.println(Arrays.toString(siteMap.keySet().toArray()) + "   " + Arrays.toString(siteMap.values().toArray()));
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

    @Override
    public void onDisable() {
        File dataFolder = getDataFolder();
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ErrorCommandHandler errorCommandHandler = new ErrorCommandHandler(sender);
        if(!sender.hasPermission("afb.player")) {
            errorCommandHandler.permission();
            return false;
        }
        PlayerCommandHandler playerCommandHandler = new PlayerCommandHandler(sender);
        AdminCommandHandler adminCommandHandler = new AdminCommandHandler(sender);
        switch (args.length) {
            case 0:
                if(sender.hasPermission("afb.admin")) {
                    adminCommandHandler.help();
                } else {
                    playerCommandHandler.help();
                }
                break;
            case 1:
                if(args[0].equals("help")) {
                    if(sender.hasPermission("afb.admin")) {
                        adminCommandHandler.help();
                    } else {
                        playerCommandHandler.help();
                    }
                }
                // admin commands
                if(args[0].equals("setLobby")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.lobby();
                    }
                }
                if(args[0].equals("listGroup")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listGroup("1");
                    }
                }
                if(args[0].equals("listSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listSite("1");
                    }
                }
                // player commands
                if(args[0].equals("lobby")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.lobby();
                    }
                }
                break;
            case 2:
                if(args[0].equals("createGroup")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createGroup(args[1], args[1]);
                    }
                }
                if(args[0].equals("removeGroup")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.removeGroup(args[1]);
                    }
                }
                if(args[0].equals("listGroup")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listGroup(args[1]);
                    }
                }
                if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createSite(new TempSite(args[1]));
                    }
                }
                if(args[0].equals("editSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.editSite(args[1]);
                    }
                }
                if(args[0].equals("removeSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.removeSite(args[1]);
                    }
                }
                if(args[0].equals("listSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listSite(args[1]);
                    }
                }
                if(args[0].equals("setPos1")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setPos1(args[1]);
                    }
                }
                if(args[0].equals("setPos2")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setPos2(args[1]);
                    }
                }
                if(args[0].equals("setSpawn")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setSpawn(args[1]);
                    }
                }
                if(args[0].equals("setEndPos")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setEndPos(args[1]);
                    }
                }
                if(args[0].equals("saveSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.saveSite(args[1]);
                    }
                }
                if(args[0].equals("throwSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.throwSite(args[1]);
                    }
                }
                break;
            case 3:
                if(args[0].equals("createGroup")) { // TODO: default: displayname -> args[2] -> args[end-1]
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createGroup(args[1], args[2]);
                    }
                }
                if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createSite(new TempSite(args[1], args[2]));
                    }
                }
            case 4:
                if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("afb.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        if(getGroup(args[3]) == null) {
                            errorCommandHandler.operation("find group", new Throwable("Group not found"));
                            return false;
                        } else {
                            adminCommandHandler.createSite(new TempSite(args[1], args[2], getGroup(args[3])));
                        }
                    }
                }
        }
        return false;
    }

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
