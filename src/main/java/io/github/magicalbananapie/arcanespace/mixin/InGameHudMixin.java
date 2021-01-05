package io.github.magicalbananapie.arcanespace.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.util.LivingEntityAccessor;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private MinecraftClient client;

    //We want to do some sort of inject or the like into the render method
    //Minecraft's effect can be freezing, this will be frozen
    //TODO: Either that, or make this freeze effect actually freeze the player's camera and pose and give a third person visual effect
    //Notice: This could crash with mods that change the player type, and also in spectator mode, and therefore should be replaced
    // Also, after testing a bit, this doesn't seem to work
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(this.client.getCameraEntity() instanceof LivingEntity) { //Because freezeTime is in livingEntity
            int freezeTime = ((LivingEntityAccessor) this.client.getCameraEntity()).getFreezeTime();
            if (client.options.getPerspective().isFirstPerson() && (freezeTime != 0 ||
                    Objects.requireNonNull(client.player).inventory.getArmorStack(3).getItem() == Blocks.ICE.asItem())) {
                renderIceOverlay(MinecraftClient.getInstance(), freezeTime);
            }
        }
    }

    private static final Identifier ICE_TEXTURE = new Identifier("textures/block/ice.png");

    @SuppressWarnings("deprecation")
    private static void renderIceOverlay(MinecraftClient client, int currentTicks) {
        ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        float transparency = currentTicks==0?1.0F:currentTicks * (1f / config.voidConfig.ticksUntilFullyFrozen);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, transparency);
        RenderSystem.disableAlphaTest();
        client.getTextureManager().bindTexture(ICE_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0D, client.getWindow().getScaledHeight(), -90.0D).texture(0.0F, 1.0F).next();
        bufferBuilder.vertex(client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), -90.0D).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(client.getWindow().getScaledWidth(), 0.0D, -90.0D).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}