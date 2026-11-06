package dev.botfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class BotMessageFilter implements ClientModInitializer {
    private static final NumberedGroups GROUPS=new NumberedGroups();
    private static int ticks;
    @Override public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler,sender,client) -> { GROUPS.reset(); ticks=19; });
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client) -> { GROUPS.reset(); ticks=0; });
        ClientTickEvents.END_CLIENT_TICK.register(BotMessageFilter::observeOnlinePlayers);
        ClientReceiveMessageEvents.ALLOW_GAME.register((message,overlay) -> !shouldHide(message,overlay));
    }
    private static void observeOnlinePlayers(Minecraft client) {
        if (client.getConnection()==null || ++ticks<20) return;
        ticks=0;
        for (var info:client.getConnection().getOnlinePlayers()) GROUPS.observe(info.getProfile().name());
    }
    static boolean shouldHide(Component message, boolean overlay) {
        if (overlay || !(message.getContents() instanceof TranslatableContents translated)) return false;
        String key=translated.getKey();
        boolean joined=key.equals("multiplayer.player.joined") || key.equals("multiplayer.player.joined.renamed");
        boolean left=key.equals("multiplayer.player.left");
        boolean death=key.startsWith("death.");
        if (!joined && !left && !death) return false;
        Object[] args=translated.getArgs();
        if (args.length==0) return false;
        String subject=args[0] instanceof Component component ? component.getString() : args[0] instanceof String string ? string : null;
        if (joined) GROUPS.observe(subject);
        // Only the player joining, leaving, or dying is checked; the killer may be another player.
        return GROUPS.matches(subject);
    }
}
