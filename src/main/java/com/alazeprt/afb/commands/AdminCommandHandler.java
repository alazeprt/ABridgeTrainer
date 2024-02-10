package com.alazeprt.afb.commands;

import com.alazeprt.afb.AFastBuilder;
import com.alazeprt.afb.utils.Group;
import com.alazeprt.afb.utils.Site;
import com.alazeprt.afb.utils.TempSite;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static com.alazeprt.afb.AFastBuilder.*;

public class AdminCommandHandler {
    private final CommandSender sender;
    public AdminCommandHandler(CommandSender sender) {
        this.sender = sender;
    }
    public void help() {
        sender.sendMessage(getMessage("admin.help"));
    }

    public void lobby() {
        Player player = (Player) sender;
        data.set("lobby.world", player.getLocation().getWorld().getName());
        data.set("lobby.x", player.getLocation().getBlockX());
        data.set("lobby.y", player.getLocation().getBlockY());
        data.set("lobby.z", player.getLocation().getBlockZ());
        if(getConfiguration("autoSave", Boolean.class)) {
            try {
                data.save(new File(AFastBuilder.getProvidingPlugin(AFastBuilder.class).getDataFolder(), "data.yml"));
            } catch (IOException e) {
                new ErrorCommandHandler(sender).operation("setLobby", e);
            }
        }
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void createGroup(String name, String displayName) {
        boolean hasSameName = false;
        for(Group group : groupList) {
            if(group.getName().equals(name)) {
                hasSameName = true;
                break;
            }
        }
        if(hasSameName) {
            sender.sendMessage(getMessage("error.name_exist"));
            return;
        }
        groupList.add(new Group(name, displayName));
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void removeGroup(String object) {
        if(getGroup(object) == null) {
            new ErrorCommandHandler(sender).operation("remove group", new Throwable("Group not found"));
        } else {
            groupList.remove(getGroup(object));
            sender.sendMessage(getMessage("admin.operation_successful"));
        }
    }

    public void listGroup(String page) {
        try {
            Integer.parseInt(page);
        } catch (Exception e) {
            new ErrorCommandHandler(sender).operation("get page content", e);
            return;
        }
        int intPage = Integer.parseInt(page);
        if(intPage < 1) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        int contentNum = groupList.size();
        int contentPerPage = getConfiguration("contentPerPage", Integer.class);
        if(contentNum == 0) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        if((contentNum / contentPerPage) + 1 < intPage) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        int startContent = ((intPage-1) * contentPerPage) + 1;
        int endContent = intPage * contentPerPage;
        sender.sendMessage(getMessage("admin.page.group.head").replace("%start_page%", String.valueOf(startContent))
                .replace("%end_page%", String.valueOf(endContent)));
        for(int i = startContent; i <= endContent; i++) {
            if(groupList.size() <= i-1) {
                break;
            }
            sender.sendMessage(getMessage("admin.page.group.info").replace("%content_num%", String.valueOf(i))
                    .replace("%content_name%", groupList.get(i-1).getName())
                    .replace("%content_display_name%", groupList.get(i-1).getDisplayName()));
        }
        sender.sendMessage(getMessage("admin.page.group.footer").replace("%current_page%", String.valueOf(intPage))
                .replace("%total_page%", String.valueOf((contentNum / contentPerPage) + 1)));
    }

    public void createSite(TempSite tempSite) {
        boolean hasSameName = false;
        for(Site site : siteList) {
            if(site.getName().equals(tempSite.getName())) {
                hasSameName = true;
                break;
            }
        }
        for(TempSite tempSite1 : tempSiteList) {
            if(tempSite1.getName().equals(tempSite.getName())) {
                hasSameName = true;
                break;
            }
        }
        if(hasSameName) {
            sender.sendMessage(getMessage("error.name_exist"));
            return;
        }
        tempSiteList.add(tempSite);
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void editSite(String arg) {
        if(getSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("edit site", new Throwable("Site not found"));
            return;
        }
        Site site = getSite(arg);
        TempSite tempSite = new TempSite(site.getName(), site.getDisplayName(), site.getGroup());
        tempSite.setPos1(site.getPos1());
        tempSite.setPos2(site.getPos2());
        tempSite.setSpawn(site.getSpawn());
        tempSite.setEndPos(site.getEndPos());
        tempSiteList.add(tempSite);
        sender.sendMessage(getMessage("admin.successfully_enable_edit_mode"));
    }

    public void removeSite(String arg) {
        if(getSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("remove site", new Throwable("Site not found"));
            return;
        }
        siteList.remove(getSite(arg));
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void listSite(String page) {
        try {
            Integer.parseInt(page);
        } catch (Exception e) {
            new ErrorCommandHandler(sender).operation("get page content", e);
            return;
        }
        int intPage = Integer.parseInt(page);
        if(intPage < 1) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        int contentNum = siteList.size();
        int contentPerPage = getConfiguration("contentPerPage", Integer.class);
        if(contentNum == 0) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        if((contentNum / contentPerPage) + 1 < intPage) {
            sender.sendMessage(getMessage("admin.page.not_found"));
            return;
        }
        int startContent = ((intPage-1) * contentPerPage) + 1;
        int endContent = intPage * contentPerPage;
        sender.sendMessage(getMessage("admin.page.site.head").replace("%start_page%", String.valueOf(startContent))
                .replace("%end_page%", String.valueOf(endContent)));
        for(int i = startContent; i <= endContent; i++) {
            if(siteList.size() <= i-1) {
                break;
            }
            System.out.println(siteList.get(i-1).getName() + " " + siteList.get(i-1).getDisplayName());
            sender.sendMessage(getMessage("admin.page.site.info").replace("%content_num%", String.valueOf(i))
                    .replace("%content_name%", siteList.get(i-1).getName())
                    .replace("%content_display_name%", siteList.get(i-1).getDisplayName()));
        }
        sender.sendMessage(getMessage("admin.page.site.footer").replace("%current_page%", String.valueOf(intPage))
                .replace("%total_page%", String.valueOf((contentNum / contentPerPage) + 1)));
    }

    public void setPos1(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("set pos1", new Throwable("TempSite not found"));
            return;
        }
        getTempSite(arg).setPos1(((Player) sender).getLocation());
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void setPos2(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("set pos2", new Throwable("TempSite not found"));
            return;
        }
        getTempSite(arg).setPos2(((Player) sender).getLocation());
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void setSpawn(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("set spawn", new Throwable("TempSite not found"));
            return;
        }
        getTempSite(arg).setSpawn(((Player) sender).getLocation());
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void setEndPos(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("set endPos", new Throwable("TempSite not found"));
            return;
        }
        getTempSite(arg).setEndPos(((Player) sender).getLocation());
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void saveSite(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("save site", new Throwable("TempSite not found"));
            return;
        }
        TempSite tempSite = getTempSite(arg);
        if(tempSite.getPos1() == null && tempSite.getPos2() == null || tempSite.getSpawn() == null || tempSite.getEndPos() == null) {
            new ErrorCommandHandler(sender).operation("save site", new Throwable("Some arguments is null"));
            return;
        }
        Site site = new Site(tempSite.getName(), tempSite.getPos1(), tempSite.getPos2(),
                tempSite.getSpawn(), tempSite.getEndPos(), tempSite.getGroup(),
                tempSite.getDisplayName() == null ? tempSite.getName() : tempSite.getDisplayName());
        siteList.add(site);
        tempSiteList.remove(tempSite);
        sender.sendMessage(getMessage("admin.operation_successful"));
    }

    public void throwSite(String arg) {
        if(getTempSite(arg) == null) {
            new ErrorCommandHandler(sender).operation("throw site", new Throwable("TempSite not found"));
            return;
        }
        tempSiteList.remove(getTempSite(arg));
        sender.sendMessage(getMessage("admin.operation_successful"));
    }
}
