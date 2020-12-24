package io.github.magicalbananapie.arcanespace.item;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//TODO: Gravity focus block and singularity item need new handheld display rotations, while the singularity item also needs a model

public class SingularityItem extends Item {
    public SingularityItem() { super(new FabricItemSettings().group(ArcaneSpace.GRAVITY_GROUP).rarity(Rarity.RARE).maxCount(1)); }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("Work in progress...").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
        tooltip.add(new LiteralText("Model and Renderer").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
        tooltip.add(new LiteralText("to be implemented.").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
    }
}
