package net.badbird5907.jdacommand.util;

import java.util.HashMap;
import java.util.Map;

public class Cooldown {
    private static final Map<String, HashMap<String, Long>> cooldowns = new HashMap<>(); // <command, <user, time>>

    public static void createCooldown(String command) {
        cooldowns.put(command.toLowerCase(), new HashMap<>());
    }

    public static void addUser(String command, String user, double time) {
        command = command.toLowerCase();
        user = user.toLowerCase();
        if (cooldowns.get(command) == null) {
            createCooldown(command);
        }
        cooldowns.get(command).put(user, System.currentTimeMillis() + (long) (time * 1000));
    }

    public static boolean isOnCooldown(String command, String user) {
        command = command.toLowerCase();
        user = user.toLowerCase();
        if (cooldowns.get(command).containsKey(user)) {
            if (cooldowns.get(command).get(user) > System.currentTimeMillis()) {
                return true;
            } else {
                cooldowns.get(command).remove(user);
                return false;
            }
        }
        return false;
    }

    public static long getCooldownTime(String command, String user) {
        return cooldowns.get(command.toLowerCase()).get(user.toLowerCase()) - System.currentTimeMillis();
    }

    public static double getCooldownTimeInSeconds(String command, String user) {

        return (double) getCooldownTime(command.toLowerCase(), user.toLowerCase()) / 1000;
    }

    public static void removeUser(String command, String user) {
        cooldowns.get(command.toLowerCase()).remove(user.toLowerCase());
    }

    public static void removeCooldown(String command) {
        cooldowns.remove(command.toLowerCase());
    }
}
