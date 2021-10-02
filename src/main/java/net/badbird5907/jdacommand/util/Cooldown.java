package net.badbird5907.jdacommand.util;

import java.util.HashMap;

public class Cooldown {
    private static HashMap<String, HashMap<Long, Long>> cooldown = new HashMap<>();
    public static void createCooldown(String k) {
        if (!cooldown.containsKey(k.toLowerCase()))
            cooldown.put(k.toLowerCase(), new HashMap<>());
    }

    public static HashMap<Long, Long> getCooldownMap(String k) {
        if (cooldown.containsKey(k.toLowerCase()))
            return cooldown.get(k.toLowerCase());
        return null;
    }

    public static void addCooldown(String k, Long p, int seconds) {
        if (!cooldown.containsKey(k.toLowerCase()))
            throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
        long next = System.currentTimeMillis() + seconds * 1000L;
        cooldown.get(k.toLowerCase()).put(p, next);
    }

    public static boolean isOnCooldown(String k, Long p) {
        return (cooldown.containsKey(k.toLowerCase()) && cooldown.get(k.toLowerCase()).containsKey(p) &&
                System.currentTimeMillis() <= (Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p));
    }

    public static int getCooldownForUserInt(String k, Long p) {
        return (int)((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p) -
                System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForUserLong(String k, Long p) {
        return (int)((Long) ((HashMap) cooldown.get(k.toLowerCase())).get(p) -
                System.currentTimeMillis());
    }

    public static void removeCooldown(String k, Long p) {
        if (!cooldown.containsKey(k.toLowerCase()))
            throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
        if(cooldown.get(k.toLowerCase()).containsKey(p))
            ((HashMap)cooldown.get(k.toLowerCase())).remove(p);
    }
    public static boolean wasOnCooldown(String k, Long p){
        if(!cooldown.containsKey(k.toLowerCase()))
            throw new IllegalArgumentException(k.toLowerCase() + " does not exist");
        return ((HashMap)cooldown.get(k.toLowerCase())).containsKey(p);
    }
    public static boolean cooldownExists(String k){
        return cooldown.containsKey(k);
    }
}
