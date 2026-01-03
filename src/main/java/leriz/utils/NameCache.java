package leriz.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NameCache {

    private static final Map<String, PlayerProfile> CACHE = new ConcurrentHashMap<>();

    public static void put(PlayerProfile profile) {
        CACHE.put(profile.uuid, profile);

        for (String name : profile.nameHistory.keySet()) {
            CACHE.put(name.toLowerCase(), profile);
        }
    }

    public static PlayerProfile get(String input) {
        return CACHE.get(input.toLowerCase());
    }
}
