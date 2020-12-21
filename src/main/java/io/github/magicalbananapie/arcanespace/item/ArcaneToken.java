package io.github.magicalbananapie.arcanespace.item;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.Gravity;
import io.github.magicalbananapie.arcanespace.util.GravityEnum;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//NOTICE: THIS IS AN OLD PLACEHOLDER FOR WHAT WILL BE THE SINGULARITY/TESSERACT ITEM THAT WILL
// BE THE CORE OF MOST OF THE BIGGER FEATURES IN THE MOD :) IT DOES NOT WORK AND SHOULD NOT BE USED

//FIXME: THIS CLASS NEEDS TO BE REMOVED, THIS ENTIRE ITEM IS REDUNDANT

public class ArcaneToken extends Item {
    private static final ArcaneConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
    private boolean active;
    private GravityEnum storedGravity;

    public ArcaneToken() {
        super(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1));
        this.active = false;
        this.storedGravity = GravityEnum.get(config.gravityDirection); //Just in case (Say somebody edits nbt data or some similar voodoo)
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return active; }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("Don't use this...").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
        tooltip.add(new LiteralText("changes default gravity...").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        this.active=!this.active;
        Gravity gravity = ((EntityAccessor)user).getGravity();
        if(active) { //activated
            this.storedGravity = gravity.getDefaultDirection();
            gravity.updateDefaultDirection();
        } else { //deactivated
            gravity.setDefaultDirection(this.storedGravity);
            this.storedGravity = GravityEnum.get(config.gravityDirection); //Again, just in case
        } ((EntityAccessor)user).setGravity(user, gravity);
        return TypedActionResult.success(user.getStackInHand(hand), false);
    }

    //TODO: Implement Use, should be some sort of tool or currency
    // possible secret use if put on boots - idea: invert gravity each
    // time space is pressed without flipping the player model, hitbox, movement, and the like
    //NOTICE: Putting these on boots makes all gravity changes permanent while boots are worn :)

    //https://github.com/amusingimpala75/VanillaPlusArmor got to some vanilla functionality first,
    // Im still adding it but I can base some of the code off of that, although final implementation
    // will likely not be similar at all.
}