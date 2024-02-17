package com.alazeprt.abt.utils;

public class Group {
    private final String name;
    private final String displayName;
    public Group(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "Group {" + name + ", " + displayName + "}";
    }

    public static Group toGroup(String group) {
        group = group.substring(7);
        group = group.substring(0, group.length() - 1);
        String[] split = group.split(", ");
        return new Group(split[0], split[1]);
    }
}
