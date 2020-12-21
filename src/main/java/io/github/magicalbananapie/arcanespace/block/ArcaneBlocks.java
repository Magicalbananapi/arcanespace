package io.github.magicalbananapie.arcanespace.block;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.item.ArcaneItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ArcaneBlocks {

    public static final Block GRAVITY_PANEL = new BlockGravityPanel();
    public static final Block GRAVITY_FOCUS = new BlockGravityFocus();

    public static void registerBlocks() {
        registerItemless("gravity_panel", GRAVITY_PANEL);
        register("gravity_focus", GRAVITY_FOCUS);
    }
    //Return value kept just in case
    public static Block register(String name, Block block) {
        ArcaneItems.register(name, new BlockItem(block, ArcaneItems.defaults().rarity(Rarity.RARE)));
        return registerItemless(name, block);
    }

    public static Block registerItemless(String name, Block block) { return registerItemless(ArcaneSpace.id(name), block); }

    public static Block registerItemless(Identifier id, Block block) { return Registry.register(Registry.BLOCK, id, block); }
}