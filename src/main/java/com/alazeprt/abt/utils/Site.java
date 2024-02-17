package com.alazeprt.abt.utils;

import org.bukkit.Location;

public class Site {
    private final String name;
    private String displayname;
    private Location pos1;
    private Location pos2;
    private Location spawn;
    private Location endPos;
    private Group group;
    public Site(String name, Location pos1, Location pos2, Location spawn, Location endPos) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawn = spawn;
        this.endPos = endPos;
        this.group = null;
        this.displayname = name;
    }

    public Site(String name, Location pos1, Location pos2, Location spawn, Location endPos, Group group) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawn = spawn;
        this.endPos = endPos;
        this.group = group;
        this.displayname = name;
    }

    public Site(String name, Location pos1, Location pos2, Location spawn, Location endPos, String displayname) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawn = spawn;
        this.endPos = endPos;
        this.group = null;
        this.displayname = displayname;
    }

    public Site(String name, Location pos1, Location pos2, Location spawn, Location endPos, Group group, String displayname) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawn = spawn;
        this.endPos = endPos;
        this.group = group;
        this.displayname = displayname;
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

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getEndPos() {
        return endPos;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String displayname) {
        this.displayname = displayname;
    }
}