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
            new ErrorCommandHandler(sender).lobbyNotSet();
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
            new ErrorCommandHandler(sender).alsoJoinedSite();
            return;
        }
        if(arg == null) {
            if(siteList.isEmpty()) {
                new ErrorCommandHandler(sender).notFound("site");
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
                new ErrorCommandHandler(sender).stillOccupied("site");
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
                            new ErrorCommandHandler(sender).stillOccupied("site");
                            return;
                        }
                        selectedSite = site;
                        break;
                    }
                }
                if(selectedSite == null) {
                    new ErrorCommandHandler(sender).stillOccupied("Group or site");
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
                    new ErrorCommandHandler(sender).stillOccupied("site");
                    return;
                } else if(selectedSite == null) {
                    new ErrorCommandHandler(sender).notFound("group or site");
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
            new ErrorCommandHandler(sender).notInSomewhere("site");
        }
    }

    public void spawn() {
        Player player = (Player) sender;
        if(!usingSiteMap.containsValue(player.getName())) {
            new ErrorCommandHandler(sender).notInSomewhere("site");
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
