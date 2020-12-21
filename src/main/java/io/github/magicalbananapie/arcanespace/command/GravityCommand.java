package io.github.magicalbananapie.arcanespace.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.util.EntityAccessor;
import io.github.magicalbananapie.arcanespace.util.GravityEnum;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;

import java.util.Collection;
import java.util.Collections;

//TODO: Clean up code a bit and merge some methods, remove redundancies, etc. However low priority as it works.
public class GravityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literal = CommandManager.literal("gravity")
                .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2));

        //NOTICE: This command will be redundant whenever I get gravity stored in entity nbt data correctly
        if(ArcaneSpace.DEBUG_MODE) { //Adds a technically redundant command conditionally :)
            literal.executes((ctx) -> execute(ctx, ctx.getSource().getPlayer()))
                    .then(CommandManager.argument("target", EntityArgumentType.entities())
                            .executes((ctx) -> execute(ctx, EntityArgumentType.getEntities(ctx, "target"))));
        }

        literal.then(CommandManager.literal("strength")
                .then(CommandManager.argument("magnitude", FloatArgumentType.floatArg())
                        .executes((ctx) -> execute(ctx, Collections.singleton(ctx.getSource().getPlayer()), FloatArgumentType.getFloat(ctx, "magnitude")))
                .then(CommandManager.argument("target", EntityArgumentType.entities())
                        .executes((ctx) -> execute(ctx, EntityArgumentType.getEntities(ctx, "target"), FloatArgumentType.getFloat(ctx, "magnitude"))))));

        for (GravityEnum gravityDirection : GravityEnum.values()) {
            literal.then(CommandManager.literal("direction")
                    .then(CommandManager.literal(gravityDirection.getName())
                            .executes((ctx) -> execute(ctx, Collections.singleton(ctx.getSource().getEntity()), gravityDirection))
                    .then(CommandManager.argument("target", EntityArgumentType.entities())
                            .executes((ctx) -> execute(ctx, EntityArgumentType.getEntities(ctx, "target"), gravityDirection))
                    .then(CommandManager.argument("length", IntegerArgumentType.integer(0))
                            .executes((ctx) -> execute(ctx, EntityArgumentType.getEntities(ctx, "target"), gravityDirection, IntegerArgumentType.getInteger(ctx, "length")))))));
        } dispatcher.register(literal);
    }

    private static int execute(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        //Display gravity data in chat, this is WIP because I cant figure out how this should work
        Text text = new TranslatableText("gravity");
        if (context.getSource().getEntity() != player && context.getSource().getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK))
            player.sendSystemMessage(new TranslatableText("gravity.checked", text), Util.NIL_UUID);
        context.getSource().sendFeedback(new LiteralText(
                "Default Gravity: "+((EntityAccessor)player).getGravity().getDefaultDirection().getName()+
                ", Temporary Gravity: "+((EntityAccessor)player).getGravity().getGravityDirection().getName()+
                ", G-Strength: "+((EntityAccessor)player).getGravity().getGravityStrength()), true);
        return 1;
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets) {
        //Set target gravity direction permanently
        int i = 0;
        for (Entity entity : targets) {
            context.getSource().sendFeedback(new LiteralText(
                    "Default Gravity: "+((EntityAccessor)entity).getGravity().getDefaultDirection().getName()+
                    ", Temporary Gravity: "+((EntityAccessor)entity).getGravity().getGravityDirection().getName()+
                    ", G-Strength: "+((EntityAccessor)entity).getGravity().getGravityStrength()), true);
            ++i;
        } return i;
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets, GravityEnum gravityDirection) {
        //Set target gravity direction permanently
        int i = 0;
        for (Entity entity : targets) {
            ((EntityAccessor)entity).setGravityDirection(entity, gravityDirection);
            //Below is command 'feedback' in chat
            Text text = gravityDirection.getTranslatableName();
            if(entity instanceof ServerPlayerEntity) {
                if (context.getSource().getEntity() == entity) {
                    context.getSource().sendFeedback(new TranslatableText("commands.gravity.direction.success.self", text), true);
                } else {
                    if (context.getSource().getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                        entity.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
                    } context.getSource().sendFeedback(new TranslatableText("commands.gravity.direction.success.other", entity.getDisplayName(), text), true);
                }
            } ++i;
        } return i;
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets, GravityEnum gravityDirection, int tickLength) {
        //Set target gravity direction temporarily
        int i = 0;
        for (Entity entity : targets) {
            ((EntityAccessor)entity).setTemporaryDirection(entity, gravityDirection, tickLength);
            //Below is command 'feedback' in chat
            Text text = gravityDirection.getTranslatableName();
            if(entity instanceof ServerPlayerEntity) {
                if (context.getSource().getEntity() == entity) {
                    context.getSource().sendFeedback(new TranslatableText("commands.gravity.direction.success.self", text), true);
                } else {
                    if (context.getSource().getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                        entity.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
                    } context.getSource().sendFeedback(new TranslatableText("commands.gravity.direction.success.other", entity.getDisplayName(), text), true);
                }
            } ++i;
        } return i;
    }

    private static int execute(CommandContext<ServerCommandSource> context, Collection<? extends Entity> targets, float magnitude) {
        //Set target gravity strength
        int i = 0;
        for (Entity entity : targets) {
            ((EntityAccessor)entity).setGravityStrength(entity, magnitude);
            //Below is command 'feedback' in chat
            Text text = new TranslatableText("gravity.strength");
            if(entity instanceof ServerPlayerEntity) {
                if (context.getSource().getEntity() == entity) {
                    context.getSource().sendFeedback(new TranslatableText("commands.gravity.strength.success.self", text), true);
                } else {
                    if (context.getSource().getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
                        entity.sendSystemMessage(new TranslatableText("gameMode.changed", text), Util.NIL_UUID);
                    } context.getSource().sendFeedback(new TranslatableText("commands.gravity.strength.success.other", entity.getDisplayName(), text), true);
                }
            } ++i;
        } return i;
    }
}
