package io.github.magicalbananapie.arcanespace.util;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class ArcaneTags {
    public static final Tag<Item> GRAVITY = TagRegistry.item(new Identifier("arcanespace", "gravity_items"));
}
