package io.github.magicalbananapie.arcanespace.mixin;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * @Author Magicalbananapie
 * This is one of the MANY, MANY steps towards making gravity bug-free
 */
public abstract class ServerPlayerInteractionManagerMixin {
    public void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight) {
        //double e = this.player.getY() - ((double) pos.getY() + 0.5D) + 1.5D; //1.5 needs to be the player Eye Height
    }
}
