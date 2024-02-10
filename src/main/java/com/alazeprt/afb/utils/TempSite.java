package com.alazeprt.afb.utils;

import org.bukkit.Location;

public class TempSite {
    private final String name;
    private String displayname;
    private Location pos1;
    private Location pos2;
    private Location spawn;
    private Location endPos;
    private Group group;
    public TempSite(String name) {
        this.name = name;
    }

    public TempSite(String name, Group group) {
        this.name = name;
        this.group = group;
    }

    public TempSite(String name, String displayname) {
        this.name = name;
        this.displayname = displayname;
    }

    public TempSite(String name, String displayname, Group group) {
        this.name = name;
        this.displayname = displayname;
        this.group = group;
    }

    public String getDisplayName() {
        return displayname;
    }

    public Location getEndPos() {
        return endPos;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getPos2() {
        return pos2;
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Group getGroup() {
        return group;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setEndPos(Location endPos) {
        this.endPos = endPos;
    }
}
