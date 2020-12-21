package io.github.magicalbananapie.arcanespace.item;

import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemGravityPanel extends BlockItem {

    public ItemGravityPanel(Block block) {
        super(block ,new FabricItemSettings().group(ArcaneSpace.TOKEN_GROUP).rarity(Rarity.UNCOMMON));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("Can be placed").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GRAY)));
    }
}
