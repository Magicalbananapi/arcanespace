package io.github.magicalbananapie.arcanespace.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ArcaneCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            GravityCommand.register(dispatcher);
        });
    }
}
