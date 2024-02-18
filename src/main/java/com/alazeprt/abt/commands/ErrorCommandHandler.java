package com.alazeprt.abt.commands;

import org.bukkit.command.CommandSender;
import static com.alazeprt.abt.utils.Common.getMessage;

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

    public void lobbyNotSet() {
        sender.sendMessage(getMessage("error.lobby_not_set"));
    }

    public void nameExists() {
        sender.sendMessage(getMessage("error.name_exists"));
    }

    public void alsoJoinedSite() {
        sender.sendMessage(getMessage("error.also_joined_site"));
    }

    public void notAllowed() {
        sender.sendMessage(getMessage("error.not_allowed"));
    }

    public void notFound(String somewhere) {
        sender.sendMessage(getMessage("error.not_found").replace("%somewhere%", somewhere));
    }

    public void notInSomewhere(String somewhere) {
        sender.sendMessage(getMessage("error.not_in_somewhere").replace("%somewhere%", somewhere));
    }

    public void stillOccupied(String somewhere) {
        sender.sendMessage(getMessage("error.still_occupied").replace("%somewhere%", somewhere));
    }

    public void saveFailed(String somewhere, Throwable throwable) {
        sender.sendMessage(getMessage("error.save_failed").replace("%somewhere%", somewhere)
                .replace("%throwable%", throwable.toString()));
    }

    public void removeFailed(String somewhere, Throwable throwable) {
        sender.sendMessage(getMessage("error.remove_failed").replace("%somewhere%", somewhere)
                .replace("%throwable%", throwable.toString()));
    }

    public void pageNotInteger() {
        sender.sendMessage(getMessage("error.page_not_integer"));
    }

    public void siteIncompleteEditing() {
        sender.sendMessage(getMessage("error.site_incomplete_editing"));
    }

    public void invalidArguments() {
        sender.sendMessage(getMessage("error.invalid_arguments"));
    }
}
