package io.github.magicalbananapie.arcanespace.renderer;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class ArcaneSprites {
    public static final Identifier BARRIER_PATH = new Identifier(ArcaneSpace.MOD_ID,"entity/gravity_barrier");
    public static final Identifier CORE_PATH = new Identifier(ArcaneSpace.MOD_ID,"entity/gravity_core");

    public static void registerSprites() {
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                .register((atlasTexture, registry) -> registry.register(BARRIER_PATH));
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                .register((atlasTexture, registry) -> registry.register(CORE_PATH));
    }
}
