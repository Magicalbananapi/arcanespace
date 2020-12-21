package io.github.magicalbananapie.arcanespace.item;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.block.ArcaneBlocks;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcaneItems {
    public static final Item ARCANE_TOKEN = new ArcaneToken();
    public static final Item GRAVITY_PANEL = new ItemGravityPanel(ArcaneBlocks.GRAVITY_PANEL);

    public static void registerItems() {
        register("gravity_token", ARCANE_TOKEN);
        register("gravity_panel", GRAVITY_PANEL);
    }
    //Return value kept just in case
    public static Item register(String name, Item item) {
        return register(ArcaneSpace.id(name), item);
    }

    public static Item register(Identifier id, Item item)
    {
        return Registry.register(Registry.ITEM, id, item);
    }

    public static Item.Settings defaults() {
        return new Item.Settings().group(ArcaneSpace.TOKEN_GROUP);
    }
}
