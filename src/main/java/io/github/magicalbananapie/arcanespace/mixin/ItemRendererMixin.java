package io.github.magicalbananapie.arcanespace.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.item.ArcaneItems;
import io.github.magicalbananapie.arcanespace.renderer.ArcaneSprites;
import io.github.magicalbananapie.arcanespace.renderer.GravityFocusItemRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Random;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    @Shadow protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer4);

    //This is like this because I want to give the Singularity a different 3D model when in the player's hand

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"), cancellable = true)
    public void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo info) {
        if (!stack.isEmpty() && stack.getItem() == ArcaneItems.SINGULARITY) {
            matrices.push();

            model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
            matrices.translate(-0.5D, -0.5D, -0.5D);
            if (model.isBuiltin() || !(renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED)) {
                BuiltinModelItemRenderer.INSTANCE.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
            } else {
                model = models.getModelManager().getModel(new ModelIdentifier(ArcaneSpace.MOD_ID + ":singularity#inventory"));
                RenderLayer baseLayer = RenderLayers.getItemLayer(stack, true);
                //NOTICE: This is an Avaritia-like item background, this was a pain to implement, I'm never doing this again (Without copy-paste)
                //TODO: This probably needs to be 24x24 instead of 20x20, DO LATER, also the renderer needs to be added in
                // now that this is done though, the texture just needs to be changed and implementation is easy for all similar things
                if(renderMode == ModelTransformation.Mode.GUI) {
                    MatrixStack.Entry entry = matrices.peek();
                    Matrix4f matrix4f = entry.getModel();
                    Matrix3f matrix3f = entry.getNormal();
                    Vector3f vector3f = matrix3f.decomposeLinearTransformation().getMiddle();

                    GlStateManager.pushMatrix();
                    //I hear I'm not supposed to use GlStateManager here, but I'm so tired of this that I don't care as long as it works
                    GlStateManager.disableCull();
                    GlStateManager.disableDepthTest();
                    GlStateManager.disableTexture();
                    GlStateManager.disableLighting();
                    GlStateManager.disableAlphaTest();

                    VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/blur_3.png")));

                    float offset = 1/8F; //20 Pixels (16+(1/8)*16*2) Fun Fact: this fits perfectly inside item frames
                    buffer.vertex(matrix4f, 0f-offset,1f+offset,0.5F).color(1f,1f,1f,1f).texture(0f,0f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                    buffer.vertex(matrix4f, 1f+offset,1f+offset,0.5F).color(1f,1f,1f,1f).texture(0f,1f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                    buffer.vertex(matrix4f, 1f+offset,0f-offset,0.5F).color(1f,1f,1f,1f).texture(1f,1f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                    buffer.vertex(matrix4f, 0f-offset,0f-offset,0.5F).color(1f,1f,1f,1f).texture(1f,0f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();

                    GlStateManager.enableCull();
                    GlStateManager.enableDepthTest();
                    GlStateManager.enableTexture();
                    GlStateManager.enableLighting();
                    GlStateManager.enableAlphaTest();
                    GlStateManager.popMatrix();

                    GlStateManager.disableDepthTest();
                    GlStateManager.bindTexture(GlStateManager.genTextures());
                }
                VertexConsumer baseVertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, baseLayer, true, stack.hasGlint());
                renderBakedItemModel(model, stack, light, overlay, matrices, baseVertexConsumer);
            } matrices.pop();
            info.cancel();
        }
    }
}
