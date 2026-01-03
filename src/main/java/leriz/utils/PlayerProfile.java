package leriz.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerProfile {
    public final String uuid;
    public final String currentName;
    public final Map<String, Long> nameHistory = new LinkedHashMap<>();

    public PlayerProfile(String uuid, String currentName) {
        this.uuid = uuid;
        this.currentName = currentName;
    }
}
