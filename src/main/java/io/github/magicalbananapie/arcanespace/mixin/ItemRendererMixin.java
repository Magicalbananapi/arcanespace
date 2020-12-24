package io.github.magicalbananapie.arcanespace.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.item.ArcaneItems;
import io.github.magicalbananapie.arcanespace.renderer.ArcaneSprites;
import io.github.magicalbananapie.arcanespace.renderer.GravityFocusItemRenderer;
import io.github.magicalbananapie.arcanespace.util.ArcaneTags;
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
import net.minecraft.tag.ItemTags;
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
        if(!stack.isEmpty()) {
            //NOTICE: This is an Avaritia-like item background, this was a pain to implement and took many, many revisions
            //TODO: This probably needs to be 24x24 instead of 20x20, also the itemRenderer needs to be added to the singularity
            if(stack.getItem().isIn(ArcaneTags.GRAVITY) && renderMode == ModelTransformation.Mode.GUI) {
                matrices.push();
                matrices.translate(-0.5D, -0.5D, -1.0D); //-0.5 z is in the center, but that clips into blocks
                MatrixStack.Entry entry = matrices.peek();
                Matrix4f matrix4f = entry.getModel();
                Matrix3f matrix3f = entry.getNormal();
                Vector3f vector3f = matrix3f.decomposeLinearTransformation().getMiddle();

                RenderSystem.pushMatrix();
                //I hear I'm not supposed to use GlStateManager here, but I'm so tired of this that I don't care as long as it works
                RenderSystem.disableCull();
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableLighting();
                RenderSystem.disableAlphaTest();

                VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(new Identifier(ArcaneSpace.MOD_ID,"textures/gui/effect/blur.png")));

                float offset = 1/4F; //24 Pixels (16+(1/4)*16*2) Fun Fact: this fits perfectly inside item frames
                buffer.vertex(matrix4f, 0f-offset,1f+offset,0.5F).color(1f,1f,1f,1f).texture(0f,0f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                buffer.vertex(matrix4f, 1f+offset,1f+offset,0.5F).color(1f,1f,1f,1f).texture(0f,1f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                buffer.vertex(matrix4f, 1f+offset,0f-offset,0.5F).color(1f,1f,1f,1f).texture(1f,1f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();
                buffer.vertex(matrix4f, 0f-offset,0f-offset,0.5F).color(1f,1f,1f,1f).texture(1f,0f).overlay(overlay).light(light).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).next();

                RenderSystem.enableCull();
                RenderSystem.enableDepthTest();
                RenderSystem.enableTexture();
                RenderSystem.enableLighting();
                RenderSystem.enableAlphaTest();
                RenderSystem.popMatrix();

                RenderSystem.disableDepthTest();
                RenderSystem.bindTexture(GL11.glGenTextures());
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
