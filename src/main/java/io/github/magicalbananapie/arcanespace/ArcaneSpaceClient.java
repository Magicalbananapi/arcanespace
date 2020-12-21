package io.github.magicalbananapie.arcanespace;

import io.github.magicalbananapie.arcanespace.block.ArcaneBlocks;
import io.github.magicalbananapie.arcanespace.blockentity.ArcaneBlockEntities;
import io.github.magicalbananapie.arcanespace.renderer.GravityFocusRenderer;
import io.github.magicalbananapie.arcanespace.renderer.SpatialItemRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArcaneSpaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ArcaneBlocks.GRAVITY_PANEL, RenderLayer.getTranslucent());
        BlockEntityRendererRegistry.INSTANCE.register(ArcaneBlockEntities.GRAVITY_FOCUS, GravityFocusRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ArcaneBlocks.GRAVITY_FOCUS, RenderLayer.getTranslucent()); //TODO: CHECK IF NEEDED
        //TODO: BuiltinItemRenderer is not a good idea for unique enchantments, this is used to make singular items with unique effects,
        // So I could make a magic wand with conditional special effects based on selected spells or something :)
        // For Unique Enchantment types or effects, mixins will likely be needed in the item rendering code
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry)
                -> registry.register(new Identifier(ArcaneSpace.MOD_ID,"entity/gravity_barrier")));
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry)
                -> registry.register(new Identifier(ArcaneSpace.MOD_ID,"entity/gravity_core")));
        BuiltinItemRendererRegistry.INSTANCE.register(ArcaneBlocks.GRAVITY_FOCUS.asItem(), new SpatialItemRenderer());
    }
}