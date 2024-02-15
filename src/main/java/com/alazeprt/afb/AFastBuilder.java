package com.alazeprt.afb;

import com.alazeprt.afb.commands.BasicCommandHandler;
import com.alazeprt.afb.events.BasicEventHandler;
import com.alazeprt.afb.utils.ConfigurationHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/*
afastbuilder - adminCommand alias
setLobby [x, y, z] ✔
createGroup <name> [displayname] ✔
removeGroup <name/index> ✔
listGroup [page] ✔
createSite <name> [displayname] [group] // new TempSite() ✔
editSite <name/index>    // Site -> TempSite ✔
removeSite <name/index> // remove(Site) ✔
listSite [page] // list(Site) ✔
setPos1 <tempsite> ✔
setPos2 <tempsite> ✔
setSpawn <tempsite> ✔
setEndPos <tempsite> ✔
saveSite <tempsite> // remove(TempSite) && new Site() ✔
throwSite <tempsite> // remove(TempSite) ✔
reload [config/message] ✔

afastbuilder - playerCommand alias
spawn  (able to no prefix) ✔
lobby ✔
join [site/group] ✔
exit ✔
help

afastbuilder - events
join -> -> lobby ✔
place -> ? ✔
break -> ? ✔
move -> y=? -> die ✔?
exit -> remove using ✔
 */

public class AFastBuilder extends JavaPlugin implements CommandExecutor, Listener {
    @Override
    public void onEnable() {
        ConfigurationHandler.load(getDataFolder());
        BasicCommandHandler.register();
        BasicEventHandler.register();
    }

    @Override
    public void onDisable() {
        ConfigurationHandler.save(getDataFolder());
    }
}
