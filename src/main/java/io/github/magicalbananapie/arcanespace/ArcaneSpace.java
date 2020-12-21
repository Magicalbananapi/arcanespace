package io.github.magicalbananapie.arcanespace;

import io.github.magicalbananapie.arcanespace.block.ArcaneBlocks;
import io.github.magicalbananapie.arcanespace.blockentity.ArcaneBlockEntities;
import io.github.magicalbananapie.arcanespace.command.ArcaneCommands;
import io.github.magicalbananapie.arcanespace.effect.ArcaneEffects;
import io.github.magicalbananapie.arcanespace.item.ArcaneItems;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

//TODO: DON'T ADD SPACE, INSTEAD ADD ASTROMINE COMPATIBILITY, THE SPACESUIT WILL BE LITERAL
// ALTERNATIVELY I CAN ADD SPACE, BUT I WOULD NEED TO MAKE IT UNIQUE IN SOME SIGNIFICANT WAY
//NOTICE: NV, I've decided to add an armor suit using my blockEntityRenderer kind of like Avaritia armor,
// it will be a reverse space suit :) (Stars inside the helmet)

public class ArcaneSpace implements ModInitializer {

    public static final String MOD_ID = "arcanespace";
    public static final String MOD_NAME = "Arcane Space";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME, StringFormatterMessageFactory.INSTANCE);

    public static final ItemGroup TOKEN_GROUP = FabricItemGroupBuilder.create(id("gravitytoken"))
            .icon(() -> new ItemStack(ArcaneItems.ARCANE_TOKEN)).build();

    public static boolean DEBUG_MODE;

    @Override
    public void onInitialize() {
        AutoConfig.register(ArcaneConfig.class, GsonConfigSerializer::new);
        DEBUG_MODE = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig().debugMode;

        ArcaneEffects.registerEffects();
        ArcaneBlocks.registerBlocks();
        ArcaneItems.registerItems();
        ArcaneBlockEntities.registerBlockEntities();
        ArcaneCommands.registerCommands();
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }

    @SuppressWarnings("unused") //Used frequently when testing
    public static void log(String message){
        LOGGER.log(Level.INFO, "[DEBUG]: "+message);
    }


    //NOTICE: THIS IS SPACE FOR FUN IDEAS I'VE HAD:
    // Achievement: I can't breathe!!! Put a gravity focus on your head, or a 'spacesuit-like' armor set's helmet
    // which will have the same effect, reason is that space is inside the helmet instead of outside,
    // possibly make the helmet just invert the environment, AKA in space the visual will be of the overworld sky
    // and... after trying it, I can say this is a bad idea as is, also, don't forget to add mixins to their file again

    //NOTICE: Ideas for effects of helmet:
    // 'By Stuffing your head into a pocket dimension, you are able to avoid the effects of nausea'
    // Possibly take advantage of the 'pocket dimension' effect of the helmet to change the player's head
    // renderer, make the head tiny :D ,UPDATE: Tried and did not look as good as I had hoped, maybe 0.8* scale later?
    // Helmet might change the sky and fog rendering :) UPDATE: Changing rendering inside helmet conditionally worked, but
    // was a disaster in more ways than one, I'd have to make a renderer that just happens to look like the sky again.

    //NOTICE: In the (extremely unlikely) event that I can figure out how to do it, free angular gravity might be implemented.
}