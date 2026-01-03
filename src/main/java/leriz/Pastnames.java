package leriz;
import com.mojang.brigadier.arguments.StringArgumentType;
import leriz.utils.MojangService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
public class Pastnames implements ClientModInitializer {

    private static final SimpleDateFormat DATE =
            new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        literal("pastname")
                                .then(argument("input", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String input = StringArgumentType.getString(ctx, "input");
                                            MojangService.lookup(input, result -> {
                                                MinecraftClient mc = MinecraftClient.getInstance();
                                                if (mc.player == null) return;
                                                if (result == null) {
                                                    sendActionBar("No such player", Formatting.RED);
                                                    return;

                                                }
                                                mc.player.sendMessage(
                                                        Text.literal("§7§m------§6" + result.currentName + "§7§m-------"),
                                                        false
                                                );
                                                MutableText line = Text.literal("§7Past name: §e");
                                                Iterator<Map.Entry<String, Long>> it =
                                                        result.nameHistory.entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry<String, Long> e = it.next();
                                                    String name = e.getKey();
                                                    long time = e.getValue();
                                                    String hover = (time == -1L)
                                                            ? "Original name"
                                                            : "Changed at: " + DATE.format(new Date(time));
                                                    MutableText part = Text.literal(name)
                                                            .styled(style -> style.withHoverEvent(
                                                                    new HoverEvent(
                                                                            HoverEvent.Action.SHOW_TEXT,
                                                                            Text.literal(hover)
                                                                    )
                                                            ));
                                                    line.append(part);
                                                    if (it.hasNext()) {
                                                        line.append(Text.literal(", "));

                                                    }

                                                }
                                                mc.player.sendMessage(line, false);

                                                sendActionBar(
                                                        "Results for " + result.currentName + " have been sent to chat",
                                                        Formatting.GREEN
                                                );
                                            });
                                            return 1;

                                        })
                                )
                )
        );
    }
    private void sendActionBar(String msg, Formatting color) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.inGameHud.setOverlayMessage(
                    Text.literal(msg).formatted(color),
                    false
            );
        }
    }
}