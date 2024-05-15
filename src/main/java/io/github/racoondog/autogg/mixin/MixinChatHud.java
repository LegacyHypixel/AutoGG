package io.github.racoondog.autogg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.racoondog.autogg.AutoGG;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {
    @Unique private long lastTime = 0L;
    @Shadow @Final private MinecraftClient client;
    @Unique
    private final List<String> hypixelGGStrings = Arrays.asList(
        "1st Killer -",
        "1st Place -",
        "Winner:",
        " - Damage Dealt -",
        "Winning Team -",
        "1st -",
        "Winners:",
        "Winner:",
        "Winning Team:",
        " won the game!",
        "Top Seeker:",
        "1st Place:",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners -",
        "Sumo Duel -",
        "Most Wool Placed -",
        "Your Overall Winstreak:"
    );

    @Unique
    private final List<String> bedwarsPracticeGGStrings = Arrays.asList(
        "Winners -",
        "Game Won!",
        "Game Lost!",
        "The winning team is"
    );

    @Unique
    private final List<String> pvpLandGGStrings = Arrays.asList(
        "The match has ended!",
        "Match Results",
        "Winner:",
        "Loser:"
    );

    @Unique
    private final List<String> minemenGGStrings = Collections.singletonList("Match Results");

    @Unique
    private final List<String> hypixelGLHFStrings = Collections.singletonList("The game starts in 1 second!");

    @Unique
    private final List<String> bedwarsPracticeGLHFStrings = Arrays.asList(
        "Game starting in 1 seconds!",
        "Game has started!"
    );

    @Unique
    private final List<String> pvpLandGLHFStrings = Arrays.asList(
        "The match is starting in 1 second.",
        "The match has started!"
    );

    @Unique
    private final List<String> minemenGLHFStrings = Collections.singletonList("1...");

    @Unique
    private final List<String> hypixelGFStrings = Arrays.asList(
        "SkyWars Experience (Kill)",
        "coins! (Final Kill)"
    );

    @Unique
    private List<String> getBedwarsPracticeGFStrings() {
        return Collections.singletonList(this.client.getSession().getUsername() + " FINAL KILL!");
    }

    @Unique
    private List<String> getPvpLandGFStrings() {
        return Collections.singletonList("slain by " + this.client.getSession().getUsername());
    }

    @Unique
    private List<String> getMinemenGFStrings() {
        return Collections.singletonList("killed by " + this.client.getSession().getUsername() + "!");
    }

    @Unique
    private void processChat(Text messageReceived, List<String> options, String messageToSend) {
        for (String s : options) {
            if (!messageReceived.asUnformattedString().contains(s)) continue;
            this.client.player.sendChatMessage(messageToSend);
            this.lastTime = System.currentTimeMillis();
            return;
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value="HEAD"))
    private void typeGG(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        // disable in singleplayer
        if (this.client.isIntegratedServerRunning()) return;

        // prevent spamming
        if (this.lastTime != 0L && System.currentTimeMillis() - this.lastTime <= 3000L) return;

        if (this.client.getCurrentServerEntry().address.contains("hypixel.net")) {
            if (AutoGG.config.gfMessages) {
                this.processChat(message, this.hypixelGFStrings, "gf");
            }
            if (AutoGG.config.ggMessages) {
                this.processChat(message, this.hypixelGGStrings, "gg");
            }
            if (AutoGG.config.glhfMessages) {
                this.processChat(message, this.hypixelGLHFStrings, "glhf");
            }
        } else if (this.client.getCurrentServerEntry().address.contains("bedwarspractice.club")) {
            if (AutoGG.config.gfMessages) {
                this.processChat(message, this.getBedwarsPracticeGFStrings(), "gf");
            }
            if (AutoGG.config.ggMessages) {
                this.processChat(message, this.bedwarsPracticeGGStrings, "gg");
            }
            if (AutoGG.config.glhfMessages) {
                this.processChat(message, this.bedwarsPracticeGLHFStrings, "glhf");
            }
        } else if (this.client.getCurrentServerEntry().address.contains("pvp.land")) {
            if (AutoGG.config.gfMessages) {
                this.processChat(message, this.getPvpLandGFStrings(), "gf");
            }
            if (AutoGG.config.ggMessages) {
                this.processChat(message, this.pvpLandGGStrings, "gg");
            }
            if (AutoGG.config.glhfMessages) {
                this.processChat(message, this.pvpLandGLHFStrings, "glhf");
            }
        } else if (this.client.getCurrentServerEntry().address.contains("minemen.club")) {
            if (AutoGG.config.gfMessages) {
                this.processChat(message, this.getMinemenGFStrings(), "gf");
            }
            if (AutoGG.config.ggMessages) {
                this.processChat(message, this.minemenGGStrings, "gg");
            }
            if (AutoGG.config.glhfMessages) {
                this.processChat(message, this.minemenGLHFStrings, "glhf");
            }
        }
    }
}
