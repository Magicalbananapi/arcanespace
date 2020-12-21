package io.github.magicalbananapie.arcanespace.blockentity;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class BlockEntityGravityFocus extends BlockEntity implements Tickable { //NOTICE: Tickable due to G-Field checks
    private int gFieldSize;

    public BlockEntityGravityFocus() {
        super(ArcaneBlockEntities.GRAVITY_FOCUS);
        ArcaneConfig settings = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
        this.gFieldSize = settings.gFieldSize;

        //NOTICE: WARNING, INFO-DUMP INCOMING, I REPEAT, INFO-DUMP INCOMING
        //this.gFieldStrength = settings.gFieldStrength; TODO: Make G-Field Strength have a tick length, etc. and
        // TODO: make it such that g-foci vary in g-strength 'Middle' of planet size is 'glued' to 'End' of g-field size

        //Todo: consider this being an upgraded version of another gravity foci block that does not have the 'casing'
        // covering it, which would mean you don't stop getting pulled in and get Destroyed like in the void.
        // If this is the case, the 'casing' needs to be reworked slightly to have special functionality when you
        // step on the bottom of it, pushing you away from it, this would be most consistent, and therefore the
        // semitransparent end portal/gateway overlay needs to appear on top instead of the bottom of it, unless
        // I am to make the effect occur on the bottom of the panel and on the outside of the foci block slightly

        //Notice: The decision from the above is that Gravity Foci are upgraded gravity cores, or singularities,
        // which are also used for portal and other spatial devices, and is crafted from 6 gravity panels surrounding
        // a singularity/tesseract/whateverIdecideOnCallingTheCore and two other items or through a special crafting
        // method. Use structure void and conduit for help on making singularity, and the gravity panels need to be
        // reworked to act differently when interacting with the bottom, while also making them solid, to think of
        // themselves as a roof, and only change gravity strength to negative temporarily instead of direction.
        // Effect will surround the singularity always, while also appearing more transparently on the bottom of
        // panels and therefore the outside of the core block.

        //TODO: Decide if the Gravity focus block should also count as a special crafting block that compresses things
        //NOTICE: YOU ARE NOW SAFE, INFO-DUMP HAS ENDED, I REPEAT, INFO-DUMP HAS ENDED
    }

    //NOTICE: Functionality for blockEntity each tick, Conduit Block Entity provides the perfect example for this :)
    @Override
    public void tick() {

    }
}
