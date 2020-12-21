package io.github.magicalbananapie.arcanespace.util;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.entity.damage.DamageSource;

import java.util.Arrays;

public class ArcaneDamageSource extends DamageSource {
    public static final DamageSource ASPHYXIATION = new ArcaneDamageSource("asphyxiation").setBypassesArmor();
    public static DamageSource BLOOD_BOIL = new ArcaneDamageSource("bloodboil").setBypassesArmor().setUnblockable().setOutOfWorld();
    public static DamageSource BLOOD_BOIL_S = new ArcaneDamageSource("bloodboil").setBypassesArmor().setUnblockable();
    public static DamageSource OUTERSPACE = new ArcaneDamageSource("outerspace").setBypassesArmor().setUnblockable().setOutOfWorld();
    public static DamageSource OUTERSPACE_S = new ArcaneDamageSource("outerspace").setBypassesArmor().setUnblockable();
    //TODO: I want to make a space dimension later, maybe a void one too, but after space
    //TODO: space update will likely be for 1.17, as it would use the freezing mechanic
    //TODO: and tinted glass for crafting helmet, among other things...

    //TODO: The freezing mechanic needs A LOT of polish, also in 1.17 maybe tied to high level freezing effect

    //TODO: While I'm writing a million TODOs, I should note that I think I can rewrite some of the mixin features
    //TODO: to use server and client tickevent, not that I know if that would be better or not

    public ArcaneDamageSource(String name) {
        super(name);
    }

    @Override
    public ArcaneDamageSource setBypassesArmor() { return (ArcaneDamageSource)super.setBypassesArmor(); }

    @Override
    public ArcaneDamageSource setUnblockable() {
        return (ArcaneDamageSource)super.setUnblockable();
    }

    @Override
    public ArcaneDamageSource setOutOfWorld() {
        return (ArcaneDamageSource)super.setOutOfWorld();
    }
}
