package dev.botfilter;

import net.minecraft.network.chat.Component;

public final class FilterTest {
    private static void check(boolean condition,String message) { if (!condition) throw new AssertionError(message); }
    private static Component joined(String name) { return Component.translatable("multiplayer.player.joined",Component.literal(name)); }
    public static void main(String[] args) {
        NumberedGroups groups=new NumberedGroups();
        for (int n=1;n<=9;n++) groups.observe("TBM_"+n);
        check(!groups.matches("TBM_1"),"nine distinct numbers do not qualify");
        groups.observe("TBM_01");
        check(!groups.matches("TBM_1"),"leading zeros do not create a new number");
        groups.observe("tbm_10");
        check(groups.matches("TBM_2048") && groups.matches("tbm_0"),"ten numbers qualify regardless of case");
        check(!groups.matches("TBM_foo") && !groups.matches("OTHER_1") && !groups.matches("123"),"other names are preserved");
        groups.reset(); check(!groups.matches("TBM_1"),"disconnect resets detection");
        for (int n=1;n<=9;n++) check(!BotMessageFilter.shouldHide(joined("BOT_"+n),false),"initial joins remain visible");
        check(BotMessageFilter.shouldHide(joined("BOT_10"),false),"tenth distinct join is filtered");
        check(BotMessageFilter.shouldHide(Component.translatable("multiplayer.player.left",Component.literal("BOT_12")),false),"matching leave filtered");
        check(BotMessageFilter.shouldHide(Component.translatable("death.attack.player",Component.literal("BOT_2"),Component.literal("Alice")),false),"matching victim filtered");
        check(!BotMessageFilter.shouldHide(Component.translatable("death.attack.player",Component.literal("Alice"),Component.literal("BOT_2")),false),"real victim message kept");
        check(!BotMessageFilter.shouldHide(Component.translatable("multiplayer.player.joined",Component.literal("Alice")),false),"ordinary join kept");
        check(!BotMessageFilter.shouldHide(Component.translatable("chat.type.text",Component.literal("BOT_2"),Component.literal("hello")),false),"chat kept");
        check(!BotMessageFilter.shouldHide(Component.literal("BOT_2 joined the game"),false),"unstructured messages kept");
        check(!BotMessageFilter.shouldHide(joined("BOT_11"),true),"overlay kept");
        System.out.println("Filter tests passed");
    }
}
