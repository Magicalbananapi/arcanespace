package io.github.magicalbananapie.arcanespace.renderer;

import io.github.magicalbananapie.arcanespace.blockentity.BlockEntityGravityFocus;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class GravityFocusItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final SpriteIdentifier BARRIER_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ArcaneSprites.BARRIER_PATH);
    private final ModelPart barrier = new ModelPart(64, 32, 0, 0);
    private final BlockEntity entity = new BlockEntityGravityFocus();

    public GravityFocusItemRenderer() {
        this.barrier.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F);
    }

    @Override
    public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        BlockEntityRenderDispatcher.INSTANCE.renderEntity(entity, matrices, vertexConsumers, light, overlay);
        this.barrier.render(matrices, BARRIER_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucent), light, overlay);
        matrices.pop();
    }
}
//NOTICE: Magical(🍌+π);