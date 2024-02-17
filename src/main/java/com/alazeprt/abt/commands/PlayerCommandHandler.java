package com.alazeprt.abt.commands;

import com.alazeprt.abt.utils.Common;
import com.alazeprt.abt.utils.Group;
import com.alazeprt.abt.utils.Site;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

import static com.alazeprt.abt.utils.Common.*;

public class PlayerCommandHandler {
    private final CommandSender sender;
    public PlayerCommandHandler(CommandSender sender) {
        this.sender = sender;
    }
    public void help() {
        getMessages("player.help").forEach(sender::sendMessage);
    }

    public void lobby() {
        Player player = (Player) sender;
        if(data.get("lobby") == null) {
            player.sendMessage(getMessage("error.lobby_not_set"));
            return;
        }
        Location lobby = new Location(Bukkit.getWorld(data.getString("lobby.world")), data.getDouble("lobby.x"),
                data.getDouble("lobby.y"), data.getDouble("lobby.z"));
        player.teleport(lobby);
        player.sendMessage(getMessage("player.teleport_success"));
    }

    public void join(String arg) {
        Group selectedGroup = null;
        Site selectedSite = null;
        if(usingSiteMap.containsValue(sender.getName())) {
            sender.sendMessage(getMessage("error.also_joined"));
            return;
        }
        if(arg == null) {
            if(siteList.isEmpty()) {
                new ErrorCommandHandler(sender).notFound("Site");
                return;
            }
            boolean using = false;
            for(Site site : siteList) {
                if(usingSiteMap.containsKey(site)) {
                    using = true;
                } else {
                    selectedSite = site;
                }
            }
            if(selectedSite == null && using) {
                new ErrorCommandHandler(sender).using("site");
                return;
            }
        } else {
            for(Group group : groupList) {
                if(group.getName().equals(arg)) {
                    selectedGroup = group;
                    break;
                }
            }
            if(selectedGroup == null) {
                for(Site site : siteList) {
                    if(site.getName().equals(arg)) {
                        if(usingSiteMap.containsKey(site)) {
                            new ErrorCommandHandler(sender).using("site");
                            return;
                        }
                        selectedSite = site;
                        break;
                    }
                }
                if(selectedSite == null) {
                    new ErrorCommandHandler(sender).notFound("Group or site");
                    return;
                }
            } else {
                boolean using = false;
                for(Site site : siteList) {
                    if(site.getGroup().equals(selectedGroup)) {
                        if(usingSiteMap.containsKey(site)) {
                            using = true;
                        } else {
                            selectedSite = site;
                            break;
                        }
                    }
                }
                if(selectedSite == null && using) {
                    new ErrorCommandHandler(sender).using("site");
                    return;
                } else if(selectedSite == null) {
                    new ErrorCommandHandler(sender).notFound("Group or site");
                    return;
                }
            }
        }
        Player player = (Player) sender;
        player.teleport(selectedSite.getSpawn());
        usingSiteMap.put(selectedSite, player.getName());
        placedBlockMap.put(player.getName(), new ArrayList<>());
        player.sendMessage(getMessage("player.join_success").replace("%site%", selectedSite.getDisplayName()));
    }

    public void exit() {
        Player player = (Player) sender;
        if(usingSiteMap.containsValue(player.getName())) {
            Common.resetSite(player);
        } else {
            new ErrorCommandHandler(sender).notIn("site");
        }
    }

    public void spawn() {
        Player player = (Player) sender;
        if(!usingSiteMap.containsValue(player.getName())) {
            new ErrorCommandHandler(sender).notIn("site");
        }
        for(Map.Entry<Site, String> entry : usingSiteMap.entrySet()) {
            if(entry.getValue().equals(player.getName())) {
                player.teleport(entry.getKey().getSpawn());
                player.sendMessage(getMessage("player.teleport_success"));
                return;
            }
        }
    }
}
