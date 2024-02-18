package com.alazeprt.abt.commands;

import com.alazeprt.abt.utils.Common;
import com.alazeprt.abt.utils.Group;
import com.alazeprt.abt.utils.Point;
import com.alazeprt.abt.utils.Site;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean inSite = new AtomicBoolean(false);
        usingSiteList.forEach(s -> {
            if(s.getValue1().equalsIgnoreCase(sender.getName())) {
                inSite.set(true);
            }
        });
        if(inSite.get()) {
            new ErrorCommandHandler(sender).alsoJoinedSite();
            return;
        }
        if(arg == null) {
            if(siteList.isEmpty()) {
                new ErrorCommandHandler(sender).notFound("site");
                return;
            }
            AtomicBoolean using = new AtomicBoolean(false);
            for(Site site : siteList) {
                usingSiteList.forEach(s -> {
                    if(s.getKey().equals(site)) {
                        using.set(true);
                    }
                });
                if(!using.get()) {
                    selectedSite = site;
                }
            }
            if(selectedSite == null && using.get()) {
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
                        AtomicBoolean containsKey = new AtomicBoolean(false);
                        usingSiteList.forEach(s -> {
                            if(s.getKey().equals(site)) {
                                containsKey.set(true);
                            }
                        });
                        if(containsKey.get()) {
                            new ErrorCommandHandler(sender).stillOccupied("site");
                            return;
                        }
                        selectedSite = site;
                        break;
                    }
                }
                if(selectedSite == null) {
                    new ErrorCommandHandler(sender).notFound("group and site");
                    return;
                }
            } else {
                AtomicBoolean using = new AtomicBoolean(false);
                for(Site site : siteList) {
                    if(site.getGroup().equals(selectedGroup)) {
                        usingSiteList.forEach(s -> {
                            if(s.getKey().equals(site)) {
                                using.set(true);
                            }
                        });
                        if(!using.get()) {
                            selectedSite = site;
                        }
                    }
                }
                if(selectedSite == null && using.get()) {
                    new ErrorCommandHandler(sender).stillOccupied("site");
                    return;
                } else if(selectedSite == null) {
                    new ErrorCommandHandler(sender).notFound("site");
                    return;
                }
            }
        }
        Player player = (Player) sender;
        List<Object> blocks = getConfiguration("siteBlocks", List.class);
        player.getInventory().clear();
        for(int i = 0; i < blocks.size(); i++) {
            try {
                Material material = Material.valueOf(blocks.get(i).toString());
                player.getInventory().setItem(i, new ItemStack(material));
            } catch (Exception ignored) {}
        }
        player.teleport(selectedSite.getSpawn());
        usingSiteList.add(new Point<>(selectedSite, player.getName(), new StopWatch()));
        placedBlockMap.put(player.getName(), new ArrayList<>());
        player.sendMessage(getMessage("player.join_success").replace("%site%", selectedSite.getDisplayName())
                .replace("&", "§"));
    }

    public void exit() {
        Player player = (Player) sender;
        AtomicBoolean contains = new AtomicBoolean(false);
        usingSiteList.forEach(s -> {
            if(s.getValue1().equals(player.getName())) {
                contains.set(true);
            }
        });
        if(contains.get()) {
            new PlayerCommandHandler(player).lobby();
            Common.resetSite(player, true);
            player.sendMessage(getMessage("player.exit_success"));
        } else {
            new ErrorCommandHandler(sender).notInSomewhere("site");
        }
    }

    public void spawn() {
        Player player = (Player) sender;
        AtomicBoolean contains = new AtomicBoolean(false);
        usingSiteList.forEach(s -> {
            if(s.getValue1().equals(player.getName())) {
                contains.set(true);
            }
        });
        if(!contains.get()) {
            new ErrorCommandHandler(sender).notInSomewhere("site");
            return;
        }
        for(Point<Site, String, StopWatch> entry : usingSiteList) {
            if(entry.getValue1().equals(player.getName())) {
                player.teleport(entry.getKey().getSpawn());
                player.sendMessage(getMessage("player.teleport_success"));
                return;
            }
        }
    }
}
