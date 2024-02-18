package com.alazeprt.abt.events;

import com.alazeprt.abt.ABridgeTrainer;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;

import java.util.*;

import static com.alazeprt.abt.utils.Common.getConfiguration;
import static com.alazeprt.abt.utils.Common.usingSiteList;

public class StopwatchEventHandler {
    private static final Map<String, Timer> map = new HashMap<>();
    public static void start(String player) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Timer timer = new Timer();
        timer.schedule(new PrintTask(player), 0L, getConfiguration("stopwatch.period", Integer.class));
        map.put(player, timer);
        usingSiteList.forEach(s -> {
            if(s.getValue1().equalsIgnoreCase(player)) {
                s.setValue2(stopWatch);
            }
        });
    }

    public static double stop(String player) {
        AtomicDouble atomicDouble = new AtomicDouble();
        usingSiteList.forEach(s -> {
            if(s.getValue1().equalsIgnoreCase(player)) {
                try {
                    s.getValue2().stop();
                } catch (IllegalStateException ignored) {}
                atomicDouble.set(s.getValue2().getTime());
                s.getValue2().reset();
            }
        });
        if(map.containsKey(player) && map.get(player) != null) {
            map.get(player).cancel();
            map.remove(player);
        }
        return atomicDouble.doubleValue();
    }

    static class PrintTask extends TimerTask {
        private final String player;
        public PrintTask(String player) {
            this.player = player;
        }

        @Override
        public void run() {
            Bukkit.getScheduler().runTask(ABridgeTrainer.getProvidingPlugin(ABridgeTrainer.class), () -> {
                usingSiteList.forEach(s -> {
                    if(s.getValue1().equalsIgnoreCase(player)) {
                        String formattedTime = getConfiguration("stopwatch.time_format", String.class)
                                .replace("hh", String.valueOf(s.getValue2().getTime()/1000/60/60))
                                .replace("mm", String.format("%02d", (s.getValue2().getTime()) / 1000 / 60 % 60))
                                .replace("ss", String.format("%02d", (s.getValue2().getTime()) / 1000 % 60))
                                .replace("ms", String.valueOf(s.getValue2().getTime()%1000));
                        getConfiguration("stopwatch.commands", List.class).forEach(command -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.toString()
                                    .replace("%player%", player)
                                    .replace("%time%", formattedTime));
                        });
                    }
                });
            });
        }
    }
}
