package com.alazeprt.afb.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.alazeprt.afb.AFastBuilder.*;

public class PlayerCommandHandler {
    private final CommandSender sender;
    public PlayerCommandHandler(CommandSender sender) {
        this.sender = sender;
    }
    public void help() {
        sender.sendMessage(getMessage("player.help"));
    }

    public void lobby() {
        Player player = (Player) sender;
        Location lobby = new Location(Bukkit.getWorld(data.getString("lobby.world")), data.getDouble("lobby.x"),
                data.getDouble("lobby.y"), data.getDouble("lobby.z"));
        player.teleport(lobby);
        player.sendMessage(getMessage("player.teleport_success"));
    }
}
