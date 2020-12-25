package io.github.magicalbananapie.arcanespace.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.item.ArcaneItems;
import io.github.magicalbananapie.arcanespace.util.ArcaneTags;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique private static final ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
    @Unique private static final Identifier PIXELATED_SHADOW = new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/shadow_low.png");
    @Unique private static final Identifier SHADOW = new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/shadow.png");
    @Unique private static final Identifier PIXELATED_GLOW = new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/glow_low.png");
    @Unique private static final Identifier GLOW = new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/glow.png");

    @Shadow @Final private ItemModels models;

    @Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer4);

    //This is like this because I want to give the Singularity a different 3D model when in the player's hand
    //I have tried for multiple days, but it looks like all background blur effects are going to have to be either
    // 100% generated or provided as textures, no mixtures of texture and color have worked for some reason...
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo info) {
        if (!stack.isEmpty()) {
            //NOTICE: This is an Avaritia-like item background, this was a pain to implement and took many, many revisions
            //TODO: This probably needs to be 24x24 instead of 20x20, also the itemRenderer needs to be added to the singularity
            //FIXME: There is a visual bug when selecting multiple items with drag-right-click due to this and the white square
            //FIXME: The shadow seems to cause some of the gui rendering to disappear
            if(!stack.getRarity().equals(Rarity.COMMON) || stack.getItem().isIn(ArcaneTags.GRAVITY)) {
                matrices.push();
                matrices.translate(-0.5D, -0.5D, -1.0D); //-0.5 z is in the center, but that clips into blockitems
                Matrix4f matrix4f = matrices.peek().getModel();
                RenderSystem.pushMatrix();
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();
                RenderSystem.disableLighting();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                final float offset = 1 / 8F; //20 Pixels (16+(1/8)*16*2) Fun Fact: this fits perfectly inside item frames
                if (stack.getItem().isIn(ArcaneTags.GRAVITY) && renderMode == ModelTransformation.Mode.GUI && config.visualConfig.shadowEnabled) {
                    MinecraftClient.getInstance().getTextureManager().bindTexture(config.visualConfig.pixelatedShadow ? PIXELATED_SHADOW : SHADOW);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                    buffer.vertex(matrix4f, 0f - offset, 1f + offset, 0.5F).color(0f, 1f, 1f, 1f).texture(0f, 1f).next();
                    buffer.vertex(matrix4f, 1f + offset, 1f + offset, 0.5F).color(0f, 1f, 1f, 1f).texture(1f, 1f).next();
                    buffer.vertex(matrix4f, 1f + offset, 0f - offset, 0.5F).color(0f, 1f, 1f, 1f).texture(1f, 0f).next();
                    buffer.vertex(matrix4f, 0f - offset, 0f - offset, 0.5F).color(0f, 1f, 1f, 1f).texture(0f, 0f).next();
                    tessellator.draw();
                } else if (!stack.getRarity().equals(Rarity.COMMON) && renderMode == ModelTransformation.Mode.GUI && config.visualConfig.glowEnabled) {
                    MinecraftClient.getInstance().getTextureManager().bindTexture(config.visualConfig.pixelatedShadow ? PIXELATED_GLOW : GLOW);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                    int color = stack.getRarity().formatting.getColorValue();
                    float r = ((color >> 16) & 0xff) / 255.0F;
                    float g = ((color >> 8) & 0xff) / 255.0F;
                    float b = (color & 0xff) / 255.0F;
                    buffer.vertex(matrix4f, 0f - offset, 1f + offset, 0.5F).color(r, g, b, 1f).texture(0f, 1f).next();
                    buffer.vertex(matrix4f, 1f + offset, 1f + offset, 0.5F).color(r, g, b, 1f).texture(1f, 1f).next();
                    buffer.vertex(matrix4f, 1f + offset, 0f - offset, 0.5F).color(r, g, b, 1f).texture(1f, 0f).next();
                    buffer.vertex(matrix4f, 0f - offset, 0f - offset, 0.5F).color(r, g, b, 1f).texture(0f, 0f).next();
                    tessellator.draw();
                }
                RenderSystem.enableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.enableLighting();
                RenderSystem.enableAlphaTest();
                RenderSystem.popMatrix();
                matrices.pop();
            }
            //Singularity will have a different handheld model
            if (stack.getItem() == ArcaneItems.SINGULARITY) {
                matrices.push();
                model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
                matrices.translate(-0.5D, -0.5D, -0.5D);
                if (model.isBuiltin() || !(renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED)) {
                    BuiltinModelItemRenderer.INSTANCE.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
                } else {
                    model = models.getModelManager().getModel(new ModelIdentifier(ArcaneSpace.MOD_ID + ":singularity#inventory"));
                    RenderLayer baseLayer = RenderLayers.getItemLayer(stack, true);
                    VertexConsumer baseVertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, baseLayer, true, stack.hasGlint());
                    renderBakedItemModel(model, stack, light, overlay, matrices, baseVertexConsumer);
                }
                matrices.pop();
                info.cancel();
            }
        }
    }
}
