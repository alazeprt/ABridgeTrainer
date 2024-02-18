package com.alazeprt.abt.commands;

import com.alazeprt.abt.ABridgeTrainer;
import com.alazeprt.abt.utils.TempSite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import static com.alazeprt.abt.utils.Common.getGroup;

public class BasicCommandHandler implements CommandExecutor {
    public static void register() {
        ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class).getCommand("abt").setExecutor(new BasicCommandHandler());
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ErrorCommandHandler errorCommandHandler = new ErrorCommandHandler(sender);
        if(!sender.hasPermission("abt.player")) {
            errorCommandHandler.permission();
            return false;
        }
        PlayerCommandHandler playerCommandHandler = new PlayerCommandHandler(sender);
        AdminCommandHandler adminCommandHandler = new AdminCommandHandler(sender);
        switch (args.length) {
            case 0:
                if(sender.hasPermission("abt.admin")) {
                    adminCommandHandler.help();
                } else {
                    playerCommandHandler.help();
                }
                break;
            case 1:
                if(args[0].equals("help")) {
                    if(sender.hasPermission("abt.admin")) {
                        adminCommandHandler.help();
                    } else {
                        playerCommandHandler.help();
                    }
                }
                // admin commands
                else if(args[0].equals("setLobby")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.lobby();
                    }
                } else if(args[0].equals("listGroup")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listGroup("1");
                    }
                } else if(args[0].equals("listSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listSite("1");
                    }
                } else if(args[0].equals("reload")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.reload("all");
                    }
                }
                // player commands
                else if(args[0].equals("lobby")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.lobby();
                    }
                } else if(args[0].equals("join")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.join(null);
                    }
                } else if(args[0].equals("exit")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.exit();
                    }
                } else if(args[0].equals("spawn")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.spawn();
                    }
                } else {
                    errorCommandHandler.invalidArguments();
                }
                break;
            case 2:
                // admin commands
                if(args[0].equals("createGroup")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createGroup(args[1], args[1]);
                    }
                } else if(args[0].equals("removeGroup")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.removeGroup(args[1]);
                    }
                } else if(args[0].equals("listGroup")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listGroup(args[1]);
                    }
                } else if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createSite(new TempSite(args[1]));
                    }
                } else if(args[0].equals("editSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.editSite(args[1]);
                    }
                } else if(args[0].equals("removeSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.removeSite(args[1]);
                    }
                } else if(args[0].equals("listSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.listSite(args[1]);
                    }
                } else if(args[0].equals("setPos1")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setPos1(args[1]);
                    }
                } else if(args[0].equals("setPos2")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setPos2(args[1]);
                    }
                } else if(args[0].equals("setSpawn")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setSpawn(args[1]);
                    }
                } else if(args[0].equals("setEndPos")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.setEndPos(args[1]);
                    }
                } else if(args[0].equals("saveSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.saveSite(args[1]);
                    }
                } else if(args[0].equals("throwSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.throwSite(args[1]);
                    }
                } else if(args[0].equals("reload")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else if(!args[1].equals("config") && !args[1].equals("message") &&
                            !args[1].equals("all")) {
                        errorCommandHandler.invalidArguments();
                    } else {
                        adminCommandHandler.reload(args[1]);
                    }
                }
                // player commands
                else if(args[0].equals("join")) {
                    if(sender instanceof ConsoleCommandSender) {
                        errorCommandHandler.console();
                    } else {
                        playerCommandHandler.join(args[1]);
                    }
                } else {
                    errorCommandHandler.invalidArguments();
                }
                break;
            case 3:
                if(args[0].equals("createGroup")) { // TODO: default: displayname -> args[2] -> args[end-1]
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createGroup(args[1], args[2]);
                    }
                } else if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        adminCommandHandler.createSite(new TempSite(args[1], args[2]));
                    }
                } else {
                    errorCommandHandler.invalidArguments();
                }
                break;
            case 4:
                if(args[0].equals("createSite")) {
                    if(!sender.hasPermission("abt.admin")) {
                        errorCommandHandler.permission();
                    } else {
                        if(getGroup(args[3]) == null) {
                            errorCommandHandler.notFound("group");
                            return false;
                        } else {
                            adminCommandHandler.createSite(new TempSite(args[1], args[2], getGroup(args[3])));
                        }
                    }
                } else {
                    errorCommandHandler.invalidArguments();
                }
                break;
            default:
                errorCommandHandler.invalidArguments();
        }
        return false;
    }
}
