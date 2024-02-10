package com.alazeprt.afb.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static com.alazeprt.afb.utils.Common.getMessage;

public class ErrorCommandHandler {
    private final CommandSender sender;
    public ErrorCommandHandler(CommandSender sender) {
        this.sender = sender;
    }
    public void permission() {
        sender.sendMessage(getMessage("error.no_permission"));
    }

    public void console() {
        sender.sendMessage(getMessage("error.not_supported_console"));
    }

    public void operation(String operationName) {
        sender.sendMessage(getMessage("error.operation_failed").replace("%operation%", operationName));
    }

    public void operation(String operationName, Throwable throwable) {
        sender.sendMessage(getMessage("error.operation_failed_detail").replace("%operation%", operationName) + throwable.getMessage());
    }
}
