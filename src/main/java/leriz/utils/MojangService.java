package leriz.utils;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class MojangService {

    private static final String API_URL = "https://api.ashcon.app/mojang/v2/user/";

    public static void lookup(String input, Consumer<PlayerProfile> callback) {
        PlayerProfile cached = NameCache.get(input);
        if (cached != null) {
            callback.accept(cached);
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(API_URL + input);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);
                con.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (con.getResponseCode() != 200) {
                    returnResult(null, callback);
                    return;
                }

                JsonObject json = JsonParser.parseReader(
                        new InputStreamReader(con.getInputStream())
                ).getAsJsonObject();

                String uuid = json.get("uuid").getAsString();
                String currentName = json.get("username").getAsString();

                PlayerProfile profile = new PlayerProfile(uuid, currentName);

                if (json.has("username_history")) {
                    JsonArray history = json.getAsJsonArray("username_history");
                    for (JsonElement el : history) {
                        JsonObject entry = el.getAsJsonObject();
                        String name = entry.get("username").getAsString();

                        long time = -1L;
                        if (entry.has("changed_at")) {
                            String dateStr = entry.get("changed_at").getAsString();
                            time = java.time.Instant.parse(dateStr).toEpochMilli();
                        }

                        profile.nameHistory.put(name, time);
                    }
                }

                NameCache.put(profile);
                returnResult(profile, callback);

            } catch (Exception e) {
                returnResult(null, callback);
            }
        }, "PastNames-Lookup").start();
    }

    private static void returnResult(PlayerProfile profile, Consumer<PlayerProfile> cb) {
        MinecraftClient.getInstance().execute(() -> cb.accept(profile));
    }
}